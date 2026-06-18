# CHƯƠNG 3. PHÁT TRIỂN HỆ THỐNG

## 3.1. Tổng quan sản phẩm sau khi triển khai

Hệ thống "Quản lý Voucher và Chương trình Khách hàng Thân thiết" đã được triển khai hoàn chỉnh với Docker Compose, bao gồm 15+ container hoạt động ổn định. Sản phẩm đáp ứng đầy đủ các yêu cầu chức năng và phi chức năng đã đề ra ở Chương 2, phục vụ 5 nhóm người dùng (Admin, Maker, Checker, Partner, Customer) với giao diện quản trị web và API cho ứng dụng khách hàng.

## 3.2. Kiến trúc tổng thể của hệ thống

### 3.2.1. Mô hình triển khai tổng quát

<!-- Chèn sơ đồ kiến trúc triển khai tại đây -->

Hình 3.1: Sơ đồ kiến trúc triển khai hệ thống

Bảng 3.1: Các thành phần trong hệ thống

| Thành phần | Port | Vai trò |
|---|---|---|
| Kong API Gateway | 8000 | Điểm vào duy nhất, routing, CORS, logging |
| Identity Service | 8081 | Xác thực, phân quyền, quản lý user |
| Voucher Service | 8082 | Quản lý voucher, mission, audit log |
| Customer Service | 8084 | Phục vụ khách hàng (voucher, payment, leaderboard) |
| Loyalty Service | 9093 (gRPC) | Tích điểm, nâng hạng, quản lý mission entity |
| Notification Service | internal | Xử lý thông báo (Kafka consumer) |
| PostgreSQL | 5432 | Cơ sở dữ liệu quan hệ |
| Redis | 6379 | Cache, leaderboard |
| Apache Kafka | 9092 | Message broker bất đồng bộ |
| Keycloak | 8180 | Máy chủ xác thực OAuth2/OIDC |
| Elasticsearch | 9200 | Lưu trữ và index log |
| Kibana | 5601 | Giao diện visualize log |
| Prometheus | 9090 | Thu thập metrics |
| Grafana | 3002 | Dashboard monitoring |

### 3.2.2. Luồng xử lý yêu cầu từ người dùng

1. Client gửi request đến Kong Gateway (:8000).
2. Kong route request đến service đích dựa trên path prefix.
3. Service verify JWT token bằng Keycloak public key.
4. Spring Security kiểm tra role qua @PreAuthorize.
5. Business logic xử lý, truy vấn database/cache.
6. Nếu cần dữ liệu từ service khác: gọi gRPC.
7. Nếu cần xử lý bất đồng bộ: publish Kafka event.
8. Trả response cho client qua Kong.

### 3.2.3. Giao tiếp giữa các service

<!-- Chèn sơ đồ giao tiếp tại đây -->

Hình 3.2: Sơ đồ giao tiếp giữa các Microservice

Bảng 3.2: Giao tiếp đồng bộ (gRPC)

| Caller | Callee | Method | Mục đích |
|---|---|---|---|
| Voucher Service | Identity Service | getNameStore() | Lấy tên cửa hàng Partner |
| Voucher Service | Loyalty Service | createMission() | Tạo mission entity |
| Voucher Service | Loyalty Service | updateMissionStatus() | Đồng bộ trạng thái |
| Customer Service | Voucher Service | searchVouchersByTier() | Lấy voucher theo hạng |
| Customer Service | Loyalty Service | getMissions() | Lấy danh sách mission |

Bảng 3.3: Giao tiếp bất đồng bộ (Kafka)

| Topic | Producer | Consumer | Mục đích |
|---|---|---|---|
| loyalty-point-topic | Customer Service | Loyalty Service | Tích điểm sau thanh toán |
| tier-upgrade-topic | Loyalty Service | Customer Service | Thông báo nâng hạng |
| voucher-used-topic | Customer Service | Voucher Service | Cập nhật stock voucher |

### 3.2.4. Sơ đồ triển khai (Docker Compose)

Bảng 3.4: Cấu hình resource Docker

