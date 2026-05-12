CHƯƠNG 2: PHÂN TÍCH THIẾT KẾ HỆ THỐNG

2.1. Xác định các đối tượng người dùng

Hệ thống Voucher-Service được xây dựng phục vụ quy trình phân phối voucher với các đối tượng người dùng sau:

2.1.1. Maker (Người tạo)

Vai trò: Tạo yêu cầu phát hành voucher và nhiệm vụ (mission).

Quyền hạn:
- Tạo voucher đơn lẻ (nhập thông tin trực tiếp).
- Tạo voucher hàng loạt (upload file Excel).
- Tạo nhiệm vụ (mission) kèm cấu hình phần thưởng.
- Gửi yêu cầu phê duyệt (Submit).
- Hủy yêu cầu khi chưa gửi duyệt (Cancel).
- Xem danh sách và chi tiết voucher/mission đã tạo.

2.1.2. Checker (Người duyệt)

Vai trò: Xem xét và phê duyệt hoặc từ chối các yêu cầu từ Maker/Partner.

Quyền hạn:
- Xem danh sách yêu cầu ở trạng thái chờ duyệt (PENDING_APPROVE).
- Phê duyệt yêu cầu (APPROVED) giúp voucher được kích hoạt.
- Từ chối yêu cầu (REJECTED) kèm lý do.
- Xem audit log các thao tác trong hệ thống.
- Chỉ thấy các yêu cầu ở trạng thái: PENDING_APPROVE, APPROVED, REJECTED, FINISH.

2.1.3. Partner (Đối tác)

Vai trò: Nhà cung cấp dịch vụ/cửa hàng tạo voucher và nhiệm vụ riêng cho thương hiệu.

Quyền hạn:
- Tạo voucher cho cửa hàng của mình (tự động gán storeName).
- Tạo nhiệm vụ gắn với thương hiệu.
- Gửi yêu cầu phê duyệt và hủy yêu cầu.
- Chỉ xem được voucher/mission do mình tạo.
- Voucher của Partner luôn có customerTier = ALL (áp dụng cho tất cả khách hàng).

2.1.4. Customer (Khách hàng)

Vai trò: Người dùng cuối, sử dụng voucher và tham gia nhiệm vụ.

Quyền hạn (thông qua Customer-Service):
- Xem danh sách voucher khả dụng theo hạng thành viên.
- Thu thập voucher vào kho cá nhân.
- Sử dụng voucher khi thanh toán.
- Tham gia nhiệm vụ và nhận thưởng khi hoàn thành.


2.2. Quy trình, luồng nghiệp vụ phân phối Voucher

2.2.1. Tổng quan luồng trạng thái Voucher

Hình 2.1: Sơ đồ chuyển trạng thái của Voucher Request

INIT --> (Submit) --> PENDING_APPROVE --> (Approve) --> APPROVED --> (Processing) --> FINISH
INIT --> (Cancel) --> CANCELLED
PENDING_APPROVE --> (Reject) --> REJECTED

2.2.2. Luồng tạo Voucher đơn lẻ (Single Mode)

Actor: Maker hoặc Partner.

Mô tả: Người dùng nhập thông tin voucher trực tiếp qua form, hệ thống tạo một yêu cầu (VoucherRequest) và một chi tiết voucher (VoucherDetail) tương ứng.

Các bước thực hiện:

Bước 1: Maker/Partner gửi request tạo voucher với thông tin: tên, mô tả, loại giảm giá (FIXED/PERCENT), giá trị giảm, giá trị đơn hàng tối thiểu, số lượng, thời hạn, hạng khách hàng áp dụng.

Bước 2: Hệ thống validate dữ liệu đầu vào. Nếu là Partner: tự động gán customerTier = ALL, creatorType = PARTNER, lấy storeName qua gRPC từ Identity Service. Nếu là System (Maker): yêu cầu phải có customerTier, creatorType = SYSTEM.

Bước 3: Hệ thống tạo requestId tự động theo format VOUCHER_{timestamp}.

Bước 4: Lưu VoucherRequest với status = INIT, requestMode = SINGLE.

Bước 5: Lưu VoucherDetail với requestStatus = INIT, voucherStatus = INACTIVE, availableStock = totalStock.

Bước 6: Mã voucher (voucherCode) được tự động sinh theo format VCH-{10 ký tự UUID}.

