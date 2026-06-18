# CHƯƠNG 4. BẢO MẬT HỆ THỐNG

## 4.1. Tổng quan bảo mật

### 4.1.1. Mục tiêu bảo mật

Hệ thống quản lý Voucher và Chương trình Khách hàng Thân thiết xử lý dữ liệu nhạy cảm bao gồm thông tin tài khoản người dùng, giao dịch thanh toán, và quy trình phê duyệt voucher có giá trị kinh tế. Việc đảm bảo bảo mật là yêu cầu bắt buộc để bảo vệ lợi ích của doanh nghiệp và khách hàng.

Các mục tiêu bảo mật chính:

- Xác thực (Authentication): Đảm bảo chỉ người dùng hợp lệ mới truy cập được hệ thống.
- Phân quyền (Authorization): Mỗi vai trò chỉ thực hiện được các thao tác được phép.
- Toàn vẹn dữ liệu (Integrity): Ngăn chặn thay đổi trái phép dữ liệu voucher, điểm thưởng, giao dịch.
- Bảo mật truyền tải (Confidentiality): Dữ liệu không bị đọc trộm khi truyền giữa client và server.
- Khả năng kiểm toán (Auditability): Mọi thao tác quan trọng đều được ghi log để truy vết.

### 4.1.2. Xác thực bằng JWT (OAuth2/OIDC)

Hệ thống sử dụng Keycloak làm máy chủ xác thực (Authorization Server) theo chuẩn OAuth2/OpenID Connect [14]. Đây là giải pháp IAM mã nguồn mở do Red Hat phát triển, được sử dụng rộng rãi trong các hệ thống enterprise.

Lý do chọn JWT Bearer Token thay vì Cookie-based Session:

- Hệ thống là REST API thuần (stateless), không render HTML phía server.
- Microservices architecture: Mỗi service cần verify token độc lập mà không cần shared session store.
- Hỗ trợ đa client: Web admin (Next.js), mobile app, và API integration đều sử dụng cùng cơ chế Bearer token.
- Horizontal scaling: Không cần sticky session, bất kỳ instance nào cũng verify được token bằng public key.

Cấu trúc JWT Token:

```json
{
  "header": { "alg": "RS256", "typ": "JWT" },
  "payload": {
    "sub": "user-uuid",
    "preferred_username": "maker01",
    "realm_access": { "roles": ["MAKER"] },
    "exp": 1700000000,
    "iat": 1699996400
  }
}
```

Token được ký bằng RSA private key của Keycloak. Các microservice verify bằng public key (lấy từ Keycloak JWKS endpoint), không cần gọi lại Keycloak mỗi request.

### 4.1.3. Luồng xác thực chi tiết

Luồng đăng nhập (Resource Owner Password Credentials):

1. Client gửi POST /api/v1/auth/login với username + password.
2. Identity Service gọi Keycloak Token Endpoint (grant_type=password).
3. Keycloak verify credentials, trả về accessToken (TTL 5 phút) + refreshToken (TTL 30 phút).
4. Identity Service trả token cho client.
5. Client lưu token và gắn vào header: `Authorization: Bearer <accessToken>`.

Luồng refresh token:

1. Khi accessToken hết hạn, client nhận HTTP 401.
2. Client gửi POST /api/v1/auth/refresh với refreshToken.
3. Identity Service gọi Keycloak refresh endpoint.
4. Keycloak trả accessToken mới + refreshToken mới (rotation).
5. Nếu refreshToken cũng hết hạn → force logout.

Luồng verify token tại mỗi service:

1. Request đến Kong Gateway → route đến target service.
2. Spring Security OAuth2 Resource Server filter intercept request.
3. Lấy token từ Authorization header.
4. Verify signature bằng Keycloak public key (cached).
5. Parse claims: userId, username, roles.
6. Tạo SecurityContext với Authentication object.
7. Tiếp tục vào Controller → @PreAuthorize kiểm tra role.

### 4.1.4. Phân quyền theo vai trò (RBAC)