| Container | Memory | Lý do |
|---|---|---|
| PostgreSQL | 256M | Buffer pool cho database |
| Redis | 128M | In-memory cache (64MB data) |
| Kafka + Zookeeper | 768M | Page cache cho message broker |
| Keycloak | 384M | JVM-based auth server |
| Elasticsearch | 512M | Full-text search engine |
| Application services | 256M/service | Spring Boot JVM (Xms128m-Xmx192m) |
| Kong | 128M | Nginx-based, rất nhẹ |

## 3.3. Phát triển frontend

### 3.3.1. Công nghệ sử dụng ở frontend

Bảng 3.5: Công nghệ Frontend

| Công nghệ | Phiên bản | Vai trò |
|---|---|---|
| Next.js | 16.x | Framework React fullstack (App Router, API Routes) |
| React | 19.x | Thư viện xây dựng UI (component-based) |
| TypeScript | 5.x | Type-safe, phát hiện lỗi compile-time |
| Tailwind CSS | 4.x | Utility-first CSS, responsive design |
| shadcn/ui (Radix UI) | Latest | Accessible UI components |
| Zustand | 5.x | State management (auth store) |
| Axios | 1.x | HTTP client với interceptor auto-refresh |

### 3.3.2. Cấu trúc mã nguồn frontend

```
app/
  api/              → API proxy routes (BFF pattern)
  dashboard/        → Các trang chính (vouchers, missions, partners...)
  login/            → Trang đăng nhập
components/
  ui/               → shadcn/ui components (Button, Dialog, Table...)
  dashboard/        → Navigation sidebar
lib/
  api/
    axios.ts        → Axios instance + auto refresh interceptor
    services/       → Service layer (voucherService, missionService...)
  auth.ts           → Zustand auth store (persist localStorage)
  types.ts          → TypeScript interfaces
```

### 3.3.3. Kiến trúc BFF (Backend-For-Frontend)

<!-- Chèn sơ đồ kiến trúc Frontend tại đây -->

Hình 3.3: Sơ đồ kiến trúc Frontend (BFF Pattern)

Frontend không gọi trực tiếp đến microservices mà thông qua Next.js API Routes, giúp ẩn URL backend, tập trung xử lý authentication header, và dễ dàng chuyển đổi backend service mà không ảnh hưởng client code.

### 3.3.4. Quản lý phiên và xác thực

Luồng xác thực:
1. User đăng nhập → Next.js API route gọi Identity Service → trả accessToken + refreshToken.
2. Zustand store lưu token vào localStorage (persist).
3. Axios interceptor tự động gắn Bearer token vào mọi request.
4. Khi token hết hạn (401) → interceptor gọi refresh endpoint → cập nhật token mới → retry request.
5. Nếu refresh thất bại → force logout → redirect trang login.

### 3.3.5. Điều hướng theo vai trò

Sau khi login, frontend gọi API allowed-pages với roles của user. Sidebar navigation chỉ hiển thị các trang user được phép truy cập. Mỗi trang wrap trong AuthGuard kiểm tra quyền trước khi render.

## 3.4. Phát triển backend

### 3.4.1. Công nghệ sử dụng ở backend

Bảng 3.6: Công nghệ Backend

| Công nghệ | Phiên bản | Vai trò |
|---|---|---|
| Java | 17 (LTS) | Ngôn ngữ lập trình chính |
| Spring Boot | 3.4.4 | Framework microservices |
| Spring Security | 6.x | OAuth2 Resource Server, RBAC |
| Spring Data JPA | 3.x | ORM, Repository pattern |
| PostgreSQL | 15 | Cơ sở dữ liệu quan hệ (ACID) |
| Redis | 7 | Cache layer (leaderboard, voucher list) |
| Apache Kafka | 3.x | Message broker bất đồng bộ |
| gRPC + Protobuf | 1.6x | Inter-service communication |
| Kong | 3.6 | API Gateway |
| Keycloak | 23.0 | IAM (OAuth2/OIDC) |
| Prometheus + Grafana | Latest | Monitoring |
| ELK Stack | 8.x | Logging tập trung |
| Docker Compose | 3.8 | Container orchestration |

### 3.4.2. Tổ chức mã nguồn backend (Multi-module Maven)