2.2.3. Luồng tạo Voucher hàng loạt (Excel Mode)

Actor: Maker hoặc Partner.

Mô tả: Người dùng upload file Excel chứa danh sách voucher, hệ thống đọc và tạo nhiều VoucherDetail từ một VoucherRequest.

Các bước thực hiện:

Bước 1: Maker/Partner upload file Excel (.xlsx) kèm loại giảm giá (discountType) và requestId.

Bước 2: Hệ thống validate: file không rỗng, discountType hợp lệ, requestId chưa tồn tại với trạng thái active (INIT, PENDING_APPROVE, APPROVED, FINISH).

Bước 3: Đọc file Excel, parse từng dòng thành đối tượng CreateVoucherExcel.

Bước 4: Lưu VoucherRequest với status = DRAFT, requestMode = EXCEL.

Bước 5: Lưu danh sách VoucherDetail theo batch (100 records/batch) để tối ưu hiệu năng.

Bước 6: Mỗi VoucherDetail có requestStatus = INIT, voucherStatus = INACTIVE.

2.2.4. Luồng gửi phê duyệt (Submit)

Actor: Maker hoặc Partner.

Điều kiện tiên quyết: Voucher request đang ở trạng thái INIT.

Các bước thực hiện:

Bước 1: Maker/Partner chọn yêu cầu voucher cần gửi duyệt.

Bước 2: Hệ thống kiểm tra request tồn tại và đang ở trạng thái INIT, đồng thời request không phải loại REWARD (voucher thưởng từ mission không submit thủ công).

Bước 3: Cập nhật trạng thái INIT sang PENDING_APPROVE.

Bước 4: Ghi nhận người cập nhật (updatedBy).

2.2.5. Luồng phê duyệt (Confirm)

Actor: Checker.

Điều kiện tiên quyết: Voucher request đang ở trạng thái PENDING_APPROVE.

Trường hợp 1 - Phê duyệt (APPROVED):

Bước 1: Checker xem chi tiết yêu cầu và quyết định phê duyệt.

Bước 2: Hệ thống cập nhật VoucherRequest: status = APPROVED, ghi nhận confirmedBy và confirmedTime.

Bước 3: Xử lý VoucherDetail theo batch (100 records/batch): cập nhật requestStatus từ INIT sang PROCESSING, kích hoạt voucher (chuyển status sang ACTIVE).

Bước 4: Sau khi xử lý xong tất cả batch, VoucherRequest chuyển sang FINISH.

Bước 5: Voucher sẵn sàng để khách hàng thu thập và sử dụng.

Trường hợp 2 - Từ chối (REJECTED):

Bước 1: Checker xem chi tiết và quyết định từ chối, nhập lý do (bắt buộc).

Bước 2: Hệ thống cập nhật VoucherRequest: status = REJECTED, ghi nhận reason, confirmedBy, confirmedTime.

Bước 3: Xử lý VoucherDetail theo batch: cập nhật requestStatus từ INIT sang REJECTED.

Bước 4: Voucher không được kích hoạt, khách hàng không thể sử dụng.

2.2.6. Luồng hủy yêu cầu (Cancel)

Actor: Maker hoặc Partner.

Điều kiện tiên quyết: Voucher request đang ở trạng thái INIT (chưa gửi duyệt).

Các bước thực hiện:

Bước 1: Maker/Partner chọn hủy yêu cầu.

Bước 2: Hệ thống kiểm tra request đang ở trạng thái INIT và không phải loại REWARD.

Bước 3: Cập nhật trạng thái từ INIT sang CANCELLED.

2.2.7. Luồng tạo và phê duyệt Mission (Nhiệm vụ)

Actor: Maker hoặc Partner (tạo), Checker (duyệt).

Mô tả: Mission là nhiệm vụ gamification gắn liền với voucher thưởng. Khi tạo mission, hệ thống đồng thời tạo voucher REWARD tương ứng.

Các bước thực hiện:

Bước 1: Maker/Partner gửi request tạo mission với thông tin: tên, mô tả nhiệm vụ, mục tiêu (targetValue), loại phần thưởng (POINT hoặc VOUCHER), thời gian bắt đầu/kết thúc. Nếu rewardType = VOUCHER thì kèm thông tin voucher thưởng (tên, giảm giá, số lượng).

