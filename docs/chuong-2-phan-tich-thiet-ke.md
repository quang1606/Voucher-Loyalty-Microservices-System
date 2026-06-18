# CHƯƠNG 2. PHÂN TÍCH VÀ THIẾT KẾ HỆ THỐNG

## 2.1. Phân tích yêu cầu

### 2.1.1. Định hướng phân tích

Hệ thống được phân tích dựa trên kết quả khảo sát nghiệp vụ ở Chương 1, hướng đến xây dựng một nền tảng quản lý voucher và chương trình khách hàng thân thiết theo kiến trúc Microservices. Hệ thống cần đáp ứng:

- Quy trình phê duyệt nhiều bước (Maker/Checker) cho việc phát hành voucher và nhiệm vụ.
- Tích hợp đa vai trò: Admin, Maker, Checker, Partner, Customer.
- Giao tiếp giữa các service qua gRPC (đồng bộ) và Apache Kafka (bất đồng bộ).
- Bảo mật theo chuẩn OAuth2/OIDC với Keycloak.
- Khả năng mở rộng và giám sát hệ thống.

### 2.1.2. Yêu cầu chức năng

Bảng 2.1: Yêu cầu chức năng theo module

| STT | Module | Chức năng | Mô tả |
|-----|--------|-----------|-------|
| 1 | Identity Service | Đăng ký tài khoản | Customer tự đăng ký, Admin tạo tài khoản cho Maker/Checker/Partner |
| 2 | Identity Service | Đăng nhập | Xác thực qua Keycloak, trả JWT token |
| 3 | Identity Service | Refresh token | Làm mới access token khi hết hạn |
| 4 | Identity Service | Quản lý profile | Xem, cập nhật thông tin cá nhân, đổi mật khẩu |
| 5 | Identity Service | Quản lý system users | CRUD tài khoản hệ thống (Admin) |
| 6 | Identity Service | Quản lý roles | CRUD vai trò và phân quyền (Admin) |
| 7 | Voucher Service | Tạo voucher đơn lẻ | Maker/Partner nhập thông tin voucher trực tiếp |
| 8 | Voucher Service | Tạo voucher hàng loạt | Upload file Excel chứa danh sách voucher |
| 9 | Voucher Service | Gửi phê duyệt (Submit) | Chuyển trạng thái INIT → PENDING_APPROVE |
| 10 | Voucher Service | Phê duyệt/Từ chối | Checker approve hoặc reject kèm lý do |
| 11 | Voucher Service | Hủy yêu cầu (Cancel) | Maker/Partner hủy khi chưa gửi duyệt |
| 12 | Voucher Service | Tạo nhiệm vụ (Mission) | Cấu hình mục tiêu, phần thưởng, thời hạn |
| 13 | Voucher Service | Ghi audit log | Tự động ghi nhật ký mọi thao tác |
| 14 | Customer Service | Xem voucher khả dụng | Danh sách voucher theo hạng thành viên |
| 15 | Customer Service | Thu thập voucher | Lưu voucher vào kho cá nhân |
| 16 | Customer Service | Thanh toán | Áp dụng voucher, tính giảm giá, tích điểm |
| 17 | Customer Service | Xem nhiệm vụ | Danh sách mission và tiến độ |
| 18 | Customer Service | Nhận thưởng | Claim reward khi hoàn thành mission |
| 19 | Customer Service | Bảng xếp hạng | Top khách hàng theo điểm tích lũy |
| 20 | Loyalty Service | Tích điểm | Tự động tính điểm khi thanh toán (1 point/1.000 VND) |
| 21 | Loyalty Service | Nâng hạng | Tự động nâng tier khi đạt ngưỡng điểm |
| 22 | Loyalty Service | Cập nhật mission | Cập nhật tiến độ nhiệm vụ khi có giao dịch |

### 2.1.3. Yêu cầu phi chức năng

Bảng 2.2: Yêu cầu phi chức năng

| STT | Yêu cầu | Chỉ tiêu | Giải pháp kỹ thuật |
|-----|----------|-----------|-------------------|
| 1 | Hiệu năng | Response time < 500ms | Redis cache, gRPC binary protocol |
| 2 | Khả năng mở rộng | Scale từng service độc lập | Docker container, Kafka consumer group |
| 3 | Bảo mật | Chuẩn OAuth2/OIDC | Keycloak, JWT, Kong API Gateway |
| 4 | Độ tin cậy | Không mất message | Kafka durability, PostgreSQL ACID |
| 5 | Giám sát | Real-time monitoring | Prometheus + Grafana, ELK Stack |
| 6 | Bảo trì | Loose coupling | Database per Service, gRPC contract |

### 2.1.4. Ràng buộc nghiệp vụ

Bảng 2.3: Ràng buộc nghiệp vụ

| STT | Ràng buộc | Mô tả |
|-----|-----------|-------|
| 1 | Phân quyền dữ liệu | Partner chỉ xem voucher/mission do mình tạo |
| 2 | Voucher theo hạng | Voucher Partner luôn customerTier = ALL; Voucher System có thể chỉ định hạng cụ thể |
| 3 | Voucher REWARD | Voucher thưởng từ mission không được Submit/Cancel thủ công |
| 4 | Batch processing | Xử lý VoucherDetail theo batch 100 records khi approve/reject |
| 5 | Giới hạn thu thập | Mỗi customer thu thập voucher tối đa = maxCollect |
| 6 | Điều kiện áp dụng | Voucher chỉ áp dụng khi orderAmount >= minOrderValue và còn hạn |
| 7 | Nâng hạng tự động | SILVER (0), GOLD (1.000), PLATINUM (5.000), DIAMOND (10.000 điểm) |

## 2.2. Phân tích tác nhân và biểu đồ Use Case

Bảng 2.4: Ký hiệu UML – Biểu đồ Use Case

| Ký hiệu | Tên | Ý nghĩa |
|---|---|---|
| 🧍 (hình người) | Actor | Tác nhân tương tác với hệ thống |
| ⬭ (hình elip) | Use Case | Một chức năng mà hệ thống cung cấp |
| ▭ (hình chữ nhật lớn) | System Boundary | Ranh giới hệ thống |
| ── (đường liền) | Association | Tác nhân tham gia use case |
| --▷ «include» | Include | Quan hệ bắt buộc – use case con luôn được gọi |
| --▷ «extend» | Extend | Quan hệ mở rộng – gọi khi có điều kiện |
| ──▷ (mũi tên tam giác rỗng) | Generalization | Quan hệ kế thừa giữa các actor |