Hệ thống triển khai Role-Based Access Control với 5 vai trò được quản lý trong Keycloak và nhúng vào JWT token. Spring Security sử dụng annotation `@PreAuthorize` để kiểm tra quyền tại method level.

Bảng 4.1: Ma trận phân quyền theo vai trò

| Chức năng | ADMIN | MAKER | CHECKER | PARTNER | CUSTOMER |
|---|---|---|---|---|---|
| Quản lý tài khoản hệ thống | ✓ | | | | |
| Quản lý vai trò | ✓ | | | | |
| Xem audit log | ✓ | | ✓ | | |
| Tạo voucher | | ✓ | | ✓ | |
| Tạo mission | | ✓ | | ✓ | |
| Gửi phê duyệt (Submit) | | ✓ | | ✓ | |
| Hủy yêu cầu (Cancel) | | ✓ | | ✓ | |
| Phê duyệt/Từ chối | | | ✓ | | |
| Xem voucher khả dụng | | | | | ✓ |
| Thu thập voucher | | | | | ✓ |
| Thanh toán | | | | | ✓ |
| Tham gia mission | | | | | ✓ |
| Xem leaderboard | | | | | ✓ |
| Xem/Sửa profile cá nhân | ✓ | ✓ | ✓ | ✓ | ✓ |

Ví dụ cấu hình Spring Security:

```java
@PreAuthorize("hasRole('CHECKER')")
@PutMapping("/{id}/confirm")
public BaseResponse<VoucherResponse> confirmVoucher(...) { ... }

@PreAuthorize("hasAnyRole('MAKER', 'PARTNER')")
@PostMapping
public BaseResponse<VoucherResponse> createVoucher(...) { ... }
```

### 4.1.5. Phân quyền theo phạm vi dữ liệu

Ngoài RBAC, hệ thống triển khai data-level access control tại service layer để đảm bảo người dùng chỉ truy cập dữ liệu thuộc phạm vi của mình:

Partner chỉ xem dữ liệu của mình: Khi Partner gọi API danh sách voucher, service layer tự động thêm điều kiện `createdBy = currentUserId` vào query. Partner không thể xem voucher do Maker hoặc Partner khác tạo.

Checker chỉ thấy trạng thái phù hợp: Checker chỉ xem được các voucher request ở trạng thái PENDING_APPROVE, APPROVED, REJECTED, FINISH. Các request ở trạng thái INIT (chưa submit) không hiển thị cho Checker.

Customer chỉ xem voucher theo hạng: Customer hạng GOLD chỉ thấy voucher có customerTier = ALL hoặc customerTier = GOLD. Không thấy voucher dành riêng cho PLATINUM hoặc DIAMOND.

```java
// Ví dụ data-level filtering cho Partner
if (currentUser.getRole() == Role.PARTNER) {
    spec = spec.and((root, query, cb) -> 
        cb.equal(root.get("createdBy"), currentUser.getId()));
}
```

### 4.1.6. Chính sách mật khẩu

Chính sách mật khẩu được cấu hình tập trung trên Keycloak, áp dụng cho tất cả người dùng trong realm:

- Độ dài tối thiểu: 6 ký tự.
- Keycloak lưu mật khẩu dưới dạng hash (PBKDF2 với SHA256).
- Hỗ trợ đổi mật khẩu qua API PUT /api/v1/profile/password (yêu cầu nhập mật khẩu cũ).
- Identity Service gọi Keycloak Admin API để thực hiện đổi mật khẩu.

### 4.1.7. Bảo mật API Gateway (Kong)

Kong API Gateway đóng vai trò điểm vào duy nhất (single entry point) cho tất cả request từ client, cung cấp các lớp bảo mật:

CORS (Cross-Origin Resource Sharing): Cấu hình plugin cors trên Kong để chỉ cho phép frontend domain truy cập API. Ngăn chặn request từ domain không được phép.

```yaml
plugins:
  - name: cors
    config:
      origins: ["http://localhost:3000"]
      methods: ["GET", "POST", "PUT", "DELETE", "OPTIONS"]
      headers: ["Authorization", "Content-Type"]
      credentials: true
```