Bước 2: Hệ thống validate: ngày bắt đầu không được ở quá khứ, ngày kết thúc phải sau ngày bắt đầu, nếu rewardType = POINT thì rewardValue phải là số dương.

Bước 3: Tạo VoucherRequest + VoucherDetail với voucherPurpose = REWARD (nếu rewardType = VOUCHER).

Bước 4: Gọi gRPC đến Loyalty Service để tạo mission entity.

Bước 5: Luồng Submit/Confirm/Cancel tương tự voucher, nhưng đồng bộ trạng thái giữa Voucher Service và Loyalty Service qua gRPC.


2.3. Sơ đồ Use Case

(Xem file: docs/usecase-diagram.xml - Import vào draw.io để xem sơ đồ)

Hệ thống có 4 actor chính tương tác với các use case:
- Maker: Tạo voucher, tạo voucher Excel, tạo mission, submit, cancel, xem danh sách.
- Checker: Phê duyệt voucher, từ chối voucher, phê duyệt mission, xem audit log.
- Partner: Tương tự Maker nhưng giới hạn trong phạm vi cửa hàng của mình.
- Customer: Xem voucher khả dụng, thu thập voucher, sử dụng voucher khi thanh toán, tham gia nhiệm vụ và nhận thưởng.

2.4. Sơ đồ Activity Diagram

(Xem file: docs/activity-diagram.xml và docs/activity-mission-diagram.xml - Import vào draw.io để xem sơ đồ)

Sơ đồ Activity mô tả luồng hoạt động chính của hệ thống theo swimlane cho từng actor:
- Luồng phân phối Voucher: Maker/Partner tạo → System validate và lưu → Maker submit → System chuyển trạng thái → Checker duyệt/từ chối → System kích hoạt hoặc reject.
- Luồng quản lý Mission: Maker/Partner tạo → Voucher Service validate và lưu voucher REWARD → Loyalty Service tạo mission qua gRPC → Maker submit → Checker duyệt → System kích hoạt mission + voucher thưởng.

2.5. Sơ đồ Sequence Diagram

(Xem file: docs/sequence-diagram.xml và docs/sequence-mission-diagram.xml - Import vào draw.io để xem sơ đồ)

Sơ đồ Sequence mô tả chi tiết tương tác giữa các thành phần theo thời gian:
- Sequence Voucher: Maker → VoucherController → VoucherService → IdentityService (gRPC) → Database, sau đó Checker → confirm → batch activate.
- Sequence Mission: Maker → MissionController → MissionService → VoucherService (tạo voucher REWARD) → LoyaltyService (gRPC: createMission) → Database.

2.6. Sơ đồ Class Diagram

(Xem file: docs/class-diagram-identity.xml, docs/class-diagram-voucher.xml, docs/class-diagram-loyalty.xml, docs/class-diagram-customer.xml - Import vào draw.io để xem sơ đồ)

Class Diagram được tách riêng cho từng service:
- Identity Service: User, Partner, các enum Role và PartnerCategory.
- Voucher Service: VoucherRequestEntity, VoucherDetailEntity, AuditLogEntity, MockInvoiceEntity, các enum RequestStatus, DiscountType, VoucherStatus, CustomerTier, VoucherPurpose, CreatorType, RequestMode.
- Loyalty Service: MissionEntity, CustomerEntity, các enum TargetType, RewardType, TaskStatus, MissionStatus.
- Customer Service: CustomerProfile, CustomerVoucher, CustomerMission, Transaction, các enum CustomerVoucherStatus, CustomerMissionStatus, TransactionStatus, và các Kafka Event DTO.

2.7. Mô tả chi tiết các trạng thái

2.7.1. Trạng thái Voucher Request (RequestStatus)

Bảng 2.1: Bảng trạng thái của Voucher Request