```
backend-parent/
├── pom.xml                  (Parent POM, quản lý version chung)
├── common/                  (BaseResponse, BaseException, BaseErrorCode)
├── proto/                   (gRPC .proto files → generated Java code)
├── identity-service/        (Port 8081, gRPC 9091)
├── voucher-service/         (Port 8082, gRPC 9099)
├── customer-service/        (Port 8084, gRPC 9094)
├── loyalty-service/         (gRPC 9093, internal only)
├── notification-service/    (Kafka consumer only)
├── api-gateway/             (Kong YAML config)
├── docker-compose.yml
├── scripts/                 (SQL init, seed data)
└── infra/                   (Prometheus, Logstash config)
```

### 3.4.3. Module Identity Service

Chức năng: Xác thực, phân quyền, quản lý user/partner.

Bảng 3.7: API Endpoints – Identity Service

| Method | Endpoint | Chức năng | Quyền |
|---|---|---|---|
| POST | /api/v1/auth/login | Đăng nhập | Public |
| POST | /api/v1/auth/register | Đăng ký customer | Public |
| POST | /api/v1/auth/refresh | Refresh token | Public |
| GET | /api/v1/profile | Xem profile | Authenticated |
| PUT | /api/v1/profile | Cập nhật profile | Authenticated |
| PUT | /api/v1/profile/password | Đổi mật khẩu | Authenticated |
| GET | /api/v1/system-users | Danh sách users | ADMIN |
| POST | /api/v1/system-users | Tạo user | ADMIN |
| GET | /api/v1/roles | Danh sách roles | ADMIN |

Đặc điểm: Tích hợp Keycloak Admin REST API, cung cấp gRPC Server (getNameStore, getPartner).

### 3.4.4. Module Voucher Service

Chức năng: Quản lý vòng đời voucher và mission.

Bảng 3.8: API Endpoints – Voucher Service

| Method | Endpoint | Chức năng | Quyền |
|---|---|---|---|
| POST | /api/v1/vouchers | Tạo voucher đơn lẻ | MAKER, PARTNER |
| POST | /api/v1/vouchers/excel | Tạo voucher Excel | MAKER, PARTNER |
| GET | /api/v1/vouchers | Danh sách requests | MAKER, CHECKER, PARTNER |
| PUT | /api/v1/vouchers/{id}/submit | Gửi phê duyệt | MAKER, PARTNER |
| PUT | /api/v1/vouchers/{id}/confirm | Approve/Reject | CHECKER |
| PUT | /api/v1/vouchers/{id}/cancel | Hủy yêu cầu | MAKER, PARTNER |
| POST | /api/v1/missions/missions | Tạo mission | MAKER, PARTNER |
| GET | /api/v1/missions/search | Tìm kiếm missions | ALL |
| GET | /api/v1/audit-logs | Xem audit log | ADMIN, CHECKER |

Đặc điểm: Batch processing 100 records, Excel import (Apache POI), audit logging (@Async), data isolation (Partner), Strategy Pattern (FIXED/PERCENT).

### 3.4.5. Module Customer Service

Chức năng: Phục vụ khách hàng cuối.

Bảng 3.9: API Endpoints – Customer Service

| Method | Endpoint | Chức năng | Quyền |
|---|---|---|---|
| GET | /api/customers/profile/{id} | Xem profile | CUSTOMER |
| GET | /api/customers/vouchers/available/with-status | Voucher khả dụng | CUSTOMER |
| POST | /api/customers/vouchers/collect/{id} | Thu thập voucher | CUSTOMER |
| GET | /api/customers/vouchers/list | Kho voucher | CUSTOMER |
| GET | /api/customers/vouchers/applicable | Voucher áp dụng được | CUSTOMER |
| GET | /api/customers/missions | Danh sách mission | CUSTOMER |
| POST | /api/customers/missions/claim-reward | Nhận thưởng | CUSTOMER |
| POST | /api/v1/payments/process | Thanh toán | CUSTOMER |
| GET | /api/customers/leaderboard | Bảng xếp hạng | CUSTOMER |

Đặc điểm: gRPC client (Voucher, Loyalty), Kafka producer/consumer, Redis Sorted Set (leaderboard).

### 3.4.6. Module Loyalty Service

Chức năng: Quản lý điểm thưởng, hạng thành viên, mission entity.

Giao tiếp: Chỉ qua gRPC (server) và Kafka (consumer/producer), không expose REST API.