Bảng 2.5: Ký hiệu UML – Biểu đồ hoạt động (Activity Diagram)

| Ký hiệu | Tên | Ý nghĩa |
|---|---|---|
| ● (hình tròn đen đặc) | Initial Node | Điểm bắt đầu luồng hoạt động |
| ◉ (hình tròn đen viền đậm) | Activity Final Node | Điểm kết thúc luồng hoạt động |
| ▭ bo góc | Action / Activity | Một hành động hoặc bước xử lý |
| ◇ (hình thoi) | Decision Node | Điểm rẽ nhánh theo điều kiện |
| ◇ (hình thoi) | Merge Node | Điểm hợp nhất các nhánh |
| → (mũi tên liền) | Control Flow | Hướng đi của luồng xử lý |
| [điều kiện] | Guard Condition | Điều kiện trên nhánh rẽ (VD: [Có], [Không]) |
| ║ (cột dọc phân vùng) | Swimlane | Phân vùng theo tác nhân hoặc thành phần |

Bảng 2.6: Ký hiệu UML – Biểu đồ trình tự (Sequence Diagram)

| Ký hiệu | Tên | Ý nghĩa |
|---|---|---|
| :Object (hình chữ nhật + đường đứt dọc) | Lifeline | Đối tượng tham gia tương tác theo thời gian |
| → (mũi tên liền ngang) | Synchronous Message | Gọi đồng bộ – chờ phản hồi |
| ⇢ (mũi tên đứt ngang) | Return Message | Phản hồi trả về cho bên gọi |
| ▭ mỏng trên lifeline | Activation Bar | Khoảng thời gian đối tượng đang xử lý |
| [điều kiện] | Guard | Điều kiện để message được gửi |
| loop | Loop Fragment | Vòng lặp (VD: batch 100 records) |
| alt | Alternative Fragment | Rẽ nhánh (VD: [rewardType=POINT] / [VOUCHER]) |

Bảng 2.7: Ký hiệu UML – Biểu đồ trạng thái (State Diagram)

| Ký hiệu | Tên | Ý nghĩa |
|---|---|---|
| ● (hình tròn đen đặc) | Initial State | Trạng thái khởi đầu |
| ◉ (hình tròn đen viền đậm) | Final State | Trạng thái kết thúc |
| ▭ bo góc | State | Một trạng thái của đối tượng |
| → (mũi tên liền) | Transition | Chuyển trạng thái |
| (event) trên mũi tên | Trigger | Sự kiện gây chuyển trạng thái (VD: Submit, Approve) |

### 2.2.1. Xác định tác nhân

Bảng 2.4: Danh sách tác nhân hệ thống

| STT | Tác nhân | Vai trò | Phạm vi tương tác |
|-----|----------|---------|-------------------|
| 1 | Admin | Quản trị hệ thống | Identity Service (quản lý user, role), Voucher Service (audit log, mock invoice) |
| 2 | Maker | Tạo nội dung | Voucher Service (tạo voucher, mission, submit, cancel) |
| 3 | Checker | Phê duyệt | Voucher Service (approve, reject, xem audit log) |
| 4 | Partner | Đối tác | Voucher Service (tạo voucher/mission cho cửa hàng riêng) |
| 5 | Customer | Người dùng cuối | Customer Service (thu thập voucher, thanh toán, mission, leaderboard) |

### 2.2.2. Biểu đồ Use Case tổng quát

<!-- Chèn sơ đồ từ file: docs/usecase-diagram.xml -->

Hình 2.1: Biểu đồ Use Case tổng quát hệ thống

### 2.2.3. Biểu đồ Use Case chi tiết theo nhóm chức năng

Nhóm 1 – Xác thực và Quản lý Profile:

<!-- Chèn sơ đồ từ file: docs/usecase-identity.xml -->

Hình 2.2: Biểu đồ Use Case – Xác thực và Quản lý Profile

Bao gồm: Đăng nhập (verify qua Keycloak, trả JWT), Đăng ký (tạo user Keycloak + gRPC tạo CustomerProfile), Refresh Token, Xem/Cập nhật Profile, Đổi mật khẩu.

Nhóm 2 – Quản trị hệ thống (Admin):

<!-- Chèn sơ đồ từ file: docs/usecase-admin.xml -->

Hình 2.3: Biểu đồ Use Case – Quản trị hệ thống

Bao gồm: Quản lý tài khoản hệ thống (include: tạo Maker/Checker, tạo Partner; extend: reset mật khẩu/khóa tài khoản), Quản lý vai trò và phân quyền, Quản lý hóa đơn mẫu, Xem Audit Log, Xem Dashboard thống kê.

Nhóm 3 – Quản lý Voucher và Mission (Maker/Partner):

<!-- Chèn sơ đồ từ file: docs/usecase-voucher-management.xml -->

Hình 2.4: Biểu đồ Use Case – Quản lý Voucher và Mission

Bao gồm: Tạo voucher đơn lẻ (include: validate, gRPC lấy storeName), Tạo voucher hàng loạt (include: parse Excel), Tạo nhiệm vụ (include: tạo voucher REWARD, gRPC createMission), Gửi phê duyệt, Hủy yêu cầu, Xem danh sách, Xem Dashboard. Partner có ràng buộc: customerTier=ALL, creatorType=PARTNER, chỉ xem dữ liệu của mình.

Nhóm 4 – Phê duyệt (Checker):

<!-- Chèn sơ đồ từ file: docs/usecase-checker.xml -->

Hình 2.5: Biểu đồ Use Case – Phê duyệt Voucher và Mission

Bao gồm: Xem danh sách yêu cầu chờ duyệt, Xem chi tiết, Phê duyệt (include: batch activate 100 records, ghi audit log), Từ chối (include: nhập lý do bắt buộc, ghi audit log), Xem Audit Log.

Nhóm 5 – Dịch vụ Khách hàng (Customer):

<!-- Chèn sơ đồ từ file: docs/usecase-customer.xml -->