Rate Limiting: Sử dụng Redis làm backend cho rate limiting, giới hạn số request mỗi client trong khoảng thời gian nhất định. Bảo vệ hệ thống khỏi brute-force attack và DDoS cơ bản.

Logging: Plugin tcp-log ghi lại toàn bộ access log (IP, method, path, status code, response time) gửi đến Logstash để phân tích và phát hiện bất thường.

Routing tập trung: Client không biết địa chỉ thực của các microservice. Chỉ Kong biết internal routing, giảm attack surface.

### 4.1.8. Bảo mật giao tiếp inter-service

Giao tiếp giữa các microservice trong hệ thống sử dụng hai cơ chế:

gRPC (đồng bộ): Các service giao tiếp qua gRPC trên mạng Docker internal network. Chỉ các container trong cùng Docker network mới truy cập được port gRPC (9091, 9093, 9094, 9099). Không expose port gRPC ra bên ngoài.

Apache Kafka (bất đồng bộ): Kafka broker chạy trong Docker internal network, không expose port 9092 ra public. Consumer xác thực bằng group.id, đảm bảo mỗi message chỉ được xử lý một lần bởi đúng service.

Cả hai cơ chế đều được bảo vệ bởi Docker network isolation: các service chỉ giao tiếp được với nhau thông qua Docker internal network, không thể truy cập từ bên ngoài host machine.

### 4.1.9. Toàn vẹn dữ liệu nghiệp vụ

Hệ thống đảm bảo toàn vẹn dữ liệu thông qua nhiều cơ chế:

Quy trình Maker/Checker: Voucher và mission phải qua bước phê duyệt trước khi kích hoạt. Maker không thể tự approve voucher của mình. Điều này ngăn chặn việc phát hành voucher trái phép hoặc sai giá trị.

Audit Log: Mọi thao tác quan trọng (tạo, submit, approve, reject, cancel voucher/mission) đều được ghi vào bảng audit_logs với thông tin: ai thực hiện (performedBy), thời gian (timestamp), hành động (action), đối tượng (entityType, entityId), chi tiết thay đổi (details). Audit log được ghi bất đồng bộ (@Async) để không ảnh hưởng hiệu năng.

PostgreSQL ACID: Các thao tác quan trọng (thu thập voucher, thanh toán) sử dụng transaction để đảm bảo tính nguyên tử. Ví dụ khi thanh toán: giảm stock voucher, tạo transaction record, và publish Kafka event phải thành công cùng nhau hoặc rollback toàn bộ.

Kafka Durability: Message tích điểm và nâng hạng được lưu trên disk với replication, đảm bảo không mất dữ liệu khi broker crash.

### 4.1.10. Hỗ trợ bảo mật ở frontend

Frontend (Next.js) triển khai các cơ chế bảo mật phía client:

Zustand Auth Store: Lưu trữ token trong localStorage thông qua Zustand persist middleware. Store quản lý trạng thái đăng nhập, thông tin user, và token.

```typescript
// lib/auth.ts - Zustand auth store
const useAuthStore = create(persist((set) => ({
  accessToken: null,
  refreshToken: null,
  user: null,
  setAuth: (data) => set({ ...data }),
  logout: () => set({ accessToken: null, refreshToken: null, user: null }),
}), { name: 'auth-storage' }));
```

Axios Interceptor Auto-Refresh: Axios instance được cấu hình interceptor tự động xử lý token hết hạn. Khi nhận HTTP 401, interceptor gọi refresh endpoint, cập nhật token mới, và retry request ban đầu. Nếu refresh thất bại → force logout.

```typescript
// lib/api/axios.ts - Auto refresh interceptor
axiosInstance.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401 && !error.config._retry) {
      error.config._retry = true;
      const newToken = await refreshToken();
      if (newToken) {
        error.config.headers.Authorization = `Bearer ${newToken}`;
        return axiosInstance(error.config);
      }
      logout(); // Force logout nếu refresh thất bại
    }
    return Promise.reject(error);
  }
);
```

AuthGuard Component: Mỗi trang được wrap trong AuthGuard kiểm tra quyền trước khi render. Nếu user không có quyền truy cập trang → redirect về dashboard hoặc login.