| STT | Trạng thái | Mô tả | Chuyển từ | Chuyển đến | Actor |
|---|---|---|---|---|---|
| 1 | DRAFT | Bản nháp (chỉ dùng cho Excel mode) | - | INIT | System |
| 2 | INIT | Đã tạo, chờ gửi duyệt | DRAFT | PENDING_APPROVE, CANCELLED | Maker/Partner |
| 3 | PENDING_APPROVE | Đã gửi, chờ Checker duyệt | INIT | APPROVED, REJECTED | Checker |
| 4 | APPROVED | Đã được phê duyệt | PENDING_APPROVE | PROCESSING | Checker |
| 5 | REJECTED | Bị từ chối (kèm lý do) | PENDING_APPROVE | - | Checker |
| 6 | CANCELLED | Đã hủy bởi người tạo | INIT | - | Maker/Partner |
| 7 | PROCESSING | Đang xử lý kích hoạt voucher | APPROVED | FINISH, FAILED | System |
| 8 | FINISH | Hoàn tất, voucher đã active | PROCESSING | - | System |
| 9 | FAILED | Xử lý thất bại | PROCESSING | - | System |

2.7.2. Trạng thái Voucher Detail (VoucherStatus)

Bảng 2.2: Bảng trạng thái của Voucher Detail

| STT | Trạng thái | Mô tả | Điều kiện |
|---|---|---|---|
| 1 | INACTIVE | Chưa kích hoạt (mới tạo hoặc chưa được duyệt) | Mặc định khi tạo |
| 2 | ACTIVE | Đang hoạt động, khách hàng có thể thu thập | Sau khi request được APPROVED |
| 3 | EXPIRED | Đã hết hạn | Khi endDate nhỏ hơn thời gian hiện tại |

2.7.3. Trạng thái Voucher của Customer (CustomerVoucherStatus)

Bảng 2.3: Bảng trạng thái voucher trong kho khách hàng

| STT | Trạng thái | Mô tả |
|---|---|---|
| 1 | AVAILABLE | Đã thu thập, có thể sử dụng |
| 2 | USED | Đã sử dụng hết lượt |
| 3 | EXPIRED | Đã hết hạn sử dụng |

2.8. Quy tắc nghiệp vụ quan trọng

2.8.1. Phân quyền truy cập dữ liệu

- Partner chỉ xem được voucher/mission do chính mình tạo (hệ thống filter theo createdBy và storeName).
- Checker chỉ thấy các yêu cầu ở trạng thái: PENDING_APPROVE, APPROVED, REJECTED, FINISH.
- Maker thấy tất cả trạng thái của voucher do mình hoặc hệ thống tạo.

2.8.2. Quy tắc voucher theo hạng khách hàng

- Voucher của Partner luôn có customerTier = ALL (áp dụng cho mọi khách hàng).
- Voucher của System (Maker) có thể chỉ định hạng cụ thể: SILVER, GOLD, PLATINUM, DIAMOND, hoặc ALL.
- Khách hàng chỉ thấy voucher phù hợp với hạng của mình hoặc hạng ALL.

2.8.3. Quy tắc voucher REWARD và HUNT

- HUNT: Voucher để khách hàng tự thu thập, phải qua quy trình Submit/Confirm thủ công.
- REWARD: Voucher thưởng từ mission, không được Submit/Cancel thủ công, chỉ được quản lý qua mission APIs.

2.8.4. Quy tắc xử lý batch

- Khi phê duyệt hoặc từ chối, hệ thống xử lý VoucherDetail theo batch 100 records để tránh timeout và tối ưu bộ nhớ.
- Mỗi batch được ghi log để theo dõi tiến trình xử lý.

2.8.5. Quy tắc thu thập và sử dụng voucher

- Mỗi khách hàng chỉ thu thập voucher tối đa số lần bằng maxCollect.
- Khi thu thập: giảm availableStock của VoucherDetail.
- Khi sử dụng: giảm availableUsage của CustomerVoucher, nếu bằng 0 thì chuyển status = USED.
- Voucher chỉ áp dụng được khi: orderAmount lớn hơn hoặc bằng minOrderValue, voucher còn hạn, và còn lượt dùng.

2.9. Kết luận chương

Chương 2 đã trình bày chi tiết phân tích thiết kế hệ thống bao gồm: xác định 4 đối tượng người dùng chính (Maker, Checker, Partner, Customer), mô tả đầy đủ các luồng nghiệp vụ phân phối voucher và quản lý mission, thiết kế các sơ đồ UML (Use Case, Activity, Sequence, Class Diagram), định nghĩa các trạng thái và quy tắc nghiệp vụ. Kết quả phân tích thiết kế này là cơ sở để triển khai xây dựng hệ thống ở Chương 3.