Hình 2.6: Biểu đồ Use Case – Dịch vụ Khách hàng

Bao gồm: Đăng ký, Đăng nhập, Xem profile (tier, points), Xem voucher khả dụng (theo hạng), Thu thập voucher, Xem kho voucher, Xem voucher áp dụng được, Thanh toán (include: tích điểm tự động → nâng hạng tự động → cập nhật tiến độ mission), Xem nhiệm vụ và tiến độ, Nhận thưởng, Xem bảng xếp hạng.


### 2.2.4. Đặc tả Use Case

Bảng 2.5: Đặc tả UC05 – Tạo voucher đơn lẻ

| Mục | Nội dung |
|-----|----------|
| Tên Use Case | Tạo voucher đơn lẻ (SINGLE mode) |
| Tác nhân | Maker, Partner |
| Mô tả | Người dùng nhập thông tin voucher, hệ thống tạo VoucherRequest và VoucherDetail |
| Tiền điều kiện | Đã đăng nhập với role MAKER hoặc PARTNER |
| Luồng chính | 1. Nhập thông tin: tên, mô tả, discountType, discountValue, maxDiscount, minOrderValue, totalStock, startDate, endDate, customerTier. 2. Hệ thống validate dữ liệu. 3. Nếu Partner: gán customerTier=ALL, creatorType=PARTNER, lấy storeName qua gRPC. 4. Tạo requestId (VOUCHER_{timestamp}). 5. Lưu VoucherRequest (status=INIT). 6. Lưu VoucherDetail (voucherStatus=INACTIVE). 7. Trả kết quả thành công. |
| Luồng ngoại lệ | Validate thất bại → trả lỗi VALIDATION_ERROR. gRPC timeout → trả lỗi INTERNAL_ERROR. |
| Hậu điều kiện | VoucherRequest ở trạng thái INIT, sẵn sàng Submit |

Bảng 2.6: Đặc tả UC14 – Phê duyệt voucher

| Mục | Nội dung |
|-----|----------|
| Tên Use Case | Phê duyệt voucher (Approve) |
| Tác nhân | Checker |
| Mô tả | Checker xem xét và phê duyệt yêu cầu voucher, kích hoạt voucher cho khách hàng |
| Tiền điều kiện | Đã đăng nhập với role CHECKER; VoucherRequest ở trạng thái PENDING_APPROVE |
| Luồng chính | 1. Checker xem chi tiết yêu cầu. 2. Chọn Approve. 3. Hệ thống cập nhật VoucherRequest: status=APPROVED, confirmedBy, confirmedTime. 4. Xử lý VoucherDetail theo batch 100 records: requestStatus=PROCESSING → voucherStatus=ACTIVE. 5. Sau khi xong: VoucherRequest → FINISH. 6. Ghi audit log. |
| Luồng ngoại lệ | Request không ở PENDING_APPROVE → lỗi BAD_REQUEST. Batch processing thất bại → status=FAILED. |
| Hậu điều kiện | Voucher ở trạng thái ACTIVE, Customer có thể thu thập |

Bảng 2.7: Đặc tả UC22 – Thu thập voucher

| Mục | Nội dung |
|-----|----------|
| Tên Use Case | Thu thập voucher |
| Tác nhân | Customer |
| Mô tả | Customer chọn voucher khả dụng và lưu vào kho cá nhân |
| Tiền điều kiện | Đã đăng nhập với role CUSTOMER; Voucher ở trạng thái ACTIVE |
| Luồng chính | 1. Customer xem danh sách voucher khả dụng (theo tier). 2. Chọn voucher muốn thu thập. 3. Hệ thống kiểm tra: chưa collect, còn stock, chưa vượt maxCollect. 4. Tạo CustomerVoucher (status=AVAILABLE). 5. Giảm availableStock của VoucherDetail. 6. Trả thành công. |
| Luồng ngoại lệ | Đã collect → VOUCHER_ALREADY_COLLECTED. Hết stock → VOUCHER_OUT_OF_STOCK. Vượt maxCollect → MAX_COLLECT_EXCEEDED. |
| Hậu điều kiện | CustomerVoucher được tạo, availableStock giảm 1 |

Bảng 2.8: Đặc tả UC24 – Thanh toán với voucher

| Mục | Nội dung |
|-----|----------|
| Tên Use Case | Thanh toán với voucher |
| Tác nhân | Customer |
| Mô tả | Customer thanh toán hóa đơn, có thể áp dụng voucher giảm giá |
| Tiền điều kiện | Đã đăng nhập; Có hóa đơn cần thanh toán |
| Luồng chính | 1. Chọn hóa đơn. 2. Chọn voucher áp dụng (optional). 3. Hệ thống validate voucher (còn hạn, đủ minOrderValue, còn lượt). 4. Tính discountAmount. 5. Lưu Transaction. 6. Cập nhật CustomerVoucher (giảm availableUsage; nếu=0 → status=USED). 7. Publish LoyaltyPointEvent qua Kafka. 8. Publish VoucherUsedEvent qua Kafka. 9. Trả PaymentResponse. |
| Luồng ngoại lệ | Voucher hết hạn → VOUCHER_EXPIRED. Đơn hàng < minOrderValue → MIN_ORDER_NOT_MET. Hết lượt → VOUCHER_OUT_OF_STOCK. |
| Hậu điều kiện | Transaction được lưu, điểm được tích (bất đồng bộ), stock voucher giảm |

Bảng 2.9: Đặc tả UC26 – Nhận thưởng nhiệm vụ

| Mục | Nội dung |
|-----|----------|
| Tên Use Case | Nhận thưởng nhiệm vụ (Claim Reward) |
| Tác nhân | Customer |
| Mô tả | Customer nhận thưởng khi hoàn thành nhiệm vụ |
| Tiền điều kiện | CustomerMission ở trạng thái COMPLETED |
| Luồng chính | 1. Customer xem danh sách mission (status=COMPLETED). 2. Bấm "Nhận thưởng". 3. Hệ thống kiểm tra mission đã hoàn thành. 4. Nếu rewardType=POINT: cộng điểm. 5. Nếu rewardType=VOUCHER: tạo CustomerVoucher từ voucher REWARD. 6. Cập nhật CustomerMission status=CLAIMED. 7. Trả ClaimMissionRewardResponse. |
| Luồng ngoại lệ | Mission chưa hoàn thành → MISSION_NOT_COMPLETED. Đã nhận thưởng → REWARD_ALREADY_CLAIMED. |
| Hậu điều kiện | Thưởng được phát, CustomerMission status=CLAIMED |

