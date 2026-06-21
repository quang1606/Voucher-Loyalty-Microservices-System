# TÀI LIỆU API - HỆ THỐNG QUẢN LÝ VOUCHER & KHÁCH HÀNG THÂN THIẾT

## 1. Giới thiệu dự án

Hệ thống "Quản lý Voucher và Chương trình Khách hàng Thân thiết" được xây dựng theo kiến trúc **Microservices**, gồm nhiều service độc lập giao tiếp với nhau qua 3 kênh: REST API (client), gRPC (đồng bộ giữa service) và Apache Kafka (bất đồng bộ, event-driven).

Hệ thống phục vụ 5 vai trò: **Admin, Maker, Checker, Partner, Customer**, hỗ trợ quản lý vòng đời voucher với quy trình phê duyệt nhiều bước (Maker/Checker), tích điểm và phân hạng khách hàng tự động, gamification qua nhiệm vụ (mission), và bảng xếp hạng.

### Các service chính

| Service | Port REST | Port gRPC | Vai trò |
|---|---|---|---|
| Identity Service | 8081 | 9091 | Xác thực, phân quyền, quản lý user/partner/role |
| Voucher Service | 8082 | 9099 | Quản lý voucher, mission, audit log, dashboard |
| Customer Service | 8084 | 9094 | Phục vụ khách hàng: voucher, payment, mission, leaderboard |
| Loyalty Service | - | 9093 | Tích điểm, nâng hạng, mission entity (chỉ gRPC + Kafka) |
| Notification Service | - | - | Consumer Kafka, xử lý thông báo |

### Hạ tầng hỗ trợ

- **Kong API Gateway** (:8000): điểm vào duy nhất, routing, CORS, logging
- **Keycloak** (:8180): máy chủ xác thực OAuth2/OIDC
- **PostgreSQL**: cơ sở dữ liệu (Database per Service)
- **Redis**: cache, leaderboard (Sorted Set)
- **Apache Kafka**: message broker bất đồng bộ
- **ELK Stack + Prometheus/Grafana**: logging tập trung và giám sát

### Quy ước chung

- Mọi request đi qua Kong Gateway theo path prefix, ví dụ: `http://localhost:8000/api/identity/...`
- Xác thực bằng JWT Bearer token (cấp bởi Keycloak qua Identity Service)
- Response chuẩn theo format `BaseResponse`:

```json
{ "status": 0, "code": "success", "message": "Success", "data": { } }
```

---

## 2. REST API

### 2.1. Identity Service (Port 8081)

**Auth — `/api/v1/auth`**

| Method | Endpoint | Chức năng | Quyền |
|---|---|---|---|
| POST | `/api/v1/auth/login` | Đăng nhập, trả JWT token | Public |
| POST | `/api/v1/auth/register` | Đăng ký tài khoản Customer | Public |
| POST | `/api/v1/auth/refresh` | Làm mới access token | Public |
| POST | `/api/v1/auth/allowed-pages` | Lấy danh sách trang được phép theo role | Public |

**Profile — `/api/v1/profile`**

| Method | Endpoint | Chức năng | Quyền |
|---|---|---|---|
| GET | `/api/v1/profile` | Xem thông tin cá nhân | Authenticated |
| PUT | `/api/v1/profile` | Cập nhật thông tin cá nhân | Authenticated |
| PUT | `/api/v1/profile/password` | Đổi mật khẩu | Authenticated |

**Roles — `/api/v1/roles`** (Admin)

| Method | Endpoint | Chức năng |
|---|---|---|
| GET | `/api/v1/roles` | Danh sách vai trò |
| POST | `/api/v1/roles` | Tạo vai trò mới |
| GET | `/api/v1/roles/{roleName}` | Chi tiết vai trò |
| PUT | `/api/v1/roles/{roleName}` | Cập nhật vai trò |
| DELETE | `/api/v1/roles/{roleName}` | Xóa vai trò |
| PUT | `/api/v1/roles/{roleName}/attributes` | Cập nhật attributes (allowed-pages) |

**System Users — `/api/v1/system-users`** (Admin)