BFF Pattern (Backend-For-Frontend): Frontend không gọi trực tiếp đến microservices. Mọi request đi qua Next.js API Routes, giúp ẩn URL backend thực và tập trung xử lý authentication header.

### 4.1.11. Đánh giá giải pháp bảo mật

Bảng 4.2: Tổng hợp các biện pháp bảo mật trong hệ thống

| STT | Lớp bảo mật | Giải pháp | Công nghệ | Mục đích |
|---|---|---|---|---|
| 1 | Xác thực | OAuth2/OIDC + JWT | Keycloak 23.0 | Xác minh danh tính người dùng |
| 2 | Phân quyền API | RBAC với @PreAuthorize | Spring Security 6.x | Kiểm soát truy cập theo vai trò |
| 3 | Phân quyền dữ liệu | Data-level filtering | Service layer (Specification) | Partner/Customer chỉ xem dữ liệu của mình |
| 4 | Bảo mật gateway | CORS, Rate Limiting, Logging | Kong + Redis | Bảo vệ entry point, chống brute-force |
| 5 | Bảo mật mạng | Docker network isolation | Docker Compose | Cô lập giao tiếp inter-service |
| 6 | Mật khẩu | Hash PBKDF2-SHA256, min 6 ký tự | Keycloak | Bảo vệ credentials |
| 7 | Token management | Access (5p) + Refresh (30p) rotation | Keycloak + Axios interceptor | Giảm thiểu rủi ro token bị đánh cắp |
| 8 | Kiểm toán | Audit log mọi thao tác | PostgreSQL + @Async | Truy vết và phát hiện bất thường |
| 9 | Toàn vẹn dữ liệu | Maker/Checker workflow | Business logic | Ngăn phát hành voucher trái phép |
| 10 | Frontend | AuthGuard + BFF pattern | Next.js + Zustand | Bảo vệ phía client |

Đánh giá ưu điểm:

- Sử dụng chuẩn công nghiệp (OAuth2/OIDC) thay vì tự xây dựng cơ chế xác thực, giảm rủi ro lỗ hổng bảo mật.
- Phân tách rõ ràng giữa xác thực (Keycloak) và phân quyền (Spring Security), dễ bảo trì và mở rộng.
- Multi-layer security: Bảo mật được triển khai ở nhiều lớp (gateway, service, database), không phụ thuộc vào một điểm duy nhất.
- Stateless architecture: JWT cho phép horizontal scaling mà không cần shared session.

Đánh giá hạn chế và hướng cải thiện:

- Chưa triển khai HTTPS (TLS) trong môi trường development (Docker Compose). Khi deploy production cần cấu hình TLS certificate cho Kong.
- Rate limiting hiện tại ở mức cơ bản. Có thể nâng cấp với thuật toán token bucket hoặc sliding window cho từng endpoint cụ thể.
- Chưa có cơ chế phát hiện xâm nhập (IDS) hoặc Web Application Firewall (WAF). Có thể bổ sung khi triển khai production.

## 4.2. Kết luận chương 4

Chương 4 đã trình bày chi tiết giải pháp bảo mật cho hệ thống quản lý Voucher và Chương trình Khách hàng Thân thiết, bao gồm: xác thực bằng JWT theo chuẩn OAuth2/OIDC với Keycloak, phân quyền RBAC kết hợp data-level access control, bảo mật API Gateway với Kong (CORS, rate limiting), bảo mật giao tiếp inter-service qua Docker network isolation, đảm bảo toàn vẹn dữ liệu qua quy trình Maker/Checker và audit log, cùng các cơ chế bảo mật phía frontend (Zustand auth store, Axios interceptor, AuthGuard).

Hệ thống áp dụng nguyên tắc defense-in-depth với nhiều lớp bảo mật bổ trợ lẫn nhau, sử dụng các công nghệ chuẩn công nghiệp (Keycloak, Spring Security, Kong) thay vì tự xây dựng, giảm thiểu rủi ro lỗ hổng bảo mật và đảm bảo khả năng mở rộng trong tương lai.
