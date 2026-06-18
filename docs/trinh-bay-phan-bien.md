# TRÌNH BÀY PHẢN BIỆN ĐỒ ÁN

## Đề tài: Xây dựng hệ thống quản lý Voucher và Chương trình Khách hàng Thân thiết theo kiến trúc Microservices

---

## THỨ TỰ TRÌNH BÀY (15-20 phút)

---

### 1. GIỚI THIỆU ĐỀ TÀI (2 phút)

"Kính chào thầy/cô, nhóm em xin trình bày đồ án: Xây dựng hệ thống quản lý Voucher và Chương trình Khách hàng Thân thiết theo kiến trúc Microservices."

**Bối cảnh:**
- Thương mại điện tử và bán lẻ tại Việt Nam phát triển mạnh, giữ chân khách hàng là yếu tố then chốt.
- Các hệ thống hiện có (Shopee Rewards, MoMo, The Coffee House) đều là hệ thống đóng, thiếu tính tổng hợp.

**Khoảng trống thị trường (điểm khác biệt đồ án):**
- Không hệ thống nào kết hợp đồng thời: quy trình phê duyệt Maker/Checker + loyalty đa hạng + gamification (mission) + leaderboard + cho phép đối tác tham gia.
- Đồ án giải quyết đồng bộ tất cả trong một nền tảng duy nhất.

---

### 2. KIẾN TRÚC HỆ THỐNG (3 phút)

**Mở sơ đồ kiến trúc (draw.io hoặc hình trong báo cáo)**

"Hệ thống được xây dựng theo kiến trúc Microservices gồm 5 service:"

| Service | Vai trò |
|---------|---------|
| Identity Service | Xác thực OAuth2/OIDC qua Keycloak, quản lý user |
| Voucher Service | Quản lý vòng đời voucher, mission, audit log |
| Customer Service | Phục vụ khách hàng (thu thập voucher, thanh toán) |
| Loyalty Service | Tích điểm, nâng hạng, quản lý mission entity |
| Notification Service | Xử lý thông báo bất đồng bộ |

**Giao tiếp:**
- Đồng bộ: gRPC (binary protocol, nhanh hơn REST 3-10 lần)
- Bất đồng bộ: Apache Kafka (đảm bảo không mất message khi tích điểm, nâng hạng)

**Hạ tầng:**
- Kong API Gateway: Điểm vào duy nhất, CORS, logging
- PostgreSQL: Database per Service (4 DB riêng biệt)
- Redis: Cache leaderboard, voucher list
- Keycloak: Xác thực OAuth2/OIDC chuẩn công nghiệp
- Docker Compose: 15+ container, một lệnh khởi động toàn bộ

---

### 3. CÁC CHỨC NĂNG CHÍNH (3 phút)

**5 vai trò người dùng:** Admin, Maker, Checker, Partner, Customer

**Luồng nghiệp vụ nổi bật:**

1. **Quy trình Maker/Checker:**
   - Maker tạo voucher (đơn lẻ hoặc Excel hàng loạt) → Submit → Checker phê duyệt/từ chối → Voucher kích hoạt cho khách hàng.
   - Đảm bảo kiểm soát chất lượng, ngăn phát hành voucher trái phép.

2. **Luồng khách hàng:**
   - Đăng ký → Thu thập voucher (theo hạng) → Thanh toán áp dụng voucher → Tự động tích điểm → Nâng hạng → Cập nhật tiến độ mission.

3. **Mission (Gamification):**
   - Nhiệm vụ chi tiêu hoặc mua hàng → Hoàn thành → Nhận thưởng (điểm hoặc voucher).

4. **Leaderboard:** Bảng xếp hạng realtime bằng Redis Sorted Set.

---

### 4. CÔNG NGHỆ SỬ DỤNG (2 phút)

| Lớp | Công nghệ | Lý do |
|-----|-----------|-------|
| Backend | Java 17 + Spring Boot 3.4 | Enterprise, type-safe, đa luồng |
| Database | PostgreSQL 15 | ACID, hiệu năng cao |
| Cache | Redis 7 | < 1ms response, Sorted Set cho leaderboard |
| Message | Apache Kafka | Durability, replay, consumer group |
| Inter-service | gRPC + Protobuf | Binary, contract-first, code gen |
| Gateway | Kong 3.6 | Routing, CORS, logging, rate limiting |
| Auth | Keycloak 23 | OAuth2/OIDC chuẩn, RBAC |
| Monitoring | Prometheus + Grafana | Metrics realtime |
| Logging | ELK Stack | Log tập trung, search across services |
| Deploy | Docker Compose | 15+ container, reproducible |
| Frontend | Next.js 16 + React 19 + TypeScript | BFF pattern, auto refresh token |

---

### 5. DEMO HỆ THỐNG (5-7 phút)

**Thứ tự demo:**

1. **Giao diện Admin/Maker** (Frontend):
   - Đăng nhập với role Maker
   - Tạo voucher đơn lẻ → Submit
   - Đổi sang Checker → Approve voucher

2. **Giao diện Customer** (Postman hoặc FE customer nếu có):
   - Login → Xem voucher khả dụng → Thu thập voucher
   - Chọn hóa đơn → Áp dụng voucher → Thanh toán
   - Xem điểm tăng, tiến độ mission cập nhật