| Method | Endpoint | Chức năng |
|---|---|---|
| GET | `/api/v1/system-users` | Danh sách tài khoản hệ thống |
| POST | `/api/v1/system-users` | Tạo user (Maker/Checker/Partner) |
| PUT | `/api/v1/system-users/{id}` | Cập nhật user |
| DELETE | `/api/v1/system-users/{id}` | Xóa user |
| POST | `/api/v1/system-users/{id}/reset-password` | Reset mật khẩu |

### 2.2. Voucher Service (Port 8082)

**Voucher — `/api/v1/vouchers`**

| Method | Endpoint | Chức năng | Quyền |
|---|---|---|---|
| POST | `/api/v1/vouchers` | Tạo voucher đơn lẻ | MAKER, PARTNER |
| POST | `/api/v1/vouchers/excel` | Tạo voucher hàng loạt (upload Excel) | MAKER, PARTNER |
| GET | `/api/v1/vouchers` | Danh sách voucher request (filter, paging) | MAKER, CHECKER, PARTNER |
| GET | `/api/v1/vouchers/details` | Danh sách voucher detail (filter) | MAKER, CHECKER, PARTNER |
| GET | `/api/v1/vouchers/{id}` | Chi tiết một voucher request | MAKER, CHECKER, PARTNER |
| PUT | `/api/v1/vouchers/{id}/submit` | Gửi phê duyệt | MAKER, PARTNER |
| PUT | `/api/v1/vouchers/{id}/confirm` | Phê duyệt / Từ chối | CHECKER |
| PUT | `/api/v1/vouchers/{id}/cancel` | Hủy yêu cầu | MAKER, PARTNER |

**Mission — `/api/v1/missions`**

| Method | Endpoint | Chức năng | Quyền |
|---|---|---|---|
| POST | `/api/v1/missions` | Tạo nhiệm vụ | MAKER, PARTNER |
| PUT | `/api/v1/missions/{id}/submit` | Gửi phê duyệt mission | MAKER, PARTNER |
| PUT | `/api/v1/missions/{id}/cancel` | Hủy mission | MAKER, PARTNER |
| PUT | `/api/v1/missions/{id}/confirm` | Phê duyệt / Từ chối mission | CHECKER |
| GET | `/api/v1/missions/search` | Tìm kiếm mission | MAKER, CHECKER, PARTNER |
| GET | `/api/v1/missions/{id}` | Chi tiết mission | MAKER, CHECKER, PARTNER |

**Dashboard — `/api/dashboard`**

| Method | Endpoint | Chức năng |
|---|---|---|
| GET | `/api/dashboard/voucher-monthly-stats?year=` | Thống kê voucher theo tháng |
| GET | `/api/dashboard/voucher-request-stats` | Thống kê tổng quan request |
| GET | `/api/dashboard/mission-stats` | Thống kê mission |
| GET | `/api/dashboard/mission-monthly-stats?year=` | Thống kê mission theo tháng |
| GET | `/api/dashboard/voucher-request-status-stats` | Thống kê theo trạng thái request |
| GET | `/api/dashboard/voucher-usage-stats?month=&year=` | Thống kê sử dụng voucher |
| GET | `/api/dashboard/voucher-usage-stats/export` | Xuất Excel thống kê sử dụng |

**Audit Log — `/api/v1/audit-logs`**

| Method | Endpoint | Chức năng | Quyền |
|---|---|---|---|
| GET | `/api/v1/audit-logs` | Xem nhật ký thao tác (filter theo role, user, ngày) | ADMIN, CHECKER, PARTNER |

**Mock Invoice — `/public/api/invoices`**

| Method | Endpoint | Chức năng | Quyền |
|---|---|---|---|
| POST | `/public/api/invoices` | Tạo hóa đơn mẫu | Public |
| GET | `/public/api/invoices` | Danh sách hóa đơn mẫu | Public |
| GET | `/public/api/invoices/{id}` | Chi tiết hóa đơn | ADMIN |

### 2.3. Customer Service (Port 8084)

**Profile — `/api/customers/profile`**

| Method | Endpoint | Chức năng |
|---|---|---|
| GET | `/api/customers/profile/{userId}` | Xem hồ sơ khách hàng (tier, điểm) |

**Voucher — `/api/customers/vouchers`** (CUSTOMER)

