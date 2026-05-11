# CHƯƠNG 2: PHÂN TÍCH THIẾT KẾ HỆ THỐNG

## 2.1. Xác định các đối tượng người dùng

Hệ thống Voucher-Service được xây dựng phục vụ quy trình phân phối voucher với các đối tượng người dùng sau:

### 2.1.1. Maker (Người tạo)

- **Vai trò:** Tạo yêu cầu phát hành voucher và nhiệm vụ (mission).
- **Quyền hạn:**
  - Tạo voucher đơn lẻ (nhập thông tin trực tiếp).
  - Tạo voucher hàng loạt (upload file Excel).
  - Tạo nhiệm vụ (mission) kèm cấu hình phần thưởng.
  - Gửi yêu cầu phê duyệt (Submit).
  - Hủy yêu cầu khi chưa gửi duyệt (Cancel).
  - Xem danh sách và chi tiết voucher/mission đã tạo.

### 2.1.2. Checker (Người duyệt)

- **Vai trò:** Xem xét và phê duyệt hoặc từ chối các yêu cầu từ Maker/Partner.
- **Quyền hạn:**
  - Xem danh sách yêu cầu ở trạng thái chờ duyệt (PENDING_APPROVE).
  - Phê duyệt yêu cầu (APPROVED) → voucher được kích hoạt.
  - Từ chối yêu cầu (REJECTED) kèm lý do.
  - Xem audit log các thao tác trong hệ thống.
  - Chỉ thấy các yêu cầu ở trạng thái: PENDING_APPROVE, APPROVED, REJECTED, FINISH.

### 2.1.3. Partner (Đối tác)

- **Vai trò:** Nhà cung cấp dịch vụ/cửa hàng tạo voucher và nhiệm vụ riêng cho thương hiệu.
- **Quyền hạn:**
  - Tạo voucher cho cửa hàng của mình (tự động gán storeName).
  - Tạo nhiệm vụ gắn với thương hiệu.
  - Gửi yêu cầu phê duyệt và hủy yêu cầu.
  - Chỉ xem được voucher/mission do mình tạo.
  - Voucher của Partner luôn có customerTier = ALL (áp dụng cho tất cả khách hàng).

### 2.1.4. Customer (Khách hàng)

- **Vai trò:** Người dùng cuối, sử dụng voucher và tham gia nhiệm vụ.
- **Quyền hạn (thông qua Customer-Service):**
  - Xem danh sách voucher khả dụng theo hạng thành viên.
  - Thu thập voucher vào kho cá nhân.
  - Sử dụng voucher khi thanh toán.
  - Tham gia nhiệm vụ và nhận thưởng khi hoàn thành.

---

## 2.2. Quy trình, luồng nghiệp vụ phân phối Voucher

### 2.2.1. Tổng quan luồng trạng thái Voucher

```
┌──────┐     Submit      ┌─────────────────┐     Approve     ┌──────────┐     Processing    ┌────────┐
│ INIT │ ──────────────► │ PENDING_APPROVE │ ──────────────► │ APPROVED │ ────────────────► │ FINISH │
└──────┘                 └─────────────────┘                 └──────────┘                   └────────┘
   │                            │                                                    
   │ Cancel                     │ Reject                                             
   ▼                            ▼                                                    
┌───────────┐            ┌──────────┐                                                
│ CANCELLED │            │ REJECTED │                                                
└───────────┘            └──────────┘                                                
```

### 2.2.2. Luồng tạo Voucher đơn lẻ (Single Mode)

**Actor:** Maker hoặc Partner

**Mô tả:** Người dùng nhập thông tin voucher trực tiếp qua form, hệ thống tạo một yêu cầu (VoucherRequest) và một chi tiết voucher (VoucherDetail) tương ứng.

**Các bước:**

1. Maker/Partner gửi request tạo voucher với thông tin: tên, mô tả, loại giảm giá (FIXED/PERCENT), giá trị giảm, giá trị đơn hàng tối thiểu, số lượng, thời hạn, hạng khách hàng áp dụng.
2. Hệ thống validate dữ liệu đầu vào:
   - Nếu là Partner: tự động gán customerTier = ALL, creatorType = PARTNER, lấy storeName qua gRPC từ Identity Service.
   - Nếu là System (Maker): yêu cầu phải có customerTier, creatorType = SYSTEM.