## 2.3. Biểu đồ hoạt động

### 2.3.1. Biểu đồ hoạt động – Đăng ký tài khoản

<!-- Chèn sơ đồ từ file: docs/activity-register.xml -->

Hình 2.2: Biểu đồ hoạt động – Đăng ký tài khoản Customer

Luồng: Customer nhập thông tin → Identity Service validate → Tạo user trên Keycloak → Lưu database nội bộ → gRPC tạo CustomerProfile → Trả kết quả.

### 2.3.2. Biểu đồ hoạt động – Đăng nhập

<!-- Chèn sơ đồ từ file: docs/activity-login.xml -->

Hình 2.3: Biểu đồ hoạt động – Đăng nhập

Luồng: Client nhập username/password → POST /auth/login → Identity Service gọi Keycloak verify → Keycloak trả JWT → Client lưu token → Chuyển hướng dashboard.

### 2.3.3. Biểu đồ hoạt động – Tạo tài khoản Partner

<!-- Chèn sơ đồ từ file: docs/activity-create-partner.xml -->

Hình 2.4: Biểu đồ hoạt động – Tạo tài khoản Partner

Luồng: Admin nhập thông tin Partner → Identity Service validate → Tạo user Keycloak (role=PARTNER) → Lưu bảng users + partner → Trả kết quả.

### 2.3.4. Biểu đồ hoạt động – Phân phối Voucher (tổng quan)

<!-- Chèn sơ đồ từ file: docs/activity-diagram.xml -->

Hình 2.5: Biểu đồ hoạt động – Luồng phân phối Voucher

Luồng: Maker/Partner tạo voucher (INIT) → Submit (PENDING_APPROVE) → Checker xem xét → Approve: kích hoạt batch → FINISH / Reject: REJECTED.

### 2.3.5. Biểu đồ hoạt động – Thu thập Voucher

<!-- Chèn sơ đồ từ file: docs/activity-collect-voucher.xml -->

Hình 2.6: Biểu đồ hoạt động – Thu thập Voucher

Luồng: Customer mở trang voucher → GET /vouchers/available → Lấy tier → gRPC searchVouchers → Kiểm tra collected → Trả danh sách → Customer chọn → POST /collect/{id} → Kiểm tra đã collect? → Kiểm tra stock? → Lưu CustomerVoucher → Trả success.

### 2.3.6. Biểu đồ hoạt động – Thanh toán

<!-- Chèn sơ đồ từ file: docs/activity-payment.xml -->

Hình 2.7: Biểu đồ hoạt động – Thanh toán với Voucher

Luồng: Customer chọn hóa đơn + voucher → POST /payments/process → Validate voucher → Tính discount → Lưu Transaction → Cập nhật CustomerVoucher → Publish Kafka events → Trả PaymentResponse.

### 2.3.7. Biểu đồ hoạt động – Quản lý Mission

<!-- Chèn sơ đồ từ file: docs/activity-mission-diagram.xml -->

Hình 2.8: Biểu đồ hoạt động – Tạo và phê duyệt Mission

Luồng: Maker/Partner nhập thông tin mission → Validate → Tạo VoucherRequest (purpose=REWARD) → gRPC createMission → Submit → Checker duyệt → Kích hoạt mission + voucher thưởng.

### 2.3.8. Biểu đồ hoạt động – Tạo voucher hàng loạt (Excel)

<!-- Chèn sơ đồ từ file: docs/activity-create-voucher-excel.xml -->

Hình 2.9: Biểu đồ hoạt động – Tạo voucher hàng loạt (Excel upload)

Luồng: Maker/Partner chọn discountType + nhập requestId + upload file Excel → Voucher Service validate (file không rỗng, requestId chưa tồn tại) → Nếu Partner: gRPC lấy storeName → Đọc file Excel (Apache POI) parse từng dòng → Tạo VoucherRequest (status=DRAFT, mode=EXCEL) → Lưu VoucherDetail theo batch 100 records (voucherStatus=INACTIVE, voucherCode=VCH-{UUID}) → Trả response (requestId, totalCreated).

### 2.3.9. Biểu đồ hoạt động – Nhận thưởng nhiệm vụ

<!-- Chèn sơ đồ từ file: docs/activity-claim-reward.xml -->

Hình 2.10: Biểu đồ hoạt động – Nhận thưởng nhiệm vụ (Claim Reward)

Luồng: Customer xem danh sách mission → Chọn mission (status=COMPLETED) → Bấm "Nhận thưởng" → Customer Service tìm CustomerMission → Kiểm tra status=COMPLETED (nếu không → lỗi MISSION_NOT_COMPLETED) → gRPC getMissionById lấy rewardType → Nếu POINT: cộng điểm vào totalPoints → Nếu VOUCHER: tạo CustomerVoucher (status=AVAILABLE) → Cập nhật CustomerMission status=CLAIMED → Trả ClaimMissionRewardResponse.

### 2.3.10. Biểu đồ hoạt động – Nâng hạng thành viên

<!-- Chèn sơ đồ từ file: docs/activity-tier-upgrade.xml -->

Hình 2.11: Biểu đồ hoạt động – Nâng hạng tự động

Luồng: Thanh toán thành công → Kafka LoyaltyPointEvent → Loyalty Service tính điểm → Cộng totalPoints → Kiểm tra ngưỡng → Nếu đạt: nâng tier → Kafka TierUpgradeEvent → Customer Service cập nhật profile.

## 2.4. Biểu đồ trình tự

### 2.4.1. Biểu đồ trình tự – Đăng nhập

<!-- Chèn sơ đồ từ file: docs/sequence-login.xml -->

Hình 2.10: Biểu đồ trình tự – Đăng nhập

Thành phần: Client → AuthController → AuthService → Keycloak → Client.

### 2.4.2. Biểu đồ trình tự – Đăng ký

<!-- Chèn sơ đồ từ file: docs/sequence-register.xml -->