| Method | Endpoint | Chức năng |
|---|---|---|
| GET | `/api/customers/vouchers/available/with-status?customerId=` | Voucher khả dụng kèm trạng thái đã thu thập |
| POST | `/api/customers/vouchers/collect/{voucherId}?customerId=` | Thu thập voucher |
| GET | `/api/customers/vouchers/list` | Kho voucher cá nhân (filter trạng thái) |
| GET | `/api/customers/vouchers/applicable?customerId=&nameStore=&orderAmount=` | Voucher áp dụng được cho đơn hàng |

**Mission — `/api/customers/missions`** (CUSTOMER)

| Method | Endpoint | Chức năng |
|---|---|---|
| GET | `/api/customers/missions` | Danh sách nhiệm vụ và tiến độ |
| POST | `/api/customers/missions/claim-reward` | Nhận thưởng nhiệm vụ |

**Leaderboard — `/api/customers/leaderboard`** (CUSTOMER)

| Method | Endpoint | Chức năng |
|---|---|---|
| GET | `/api/customers/leaderboard` | Bảng xếp hạng top khách hàng |
| POST | `/api/customers/leaderboard/sync` | Đồng bộ dữ liệu lên Redis |

**Payment — `/api/v1/payments`**

| Method | Endpoint | Chức năng |
|---|---|---|
| POST | `/api/v1/payments/process` | Thanh toán hóa đơn, áp dụng voucher |

**Transaction — `/api/customers/transactions`**

| Method | Endpoint | Chức năng | Quyền |
|---|---|---|---|
| GET | `/api/customers/transactions?customerId=` | Lịch sử giao dịch | CUSTOMER |
| GET | `/api/customers/transactions/{id}?customerId=` | Chi tiết giao dịch | CUSTOMER |
| GET | `/api/customers/transactions/stats?requestId=` | Thống kê sử dụng voucher theo request | ADMIN |

**Mock Invoice — `/api/customers/invoices`**

| Method | Endpoint | Chức năng |
|---|---|---|
| GET | `/api/customers/invoices` | Danh sách hóa đơn để khách thanh toán |

---

## 3. Giao tiếp gRPC (đồng bộ)

gRPC dùng cho các truy vấn cần response ngay giữa các service. Mỗi service vừa có thể là **server** (cung cấp method) vừa là **client** (gọi service khác).

### 3.1. Các gRPC Service (Server)

**CustomerService** (Customer Service cung cấp)
- `CreateCustomerProfile` — tạo hồ sơ khách hàng khi đăng ký
- `GetVoucherUsageStats` — lấy thống kê sử dụng voucher

**IdentityService** (Identity Service cung cấp)
- `GetIdentity` — lấy thông tin user
- `CheckNameStore` — kiểm tra tên cửa hàng
- `GetPartner` — lấy thông tin partner theo userId
- `GetPartnerByName` — lấy partner theo tên

**LoyaltyService** (Loyalty Service cung cấp)
- `CreateMission` — tạo mission entity
- `GetMissionById` — lấy mission theo id
- `SearchMission` — tìm kiếm mission
- `UpdateMissionStatus` — cập nhật trạng thái mission
- `GetMissionMonthlyStats` — thống kê mission theo tháng

**VoucherGrpcService** (Voucher Service cung cấp)
- `SearchVoucher` — tìm voucher theo hạng/điều kiện
- `GetVoucherByRequestId` — lấy voucher theo requestId
- `GetVoucherById` — lấy voucher theo id
- `GetMockInvoices` — lấy hóa đơn mẫu

### 3.2. Sơ đồ gọi gRPC giữa các service

| Caller (client) | Callee (server) | Method | Mục đích |
|---|---|---|---|
| Identity Service | Customer Service | CreateCustomerProfile | Tạo profile khi đăng ký customer |
| Voucher Service | Identity Service | GetPartner / CheckNameStore | Lấy tên cửa hàng, thông tin partner |
| Voucher Service | Loyalty Service | CreateMission / UpdateMissionStatus | Tạo & đồng bộ mission entity |
| Voucher Service | Customer Service | GetVoucherUsageStats | Lấy số liệu sử dụng cho dashboard |
| Customer Service | Voucher Service | SearchVoucher / GetVoucherById | Lấy voucher theo hạng, chi tiết voucher |
| Customer Service | Loyalty Service | GetMissionById / SearchMission | Lấy mission, kiểm tra reward khi claim |