3. Hệ thống tạo requestId tự động (format: `VOUCHER_{timestamp}`).
4. Lưu VoucherRequest với status = INIT, requestMode = SINGLE.
5. Lưu VoucherDetail với requestStatus = INIT, voucherStatus = INACTIVE, availableStock = totalStock.
6. Mã voucher (voucherCode) được tự động sinh theo format: `VCH-{10 ký tự UUID}`.

### 2.2.3. Luồng tạo Voucher hàng loạt (Excel Mode)

**Actor:** Maker hoặc Partner

**Mô tả:** Người dùng upload file Excel chứa danh sách voucher, hệ thống đọc và tạo nhiều VoucherDetail từ một VoucherRequest.

**Các bước:**

1. Maker/Partner upload file Excel (.xlsx) kèm loại giảm giá (discountType) và requestId.
2. Hệ thống validate:
   - File không rỗng.
   - DiscountType hợp lệ.
   - RequestId chưa tồn tại với trạng thái active (INIT, PENDING_APPROVE, APPROVED, FINISH).
3. Đọc file Excel, parse từng dòng thành đối tượng CreateVoucherExcel.
4. Lưu VoucherRequest với status = DRAFT, requestMode = EXCEL.
5. Lưu danh sách VoucherDetail theo batch (100 records/batch) để tối ưu hiệu năng.
6. Mỗi VoucherDetail có requestStatus = INIT, voucherStatus = INACTIVE.

### 2.2.4. Luồng gửi phê duyệt (Submit)

**Actor:** Maker hoặc Partner

**Điều kiện tiên quyết:** Voucher request đang ở trạng thái INIT.

**Các bước:**

1. Maker/Partner chọn yêu cầu voucher cần gửi duyệt.
2. Hệ thống kiểm tra:
   - Request tồn tại và đang ở trạng thái INIT.
   - Request không phải loại REWARD (voucher thưởng từ mission không submit thủ công).
3. Cập nhật trạng thái: INIT → PENDING_APPROVE.
4. Ghi nhận người cập nhật (updatedBy).

### 2.2.5. Luồng phê duyệt (Confirm)

**Actor:** Checker

**Điều kiện tiên quyết:** Voucher request đang ở trạng thái PENDING_APPROVE.

**Trường hợp 1: Phê duyệt (APPROVED)**

1. Checker xem chi tiết yêu cầu và quyết định phê duyệt.
2. Hệ thống cập nhật VoucherRequest: status = APPROVED, ghi nhận confirmedBy, confirmedTime.
3. Xử lý VoucherDetail theo batch (100 records/batch):
   - Cập nhật requestStatus: INIT → PROCESSING.
   - Kích hoạt voucher (chuyển status sang ACTIVE, đặt thời hạn).
4. Sau khi xử lý xong tất cả batch → VoucherRequest chuyển sang FINISH.
5. Voucher sẵn sàng để khách hàng thu thập và sử dụng.

**Trường hợp 2: Từ chối (REJECTED)**

1. Checker xem chi tiết và quyết định từ chối, nhập lý do (bắt buộc).
2. Hệ thống cập nhật VoucherRequest: status = REJECTED, ghi nhận reason, confirmedBy, confirmedTime.
3. Xử lý VoucherDetail theo batch: cập nhật requestStatus: INIT → REJECTED.
4. Voucher không được kích hoạt, khách hàng không thể sử dụng.

### 2.2.6. Luồng hủy yêu cầu (Cancel)

**Actor:** Maker hoặc Partner

**Điều kiện tiên quyết:** Voucher request đang ở trạng thái INIT (chưa gửi duyệt).

**Các bước:**

1. Maker/Partner chọn hủy yêu cầu.
2. Hệ thống kiểm tra request đang ở trạng thái INIT và không phải loại REWARD.
3. Cập nhật trạng thái: INIT → CANCELLED.

### 2.2.7. Luồng tạo và phê duyệt Mission (Nhiệm vụ)

**Actor:** Maker hoặc Partner (tạo), Checker (duyệt)

**Mô tả:** Mission là nhiệm vụ gamification gắn liền với voucher thưởng. Khi tạo mission, hệ thống đồng thời tạo voucher REWARD tương ứng.

**Các bước:**

1. Maker/Partner gửi request tạo mission với thông tin:
   - Tên, mô tả nhiệm vụ.
   - Mục tiêu (targetValue) và loại phần thưởng (POINT hoặc VOUCHER).
   - Thời gian bắt đầu/kết thúc.
   - Nếu rewardType = VOUCHER: kèm thông tin voucher thưởng (tên, giảm giá, số lượng...).