gRPC methods: CreateMission, UpdateMissionStatus, GetMissionById, SearchMissions.

Kafka consumer: Lắng nghe loyalty-point-topic → tính điểm → kiểm tra nâng hạng → publish tier-upgrade-topic nếu cần.

### 3.4.7. Design Patterns

Bảng 3.10: Design Patterns sử dụng

| Pattern | Vị trí | Mục đích |
|---|---|---|
| Strategy | Voucher Service | Xử lý 2 loại giảm giá (FIXED, PERCENT) |
| Specification | Voucher/Customer Service | Dynamic query với nhiều filter |
| Builder | Tất cả service | Tạo response DTO (Lombok @Builder) |
| Template Method | Voucher Service | Batch processing (approve/reject) |

### 3.4.8. Bảo mật và phân quyền

Luồng xác thực: Client → Kong → Target Service → verify JWT (Keycloak public key) → @PreAuthorize check role.

Phân quyền chi tiết: Ngoài role-based, service layer kiểm tra thêm data-level access (Partner chỉ xem dữ liệu của mình, Checker chỉ thấy trạng thái phù hợp).

### 3.4.9. Xử lý lỗi thống nhất

Cơ chế: BaseException → GlobalExceptionHandler → BaseResponse format chuẩn.

```json
{ "status": 0, "code": "success", "message": "Success", "data": {...} }
{ "status": 1003, "code": "NOT_FOUND", "message": "Resource not found", "data": null }
```

### 3.4.9. Đánh chỉ mục (Index) cơ sở dữ liệu

Hệ thống quản lý voucher và chương trình khách hàng thân thiết có nhiều truy vấn lọc phức tạp: danh sách voucher theo trạng thái, audit log theo thời gian, voucher của khách hàng theo nhiều điều kiện. Nếu không có index phù hợp, các truy vấn này sẽ thực hiện full table scan, gây chậm nghiêm trọng khi dữ liệu tăng. Việc đánh index đúng cột giúp giảm thời gian truy vấn từ O(n) xuống O(log n), đảm bảo response time dưới 500ms theo yêu cầu phi chức năng.

Bảng 3.12: Danh sách các index quan trọng trong hệ thống

| Bảng | Cột | Loại Index | Mục đích |
|---|---|---|---|
| voucher_requests | status | B-Tree | Lọc danh sách theo trạng thái (PENDING_APPROVE, APPROVED...) |
| voucher_requests | created_by | B-Tree | Maker/Partner xem voucher do mình tạo |
| voucher_requests | store_name | B-Tree | Lọc voucher theo cửa hàng Partner |
| voucher_requests | created_at | B-Tree | Sắp xếp theo thời gian tạo |
| voucher_detail | request_id | B-Tree | Join với voucher_requests, lấy chi tiết theo request |
| voucher_detail | voucher_code | Unique | Tìm kiếm nhanh theo mã voucher, đảm bảo không trùng |
| voucher_detail | customer_tier | B-Tree | Lọc voucher theo hạng khách hàng (gRPC searchByTier) |
| voucher_detail | voucher_status | B-Tree | Lọc voucher đang ACTIVE cho khách hàng |
| voucher_detail | start_date, end_date | B-Tree | Kiểm tra voucher còn hiệu lực |
| customer_vouchers | customer_id | B-Tree | Lấy kho voucher của một khách hàng |
| customer_vouchers | voucher_id | B-Tree | Kiểm tra khách hàng đã collect voucher chưa |
| customer_vouchers | status | B-Tree | Lọc voucher AVAILABLE/USED/EXPIRED |
| customer_profiles | user_id | Unique | Lookup profile theo userId từ JWT token |
| transactions | customer_id | B-Tree | Lịch sử giao dịch của khách hàng |
| transactions | created_at | B-Tree | Sắp xếp giao dịch theo thời gian |
| audit_logs | entity_type | B-Tree | Lọc log theo loại entity (VOUCHER, MISSION) |
| audit_logs | performed_by | B-Tree | Xem log theo người thực hiện |
| audit_logs | created_at | B-Tree | Sắp xếp log theo thời gian |
| tasks (missions) | task_status | B-Tree | Lọc mission theo trạng thái (ACTIVE, FINISH) |
| tasks (missions) | partner_id | B-Tree | Partner xem mission do mình tạo |