Hình 2.11: Biểu đồ trình tự – Đăng ký Customer

Thành phần: Client → AuthController → AuthService → Keycloak → UserRepository → CustomerGrpcClient → CustomerService.

### 2.4.3. Biểu đồ trình tự – Tạo Partner

<!-- Chèn sơ đồ từ file: docs/sequence-create-partner.xml -->

Hình 2.12: Biểu đồ trình tự – Tạo tài khoản Partner

Thành phần: Admin → SystemUserController → SystemUserService → Keycloak → UserRepository → PartnerRepository.

### 2.4.4. Biểu đồ trình tự – Tạo và phê duyệt Voucher

<!-- Chèn sơ đồ từ file: docs/sequence-diagram.xml -->

Hình 2.13: Biểu đồ trình tự – Tạo và phê duyệt Voucher

Thành phần: Maker → VoucherController → VoucherService → IdentityGrpcClient → VoucherRepository → VoucherDetailRepository → AuditLogService.

### 2.4.5. Biểu đồ trình tự – Thu thập Voucher

<!-- Chèn sơ đồ từ file: docs/sequence-collect-voucher.xml -->

Hình 2.14: Biểu đồ trình tự – Thu thập Voucher

Thành phần: Customer → CustomerVoucherController → CustomerVoucherService → VoucherGrpcClient → Redis → CustomerVoucherRepository.

### 2.4.6. Biểu đồ trình tự – Thanh toán

<!-- Chèn sơ đồ từ file: docs/sequence-payment.xml -->

Hình 2.15: Biểu đồ trình tự – Thanh toán

Thành phần: Customer → PaymentController → PaymentService → CustomerVoucherRepository → TransactionRepository → KafkaService.

### 2.4.7. Biểu đồ trình tự – Tạo và phê duyệt Mission

<!-- Chèn sơ đồ từ file: docs/sequence-mission-diagram.xml -->

Hình 2.18: Biểu đồ trình tự – Tạo và phê duyệt Mission

Thành phần: Maker → MissionController → MissionService → VoucherService → MissionGrpcClient → LoyaltyService.

### 2.4.8. Biểu đồ trình tự – Tạo voucher hàng loạt (Excel)

<!-- Chèn sơ đồ từ file: docs/sequence-create-voucher-excel.xml -->

Hình 2.19: Biểu đồ trình tự – Tạo voucher hàng loạt (Excel upload)

Thành phần: Maker/Partner → VoucherController → VoucherService → ExcelReaderHelper (Apache POI) → IdentityGrpcClient (getNameStore) → VoucherRepository (save request) → VoucherDetailRepository (batch saveAll 100 records/batch).

### 2.4.9. Biểu đồ trình tự – Nhận thưởng nhiệm vụ

<!-- Chèn sơ đồ từ file: docs/sequence-claim-reward.xml -->

Hình 2.20: Biểu đồ trình tự – Nhận thưởng nhiệm vụ (Claim Reward)

Thành phần: Customer → CustomerMissionController → CustomerMissionService → CustomerMissionRepository (findByCustomerIdAndMissionId) → MissionGrpcClient (getMissionById) → [POINT] CustomerProfileRepository (updateTotalPoints) / [VOUCHER] CustomerVoucherRepository (save) → CustomerMissionRepository (updateStatus=CLAIMED).

### 2.4.10. Biểu đồ trình tự – Nâng hạng

<!-- Chèn sơ đồ từ file: docs/sequence-tier-upgrade.xml -->

Hình 2.21: Biểu đồ trình tự – Nâng hạng tự động

Thành phần: Kafka (LoyaltyPointEvent) → LoyaltyPointConsumer → UserPointRepository → Kafka (TierUpgradeEvent) → TierUpgradeConsumer → CustomerProfileRepository.

## 2.5. Biểu đồ trạng thái

### 2.5.1. Trạng thái Voucher Request

<!-- Chèn sơ đồ trạng thái hoặc mô tả bằng text -->

Hình 2.18: Biểu đồ trạng thái – Voucher Request

```
INIT ──(Submit)──► PENDING_APPROVE ──(Approve)──► APPROVED ──(Processing)──► FINISH
 │                       │
 │(Cancel)               │(Reject)
 ▼                       ▼
CANCELLED              REJECTED
```

Bảng 2.10: Mô tả trạng thái Voucher Request

| Trạng thái | Mô tả | Chuyển từ | Chuyển đến | Actor |
|---|---|---|---|---|
| INIT | Đã tạo, chờ gửi duyệt | – | PENDING_APPROVE, CANCELLED | Maker/Partner |
| PENDING_APPROVE | Đã gửi, chờ duyệt | INIT | APPROVED, REJECTED | Checker |
| APPROVED | Đã phê duyệt | PENDING_APPROVE | FINISH | System |
| REJECTED | Bị từ chối (kèm lý do) | PENDING_APPROVE | – | Checker |
| CANCELLED | Đã hủy | INIT | – | Maker/Partner |
| FINISH | Hoàn tất, voucher ACTIVE | APPROVED | – | System |

### 2.5.2. Trạng thái Voucher Detail

VoucherDetail có 2 trường trạng thái hoạt động song song:
- `requestStatus`: Trạng thái trong quy trình phê duyệt (gắn với VoucherRequest).
- `voucherStatus` (status): Trạng thái hoạt động của voucher đối với khách hàng.

**A. Trạng thái requestStatus (trong quy trình phê duyệt)**

Hình 2.19: Biểu đồ trạng thái – Voucher Detail (requestStatus)

```
Tạo đơn lẻ (SINGLE):
[●] ──(Tạo voucher)──► INIT ──(Submit request)──► INIT ──(Approve)──► PROCESSING ──► SUCCESS
                                                        │
                                                        │(Reject)
                                                        ▼
                                                     REJECTED

Tạo hàng loạt (EXCEL):
[●] ──(Upload Excel)──► DRAFT ──(Parse xong)──► INIT ──(Submit)──► INIT ──(Approve)──► PROCESSING ──► SUCCESS
                                                                         │
                                                                         │(Reject)
                                                                         ▼
                                                                      REJECTED
```

Bảng 2.11a: Mô tả trạng thái requestStatus của Voucher Detail