2. Hệ thống validate:
   - Ngày bắt đầu không được ở quá khứ.
   - Ngày kết thúc phải sau ngày bắt đầu.
   - Nếu rewardType = POINT: rewardValue phải là số dương.
3. Tạo VoucherRequest + VoucherDetail với voucherPurpose = REWARD (nếu rewardType = VOUCHER).
4. Gọi gRPC đến Loyalty Service để tạo mission entity.
5. Luồng Submit/Confirm/Cancel tương tự voucher, nhưng đồng bộ trạng thái giữa Voucher Service và Loyalty Service qua gRPC.

---

## 2.3. Sơ đồ luồng hoạt động (Activity Diagram)

### 2.3.1. Luồng phân phối Voucher hoàn chỉnh

```
┌─────────────┐          ┌─────────────┐          ┌─────────────┐          ┌──────────┐
│ Maker/Partner│          │   System    │          │   Checker   │          │ Customer │
└──────┬──────┘          └──────┬──────┘          └──────┬──────┘          └────┬─────┘
       │                        │                        │                      │
       │  1. Tạo voucher        │                        │                      │
       │───────────────────────►│                        │                      │
       │                        │  Validate & Save       │                      │
       │                        │  (status=INIT)         │                      │
       │  ◄─────────────────────│                        │                      │
       │                        │                        │                      │
       │  2. Submit (gửi duyệt) │                        │                      │
       │───────────────────────►│                        │                      │
       │                        │  INIT→PENDING_APPROVE  │                      │
       │                        │───────────────────────►│                      │
       │                        │                        │                      │
       │                        │                        │  3. Review & Confirm │
       │                        │                        │─────────┐            │
       │                        │                        │         │            │
       │                        │                        │◄────────┘            │
       │                        │                        │                      │
       │                        │  4a. APPROVED          │                      │
       │                        │◄───────────────────────│                      │
       │                        │                        │                      │
       │                        │  Activate vouchers     │                      │
       │                        │  (INACTIVE→ACTIVE)     │                      │
       │                        │  (status=FINISH)       │                      │
       │                        │                        │                      │
       │                        │                        │    5. Xem & Collect  │
       │                        │◄───────────────────────────────────────────────│
       │                        │                        │                      │
       │                        │  Return voucher list   │                      │
       │                        │  (filtered by tier)    │                      │
       │                        │──────────────────────────────────────────────►│
       │                        │                        │                      │
       │                        │                        │    6. Sử dụng khi    │
       │                        │                        │       thanh toán     │
       │                        │◄───────────────────────────────────────────────│
       │                        │                        │                      │
       │                        │  Apply discount        │                      │
       │                        │  Update stock          │                      │
       │                        │  Earn points           │                      │
       │                        │──────────────────────────────────────────────►│
       │                        │                        │                      │
```

### 2.3.2. Luồng sử dụng Voucher của Customer

```
┌──────────┐          ┌────────────────┐          ┌────────────────┐
│ Customer │          │Customer Service│          │ Voucher Service│
└────┬─────┘          └───────┬────────┘          └───────┬────────┘
     │                        │                           │
     │ 1. Xem voucher        │                           │
     │    khả dụng           │                           │
     │───────────────────────►│                           │
     │                        │  gRPC: get vouchers      │
     │                        │  by customer tier        │
     │                        │──────────────────────────►│
     │                        │                           │
     │                        │  Return active vouchers  │
     │                        │◄──────────────────────────│
     │  Danh sách voucher    │                           │
     │◄───────────────────────│                           │
     │                        │                           │
     │ 2. Thu thập voucher   │                           │
     │───────────────────────►│                           │
     │                        │  Check: đã collect chưa? │
     │                        │  Check: còn stock?       │
     │                        │  Save CustomerVoucher    │
     │                        │──────────────────────────►│
     │                        │  Giảm availableStock     │
     │                        │◄──────────────────────────│
     │  Thành công            │                           │
     │◄───────────────────────│                           │
     │                        │                           │
     │ 3. Thanh toán         │                           │
     │    (chọn voucher)     │                           │
     │───────────────────────►│                           │
     │                        │  Validate voucher:       │
     │                        │  - Còn lượt dùng?       │
     │                        │  - Đơn hàng >= min?     │
     │                        │  - Còn hạn?             │
     │                        │                           │
     │                        │  Apply discount          │
     │                        │  Mark voucher USED       │
     │                        │                           │
     │                        │  Kafka: LoyaltyPointEvent│
     │                        │─────────────────────────►│
     │                        │  (tích điểm + update     │
     │                        │   mission progress)      │
     │  Kết quả thanh toán   │                           │
     │◄───────────────────────│                           │
     │                        │                           │
```