---

## 4. Giao tiếp Kafka (bất đồng bộ)

Kafka dùng cho các luồng không cần response ngay, đảm bảo độ tin cậy và decoupling. Customer Service có cơ chế retry qua `RetryableKafkaMessage`.

| Topic | Producer | Consumer | Mục đích |
|---|---|---|---|
| `loyalty-point-topic` | Customer Service | Loyalty Service | Sau thanh toán → tích điểm, cập nhật tiến độ mission |
| `tier-upgrade-topic` | Loyalty Service | Customer Service | Khi đạt ngưỡng điểm → nâng hạng, cập nhật profile |
| `voucher-used-topic` | Customer Service | Voucher Service | Khi dùng voucher → giảm stock |

### Event chính

- **LoyaltyPointEvent**: Customer → Loyalty. Chứa customerId, số tiền giao dịch để tính điểm (1 point / 1.000 VND).
- **TierUpgradeEvent**: Loyalty → Customer. Phát khi khách hàng đạt ngưỡng nâng hạng (GOLD ≥ 1.000, PLATINUM ≥ 5.000, DIAMOND ≥ 10.000 điểm).
- **VoucherUsedEvent**: Customer → Voucher. Cập nhật giảm số lượng tồn của voucher.

---

## 5. Các luồng nghiệp vụ tích hợp (end-to-end)

### 5.1. Luồng phát hành & sử dụng Voucher

```
Maker/Partner: POST /api/v1/vouchers (tạo, INIT)
   → PUT /{id}/submit (PENDING_APPROVE)
Checker: PUT /{id}/confirm (APPROVED → batch activate → FINISH)
   [gRPC] Voucher → Identity: lấy storeName (nếu Partner)
Customer: GET /api/customers/vouchers/available (xem theo hạng)
   [gRPC] Customer → Voucher: SearchVoucher
   → POST /collect/{voucherId} (thu thập)
   → POST /api/v1/payments/process (thanh toán, áp voucher)
   [Kafka] Customer → loyalty-point-topic (tích điểm)
   [Kafka] Customer → voucher-used-topic (giảm stock)
```

### 5.2. Luồng Mission

```
Maker/Partner: POST /api/v1/missions (tạo)
   [gRPC] Voucher → Loyalty: CreateMission
   → PUT /{id}/submit → Checker: PUT /{id}/confirm (ACTIVE)
Customer: thanh toán → [Kafka loyalty-point-topic]
   Loyalty: cập nhật progress → COMPLETED
Customer: POST /api/customers/missions/claim-reward
   [gRPC] Customer → Loyalty: GetMissionById (lấy rewardType)
   → cộng điểm hoặc tạo voucher thưởng → CLAIMED
```

### 5.3. Luồng nâng hạng (event-driven)

```
Customer: thanh toán
   [Kafka] Customer → loyalty-point-topic
Loyalty: tính điểm → cộng totalPoints → kiểm tra ngưỡng
   [Kafka] Loyalty → tier-upgrade-topic (nếu đạt)
Customer: TierUpgradeConsumer → cập nhật tier trong profile
```

---

## 6. Mã lỗi thường gặp

| Mã lỗi | Ý nghĩa |
|---|---|
| `VALIDATION_ERROR` | Dữ liệu đầu vào không hợp lệ |
| `INVALID_CUSTOMER_TIER` | Thiếu hoặc sai hạng khách hàng |
| `VOUCHER_ALREADY_COLLECTED` | Đã thu thập voucher này |
| `VOUCHER_OUT_OF_STOCK` | Voucher hết số lượng |
| `VOUCHER_EXPIRED` | Voucher hết hạn |
| `MIN_ORDER_NOT_MET` | Đơn hàng chưa đạt giá trị tối thiểu |
| `MISSION_NOT_COMPLETED` | Nhiệm vụ chưa hoàn thành |
| `REWARD_ALREADY_CLAIMED` | Đã nhận thưởng |
| `UNAUTHORIZED` | Chưa xác thực hoặc token không hợp lệ |
| `NOT_FOUND` | Không tìm thấy tài nguyên |