3. **Monitoring (ấn tượng với thầy/cô):**
   - Mở Grafana (`localhost:3002`): Chỉ vào Rate, Duration, Memory → "Response time trung bình < 50ms, đạt yêu cầu < 500ms"
   - Mở Kibana (`localhost:5601`): Discover → log realtime khi gọi API → "Logging tập trung, trace được request across services"

4. **Docker Compose:**
   - Show terminal: `docker ps` → 15+ container đang chạy
   - "Toàn bộ hệ thống khởi động bằng một lệnh docker-compose up"

---

### 6. BẢO MẬT (2 phút)

- Xác thực: JWT token (Keycloak), access 5 phút + refresh 30 phút
- Phân quyền: RBAC (5 roles) + data-level access (Partner chỉ xem dữ liệu của mình)
- API Gateway: CORS, rate limiting, logging tập trung
- Toàn vẹn dữ liệu: Maker/Checker workflow + Audit log mọi thao tác
- Frontend: Auto refresh token, AuthGuard, BFF pattern ẩn backend URL

---

### 7. KẾT LUẬN (1 phút)

**Đạt được:**
- 22 chức năng hoạt động đầy đủ
- 5 microservices giao tiếp qua gRPC + Kafka
- Quy trình phê duyệt Maker/Checker hoàn chỉnh
- Monitoring + Logging tập trung
- Container hóa toàn bộ (Docker Compose)
- Giao diện quản trị frontend (Next.js)

**Hạn chế:**
- Chưa deploy lên cloud production
- Chưa có mobile app cho Customer
- Notification Service chưa tích hợp kênh thực (email, push)

**Hướng phát triển:**
- Triển khai Kubernetes cho auto-scaling
- Mobile app (React Native)
- A/B testing cho voucher campaign

---

## CÂU HỎI THƯỜNG GẶP KHI PHẢN BIỆN

### Q: Tại sao chọn Microservices thay vì Monolith?
"Hệ thống có 5 module nghiệp vụ độc lập (auth, voucher, customer, loyalty, notification). Microservices cho phép: scale từng module riêng, deploy độc lập, dùng database riêng tránh coupling. Ví dụ khi flash sale voucher, chỉ cần scale Customer Service mà không ảnh hưởng Identity Service."

### Q: Tại sao dùng gRPC thay vì REST cho inter-service?
"gRPC dùng Protobuf (binary) nhỏ hơn JSON 3-10 lần, HTTP/2 multiplexing giảm latency. Contract-first qua file .proto đảm bảo interface giữa services luôn đồng bộ. REST vẫn dùng cho client-facing API vì dễ debug và tích hợp."

### Q: Kafka dùng cho gì trong hệ thống?
"3 event chính: (1) Tích điểm sau thanh toán (loyalty-point-topic), (2) Nâng hạng thông báo (tier-upgrade-topic), (3) Cập nhật stock voucher (voucher-used-topic). Kafka đảm bảo không mất message — nếu Loyalty Service tạm down, khi restart sẽ xử lý lại từ offset chưa consume."

### Q: Quy trình Maker/Checker hoạt động thế nào?
"Maker tạo voucher (status=INIT) → Submit (PENDING_APPROVE) → Checker xem xét → Approve: batch activate 100 records/batch → Voucher ACTIVE. Reject: kèm lý do. Maker không thể tự approve, đảm bảo 4 mắt kiểm soát."

### Q: Redis dùng ở đâu?
"Cache leaderboard bằng Sorted Set (O(log N) mỗi thao tác, thay vì ORDER BY trên bảng lớn). Cache danh sách voucher theo tier. Rate limiting cho collect voucher."

### Q: Bảo mật hệ thống thế nào?
"Multi-layer: Keycloak (OAuth2 chuẩn) → Kong (CORS, rate limit) → Spring Security (@PreAuthorize RBAC) → Service layer (data-level filtering). Token JWT verify bằng public key, stateless, horizontal scale được."

### Q: Database per Service có vấn đề gì không?
"Khi cần dữ liệu từ service khác phải gọi gRPC (không query trực tiếp DB khác). Ưu điểm: loose coupling, thay đổi schema không ảnh hưởng service khác. Nhược điểm: eventual consistency với Kafka event."

### Q: Frontend hoạt động thế nào?
"Next.js với kiến trúc BFF — API routes proxy đến backend, ẩn URL thực. Zustand quản lý auth state. Axios interceptor tự động refresh token khi hết hạn. Phân quyền giao diện qua allowed-pages API."

---

## CHECKLIST TRƯỚC KHI BÁO CÁO

- [ ] Docker Compose đang chạy đủ container (postgres, redis, kafka, keycloak, kong, elk, prometheus, grafana)
- [ ] Các service chạy trên IntelliJ (identity, voucher, customer, loyalty)
- [ ] Frontend đang chạy (localhost:3000)
- [ ] Grafana có dữ liệu (localhost:3002)
- [ ] Kibana có log (localhost:5601)
- [ ] Postman collection sẵn sàng (login, tạo voucher, thanh toán)
- [ ] Tài khoản test: admin, maker01, checker01, partner01, customer01