| Trạng thái | Mô tả | Chuyển từ | Chuyển đến | Khi nào |
|---|---|---|---|---|
| DRAFT | Bản nháp (chỉ Excel mode) | – | INIT | Sau khi parse file Excel xong |
| INIT | Đã tạo, chờ request được duyệt | DRAFT hoặc – (Single) | PROCESSING, REJECTED | Khi VoucherRequest được approve/reject |
| PROCESSING | Đang xử lý kích hoạt (batch) | INIT | SUCCESS, FAILED | Trong quá trình batch activate |
| SUCCESS | Đã kích hoạt thành công | PROCESSING | – (cuối) | Batch xử lý xong, voucher ACTIVE |
| FAILED | Xử lý thất bại | PROCESSING | – (cuối) | Lỗi trong quá trình batch |
| REJECTED | Bị từ chối | INIT | – (cuối) | Checker reject VoucherRequest |

**B. Trạng thái voucherStatus (đối với khách hàng)**

Hình 2.20: Biểu đồ trạng thái – Voucher Detail (voucherStatus)

```
[●] ──(Tạo)──► INACTIVE ──(Request APPROVED, batch activate)──► ACTIVE ──(endDate hết hạn)──► EXPIRED
                                                                    │
                                                                    │(availableStock = 0)
                                                                    ▼
                                                               OUT_OF_STOCK
```

Bảng 2.11b: Mô tả trạng thái voucherStatus của Voucher Detail

| Trạng thái | Mô tả | Chuyển từ | Chuyển đến | Điều kiện |
|---|---|---|---|---|
| INACTIVE | Chưa kích hoạt, customer không thấy | – (mặc định) | ACTIVE | VoucherRequest được APPROVED → batch processing |
| ACTIVE | Đang hoạt động, customer có thể thu thập | INACTIVE | EXPIRED, OUT_OF_STOCK | Hết hạn hoặc hết stock |
| EXPIRED | Đã hết hạn, không thể thu thập | ACTIVE | – (cuối) | endDate < thời gian hiện tại |
| OUT_OF_STOCK | Hết số lượng, không thể thu thập thêm | ACTIVE | – (cuối) | availableStock = 0 |

**C. Mối quan hệ giữa 2 trạng thái theo luồng nghiệp vụ:**

Bảng 2.11c: Chuyển trạng thái theo từng bước nghiệp vụ

| Bước | Hành động | requestStatus | voucherStatus | Ghi chú |
|---|---|---|---|---|
| 1 | Tạo voucher đơn lẻ (SINGLE) | INIT | INACTIVE | Maker/Partner tạo qua form |
| 1 | Upload Excel (EXCEL) | DRAFT → INIT | INACTIVE | Parse file xong → INIT |
| 2 | Submit VoucherRequest | INIT (giữ nguyên) | INACTIVE (giữ nguyên) | Chỉ VoucherRequest đổi trạng thái |
| 3a | Checker Approve | INIT → PROCESSING → SUCCESS | INACTIVE → ACTIVE | Batch 100 records |
| 3b | Checker Reject | INIT → REJECTED | INACTIVE (giữ nguyên) | Voucher không bao giờ active |
| 4 | Customer collect | SUCCESS (giữ nguyên) | ACTIVE (giữ nguyên) | availableStock giảm 1 |
| 5 | Hết hạn | SUCCESS (giữ nguyên) | ACTIVE → EXPIRED | endDate < now |
| 6 | Hết stock | SUCCESS (giữ nguyên) | ACTIVE → OUT_OF_STOCK | availableStock = 0 |

### 2.5.3. Trạng thái Customer Voucher (CustomerVoucherStatus)

<!-- Chèn sơ đồ trạng thái hoặc mô tả bằng text -->

Hình 2.20: Biểu đồ trạng thái – Customer Voucher

```
[●] ──(Customer collect)──► AVAILABLE ──(Thanh toán, availableUsage=0)──► USED
                                 │
                                 │(expiredAt < now)
                                 ▼
                              EXPIRED
```

Bảng 2.12: Mô tả trạng thái Customer Voucher

| Trạng thái | Mô tả | Chuyển từ | Chuyển đến | Điều kiện chuyển |
|---|---|---|---|---|
| AVAILABLE | Đã thu thập, có thể sử dụng khi thanh toán | – (khi collect) | USED, EXPIRED | Thanh toán hoặc hết hạn |
| USED | Đã sử dụng hết lượt, không thể dùng lại | AVAILABLE | – (trạng thái cuối) | availableUsage giảm về 0 sau thanh toán |
| EXPIRED | Đã hết hạn, không thể sử dụng | AVAILABLE | – (trạng thái cuối) | expiredAt < thời gian hiện tại |

Luồng chi tiết:
- Khi customer collect voucher → tạo CustomerVoucher với status = AVAILABLE, availableUsage = maxCollect.
- Khi customer thanh toán sử dụng voucher → giảm availableUsage. Nếu availableUsage = 0 → chuyển status = USED.
- Nếu voucher có nhiều lượt dùng (availableUsage > 1) → sau mỗi lần thanh toán giảm 1, giữ AVAILABLE cho đến khi = 0.
- Khi expiredAt < thời gian hiện tại → chuyển status = EXPIRED (kiểm tra tại thời điểm query hoặc scheduled job).

### 2.5.4. Trạng thái Customer Mission (CustomerMissionStatus)

<!-- Chèn sơ đồ trạng thái hoặc mô tả bằng text -->

Hình 2.21: Biểu đồ trạng thái – Customer Mission

```
[●] ──(Thanh toán, mission ACTIVE)──► IN_PROGRESS ──(currentProgress >= targetValue)──► COMPLETED ──(Claim Reward)──► CLAIMED
```

Bảng 2.13: Mô tả trạng thái Customer Mission

| Trạng thái | Mô tả | Chuyển từ | Chuyển đến | Điều kiện chuyển |
|---|---|---|---|---|
| IN_PROGRESS | Đang thực hiện, tiến độ chưa đạt mục tiêu | – (khi tham gia) | COMPLETED | currentProgress >= targetValue |
| COMPLETED | Đã hoàn thành mục tiêu, chờ nhận thưởng | IN_PROGRESS | CLAIMED | Customer bấm "Nhận thưởng" |
| CLAIMED | Đã nhận thưởng (điểm hoặc voucher) | COMPLETED | – (trạng thái cuối) | Claim reward thành công |