---

## 2.4. Mô tả chi tiết các trạng thái

### 2.4.1. Trạng thái Voucher Request (RequestStatus)

| Trạng thái | Mô tả | Chuyển từ | Chuyển đến | Actor |
|---|---|---|---|---|
| DRAFT | Bản nháp (chỉ dùng cho Excel mode) | - | INIT | System |
| INIT | Đã tạo, chờ gửi duyệt | DRAFT | PENDING_APPROVE, CANCELLED | Maker/Partner |
| PENDING_APPROVE | Đã gửi, chờ Checker duyệt | INIT | APPROVED, REJECTED | Checker |
| APPROVED | Đã được phê duyệt | PENDING_APPROVE | PROCESSING | Checker |
| REJECTED | Bị từ chối (kèm lý do) | PENDING_APPROVE | - | Checker |
| CANCELLED | Đã hủy bởi người tạo | INIT | - | Maker/Partner |
| PROCESSING | Đang xử lý kích hoạt voucher | APPROVED | FINISH, FAILED | System |
| FINISH | Hoàn tất, voucher đã active | PROCESSING | - | System |
| FAILED | Xử lý thất bại | PROCESSING | - | System |

### 2.4.2. Trạng thái Voucher Detail (VoucherStatus)

| Trạng thái | Mô tả | Điều kiện |
|---|---|---|
| INACTIVE | Chưa kích hoạt (mới tạo hoặc chưa được duyệt) | Mặc định khi tạo |
| ACTIVE | Đang hoạt động, khách hàng có thể thu thập | Sau khi request được APPROVED |
| EXPIRED | Đã hết hạn | Khi endDate < thời gian hiện tại |

### 2.4.3. Trạng thái Voucher của Customer (CustomerVoucherStatus)

| Trạng thái | Mô tả |
|---|---|
| AVAILABLE | Đã thu thập, có thể sử dụng |
| USED | Đã sử dụng hết lượt |
| EXPIRED | Đã hết hạn sử dụng |

---

## 2.5. Quy tắc nghiệp vụ quan trọng

### 2.5.1. Phân quyền truy cập dữ liệu

- **Partner** chỉ xem được voucher/mission do chính mình tạo (filter theo createdBy).
- **Checker** chỉ thấy các yêu cầu ở trạng thái: PENDING_APPROVE, APPROVED, REJECTED, FINISH.
- **Maker** thấy tất cả trạng thái của voucher do mình hoặc hệ thống tạo.

### 2.5.2. Quy tắc voucher theo hạng khách hàng

- Voucher của Partner luôn có customerTier = ALL (áp dụng cho mọi khách hàng).
- Voucher của System (Maker) có thể chỉ định hạng cụ thể: SILVER, GOLD, PLATINUM, DIAMOND, hoặc ALL.
- Khách hàng chỉ thấy voucher phù hợp với hạng của mình hoặc hạng ALL.

### 2.5.3. Quy tắc voucher REWARD vs HUNT

- **HUNT:** Voucher để khách hàng tự thu thập → phải qua quy trình Submit/Confirm.
- **REWARD:** Voucher thưởng từ mission → không được Submit/Cancel thủ công, chỉ được quản lý qua mission APIs.

### 2.5.4. Quy tắc xử lý batch

- Khi phê duyệt/từ chối, hệ thống xử lý VoucherDetail theo batch 100 records để tránh timeout và tối ưu bộ nhớ.
- Mỗi batch được log để theo dõi tiến trình xử lý.

### 2.5.5. Quy tắc thu thập và sử dụng voucher

- Mỗi khách hàng chỉ thu thập voucher tối đa số lần = maxCollect.
- Khi thu thập: giảm availableStock của VoucherDetail.
- Khi sử dụng: giảm availableUsage của CustomerVoucher, nếu = 0 → status = USED.
- Voucher chỉ áp dụng được khi: orderAmount >= minOrderValue, voucher còn hạn, còn lượt dùng.