Ngoài các index đơn cột, hệ thống sử dụng composite index cho các truy vấn kết hợp nhiều điều kiện thường gặp:

Bảng 3.13: Composite Index cho các truy vấn phổ biến

| Bảng | Các cột | Mục đích |
|---|---|---|
| voucher_requests | (status, created_by) | Maker lọc voucher của mình theo trạng thái |
| voucher_detail | (voucher_status, customer_tier) | Customer xem voucher ACTIVE theo hạng |
| voucher_detail | (request_id, request_status) | Batch processing khi approve/reject |
| customer_vouchers | (customer_id, status) | Lấy voucher khả dụng của khách hàng |
| customer_vouchers | (customer_id, voucher_id) | Kiểm tra trùng khi collect voucher |
| transactions | (customer_id, created_at) | Lịch sử giao dịch sắp xếp theo thời gian |
| audit_logs | (entity_type, created_at) | Xem audit log theo loại entity và thời gian |

Trong Spring Data JPA, các index được khai báo trực tiếp trên entity class thông qua annotation `@Table(indexes = {...})` với `@Index`. Ví dụ:

```java
@Table(name = "voucher_requests", indexes = {
    @Index(name = "idx_vr_status", columnList = "status"),
    @Index(name = "idx_vr_created_by", columnList = "created_by"),
    @Index(name = "idx_vr_status_created_by", columnList = "status, created_by")
})
public class VoucherRequestEntity { ... }
```

Cách tiếp cận này cho phép JPA/Hibernate tự động tạo index khi khởi tạo schema (ddl-auto = update), đồng thời giữ định nghĩa index gần với entity code, dễ review và bảo trì.

## 3.5. Tích hợp các luồng nghiệp vụ chính

### 3.5.1. Luồng phân phối Voucher (end-to-end)

Maker tạo voucher (REST) → Submit → Checker approve → System batch activate (VoucherDetail ACTIVE) → Customer xem (gRPC searchByTier) → Collect (lưu CustomerVoucher) → Thanh toán (apply discount, publish Kafka) → Loyalty tích điểm.

### 3.5.2. Luồng Mission (end-to-end)

Maker tạo mission (REST) → Voucher Service tạo voucher REWARD → gRPC createMission (Loyalty) → Submit → Checker approve → Mission ACTIVE → Customer thanh toán → Kafka event → Loyalty cập nhật progress → Khi đạt target: COMPLETED → Customer claim reward → Nhận điểm hoặc voucher.

### 3.5.3. Luồng nâng hạng (event-driven)

Customer thanh toán → Kafka LoyaltyPointEvent → Loyalty Service tính điểm (1 point/1.000 VND) → Cộng totalPoints → Kiểm tra ngưỡng (GOLD≥1000, PLATINUM≥5000, DIAMOND≥10000) → Nếu đạt: Kafka TierUpgradeEvent → Customer Service cập nhật tier.

## 3.6. Đánh giá kết quả phát triển hệ thống

Bảng 3.11: Đánh giá đáp ứng yêu cầu

| Yêu cầu | Kết quả | Ghi chú |
|---|---|---|
| 22 chức năng (Bảng 2.1) | Đáp ứng đầy đủ | Tất cả API hoạt động đúng |
| Response time < 500ms | Đạt | Cache Redis, gRPC binary |
| Không mất message | Đạt | Kafka durability |
| Phân quyền RBAC | Đạt | Keycloak + Spring Security |
| Monitoring real-time | Đạt | Prometheus + Grafana |
| Logging tập trung | Đạt | ELK Stack |
| Container hóa | Đạt | Docker Compose 15+ containers |

## 3.7. Kết luận chương 3

Chương 3 đã trình bày chi tiết quá trình phát triển hệ thống bao gồm: kiến trúc triển khai với 14 thành phần, phát triển frontend với kiến trúc BFF (Next.js + React + TypeScript), phát triển backend với 5 microservices (Java 17 + Spring Boot 3.4), tích hợp 3 luồng nghiệp vụ chính (voucher, mission, nâng hạng), và đánh giá kết quả đáp ứng đầy đủ yêu cầu đã đề ra.