Luồng chi tiết:
- Khi customer thanh toán lần đầu trong thời gian mission ACTIVE → Loyalty Service tạo CustomerMission với status = IN_PROGRESS, currentProgress = giá trị giao dịch.
- Mỗi lần thanh toán tiếp theo → Loyalty Service cộng dồn currentProgress (theo AMOUNT hoặc COUNT).
- Khi currentProgress >= targetValue → chuyển status = COMPLETED. Frontend hiển thị nút "Nhận thưởng".
- Khi customer bấm "Nhận thưởng" → nếu rewardType=POINT: cộng điểm; nếu rewardType=VOUCHER: tạo CustomerVoucher → chuyển status = CLAIMED.


## 2.6. Thiết kế cơ sở dữ liệu

### 2.6.1. Nguyên tắc thiết kế

Hệ thống áp dụng nguyên tắc Database per Service: mỗi microservice sở hữu database riêng, không service nào truy cập trực tiếp database của service khác. Khi cần dữ liệu từ service khác, phải gọi qua gRPC hoặc Kafka event. Điều này đảm bảo loose coupling và khả năng thay đổi schema độc lập.

### 2.6.2. Tổng quan database

Bảng 2.14: Phân chia database theo service

| Database | Service | Mô tả |
|---|---|---|
| identity_db | Identity Service | Quản lý user, partner, role |
| voucher_db | Voucher Service | Quản lý voucher request, detail, audit log |
| loyalty_db | Loyalty Service | Quản lý mission, điểm thưởng |
| customer_db | Customer Service | Quản lý profile, voucher cá nhân, transaction |

### 2.6.3. Bảng users (identity_db)

Bảng 2.15: Cấu trúc bảng users

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|-----|---------------|-----------|-------|
| id | UUID | PK | Khóa chính |
| keycloak_id | VARCHAR(255) | UNIQUE | ID trên Keycloak |
| username | VARCHAR(100) | UNIQUE, NOT NULL | Tên đăng nhập |
| email | VARCHAR(255) | UNIQUE | Email |
| first_name | VARCHAR(100) | | Tên |
| last_name | VARCHAR(100) | | Họ |
| role | VARCHAR(50) | NOT NULL | ADMIN, MAKER, CHECKER, PARTNER, CUSTOMER |
| status | VARCHAR(20) | DEFAULT 'ACTIVE' | ACTIVE, LOCKED |
| created_at | TIMESTAMP | DEFAULT NOW() | Thời gian tạo |

### 2.6.4. Bảng partner (identity_db)

Bảng 2.16: Cấu trúc bảng partner

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|-----|---------------|-----------|-------|
| id | BIGSERIAL | PK | Khóa chính |
| user_id | UUID | FK → users.id | Liên kết user |
| store_name | VARCHAR(255) | NOT NULL | Tên cửa hàng |
| phone | VARCHAR(20) | | Số điện thoại |
| category | VARCHAR(50) | | Danh mục (F&B, RETAIL...) |

### 2.6.5. Bảng voucher_requests (voucher_db)

Bảng 2.17: Cấu trúc bảng voucher_requests

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|-----|---------------|-----------|-------|
| id | BIGSERIAL | PK | Khóa chính |
| request_id | VARCHAR(100) | UNIQUE | Mã yêu cầu (VOUCHER_{timestamp}) |
| status | VARCHAR(30) | NOT NULL | INIT, PENDING_APPROVE, APPROVED, REJECTED, CANCELLED, FINISH |
| request_mode | VARCHAR(20) | | SINGLE, EXCEL |
| creator_type | VARCHAR(20) | | SYSTEM, PARTNER |
| voucher_purpose | VARCHAR(20) | | HUNT, REWARD |
| store_name | VARCHAR(255) | | Tên cửa hàng (Partner) |
| created_by | VARCHAR(255) | | Người tạo |
| confirmed_by | VARCHAR(255) | | Người duyệt |
| confirmed_time | TIMESTAMP | | Thời gian duyệt |
| reason | TEXT | | Lý do từ chối |
| created_at | TIMESTAMP | DEFAULT NOW() | |
| updated_at | TIMESTAMP | | |

### 2.6.6. Bảng vouchers (voucher_db)

Bảng 2.18: Cấu trúc bảng vouchers (voucher_detail)

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|-----|---------------|-----------|-------|
| id | BIGSERIAL | PK | Khóa chính |
| request_id | VARCHAR(100) | FK | Liên kết voucher_requests |
| voucher_code | VARCHAR(50) | UNIQUE | Mã voucher (VCH-{UUID}) |
| voucher_name | VARCHAR(255) | NOT NULL | Tên voucher |
| description | TEXT | | Mô tả |
| discount_type | VARCHAR(20) | NOT NULL | FIXED, PERCENT |
| discount_value | VARCHAR(50) | NOT NULL | Giá trị giảm |
| max_discount | VARCHAR(50) | | Giảm tối đa (cho PERCENT) |
| min_order_value | VARCHAR(50) | | Đơn hàng tối thiểu |
| customer_tier | VARCHAR(20) | | ALL, SILVER, GOLD, PLATINUM, DIAMOND |
| total_stock | INTEGER | | Tổng số lượng |
| available_stock | INTEGER | | Số lượng còn lại |
| max_collect | INTEGER | DEFAULT 1 | Số lần thu thập tối đa/customer |
| start_date | BIGINT | | Epoch milliseconds |
| end_date | BIGINT | | Epoch milliseconds |
| request_status | VARCHAR(30) | | INIT, PROCESSING, REJECTED |
| voucher_status | VARCHAR(20) | | INACTIVE, ACTIVE, EXPIRED |
| store_name | VARCHAR(255) | | Tên cửa hàng |

### 2.6.7. Bảng audit_logs (voucher_db)

Bảng 2.19: Cấu trúc bảng audit_logs

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|-----|---------------|-----------|-------|
| id | BIGSERIAL | PK | Khóa chính |
| action | VARCHAR(50) | NOT NULL | CREATE, SUBMIT, APPROVE, REJECT, CANCEL |
| entity_type | VARCHAR(50) | | VOUCHER, MISSION |
| entity_id | VARCHAR(100) | | ID đối tượng |
| performed_by | VARCHAR(255) | | Người thực hiện |
| details | TEXT | | Chi tiết (JSON) |
| created_at | TIMESTAMP | DEFAULT NOW() | |

### 2.6.8. Bảng customer_profiles (customer_db)

Bảng 2.20: Cấu trúc bảng customer_profiles

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|-----|---------------|-----------|-------|
| id | BIGSERIAL | PK | Khóa chính |
| user_id | UUID | UNIQUE | Liên kết Identity Service |
| full_name | VARCHAR(255) | | Họ tên |
| balance | DECIMAL(15,2) | DEFAULT 0 | Số dư |
| total_points | INTEGER | DEFAULT 0 | Tổng điểm tích lũy |
| tier | VARCHAR(20) | DEFAULT 'SILVER' | SILVER, GOLD, PLATINUM, DIAMOND |
| status | VARCHAR(20) | DEFAULT 'ACTIVE' | ACTIVE, LOCKED |
| created_at | TIMESTAMP | DEFAULT NOW() | |

### 2.6.9. Bảng customer_vouchers (customer_db)

Bảng 2.21: Cấu trúc bảng customer_vouchers

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|-----|---------------|-----------|-------|
| id | BIGSERIAL | PK | Khóa chính |
| customer_id | BIGINT | FK | Liên kết customer_profiles |
| voucher_id | BIGINT | | ID voucher (voucher_db) |
| available_usage | INTEGER | | Số lượt còn dùng được |
| voucher_code | VARCHAR(50) | | Mã voucher |
| name_store | VARCHAR(255) | | Tên cửa hàng |
| creator_type | VARCHAR(20) | | SYSTEM, PARTNER |
| status | VARCHAR(20) | | AVAILABLE, USED, EXPIRED |
| obtained_at | TIMESTAMP | | Thời gian thu thập |
| used_at | TIMESTAMP | | Thời gian sử dụng |
| expired_at | TIMESTAMP | | Thời gian hết hạn |

### 2.6.10. Bảng transactions (customer_db)

Bảng 2.22: Cấu trúc bảng transactions

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|-----|---------------|-----------|-------|
| id | BIGSERIAL | PK | Khóa chính |
| customer_id | BIGINT | FK | Liên kết customer_profiles |
| invoice_id | BIGINT | | ID hóa đơn |
| voucher_id | BIGINT | | ID voucher áp dụng (nullable) |
| original_amount | DECIMAL(15,2) | | Tổng tiền gốc |
| discount_amount | DECIMAL(15,2) | | Số tiền giảm |
| final_amount | DECIMAL(15,2) | | Số tiền thực trả |
| points_earned | INTEGER | | Điểm nhận được |
| status | VARCHAR(20) | | SUCCESS, FAILED |
| created_at | TIMESTAMP | DEFAULT NOW() | |

### 2.6.11. Bảng tasks/missions (loyalty_db)

Bảng 2.23: Cấu trúc bảng tasks (missions)

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
|-----|---------------|-----------|-------|
| id | BIGSERIAL | PK | Khóa chính |
| mission_name | VARCHAR(255) | NOT NULL | Tên nhiệm vụ |
| mission_description | TEXT | | Mô tả |
| target_value | DOUBLE | | Mục tiêu cần đạt |
| target_type | VARCHAR(20) | | AMOUNT, COUNT |
| reward_type | VARCHAR(20) | | POINT, VOUCHER |
| reward_value | VARCHAR(50) | | Giá trị thưởng |
| partner_id | BIGINT | | ID đối tác (0 = system) |
| start_date | BIGINT | | Epoch milliseconds |
| end_date | BIGINT | | Epoch milliseconds |
| task_status | VARCHAR(20) | | ACTIVE, FINISH, EXPIRED |
| voucher_request_id | VARCHAR(100) | | Liên kết voucher thưởng |

### 2.6.12. Biểu đồ ERD

<!-- Chèn biểu đồ ERD tổng quan tại đây -->

Hình 2.19: Biểu đồ ERD tổng quan hệ thống (Database per Service)

## 2.7. Thiết kế kiến trúc phần mềm

### 2.7.1. Mô hình kiến trúc Microservices

Hệ thống được thiết kế theo kiến trúc Microservices với các nguyên tắc:
- Mỗi service có database riêng (Database per Service).
- Giao tiếp đồng bộ qua gRPC cho các truy vấn cần response ngay.
- Giao tiếp bất đồng bộ qua Apache Kafka cho các event không cần response tức thì.
- API Gateway (Kong) là điểm vào duy nhất cho client.
- Xác thực tập trung qua Keycloak, mỗi service verify JWT token độc lập.

### 2.7.2. Tổ chức module backend

Mỗi service tuân theo kiến trúc phân lớp (Layered Architecture):
- Controller: REST endpoints, request validation.
- Service: Business logic, orchestration.
- Repository: Data access (Spring Data JPA).
- Entity: JPA entities, database mapping.
- DTO: Request/Response/Event objects.
- gRPC: Client/Server cho inter-service communication.
- Configuration: Security, Redis, Kafka config.

### 2.7.3. Tổ chức frontend

Frontend sử dụng mô hình BFF (Backend-For-Frontend) qua Next.js API Routes:
- app/api/: Proxy routes gọi đến backend services.
- app/dashboard/: Các trang chính theo chức năng.
- components/: UI components tái sử dụng.
- lib/: Service layer, auth store, utilities.

## 2.8. Kết luận chương 2

Chương 2 đã trình bày đầy đủ phân tích và thiết kế hệ thống bao gồm: phân tích yêu cầu chức năng (22 chức năng), phi chức năng (6 yêu cầu), và ràng buộc nghiệp vụ (7 ràng buộc); xác định 5 tác nhân với 27 use case chi tiết; thiết kế 8 biểu đồ hoạt động, 8 biểu đồ trình tự, biểu đồ trạng thái cho 4 đối tượng chính; thiết kế cơ sở dữ liệu với 4 database riêng biệt và 11 bảng dữ liệu; thiết kế kiến trúc phần mềm theo mô hình Microservices. Kết quả thiết kế này là cơ sở để triển khai phát triển hệ thống ở Chương 3.
