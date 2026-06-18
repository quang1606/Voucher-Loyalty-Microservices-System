# KẾT LUẬN VÀ HƯỚNG PHÁT TRIỂN

## 1. Kết luận

### 1.1. Tổng kết kết quả đạt được

Đồ án đã hoàn thành việc xây dựng hệ thống "Quản lý Voucher và Chương trình Khách hàng Thân thiết" theo kiến trúc Microservices với các kết quả cụ thể:

- Xây dựng thành công 5 microservice (Identity, Voucher, Customer, Loyalty, Notification) hoạt động độc lập, giao tiếp qua gRPC và Apache Kafka.
- Triển khai quy trình phê duyệt Maker/Checker hoàn chỉnh cho voucher và mission, đảm bảo kiểm soát chất lượng phát hành.
- Xây dựng hệ thống tích điểm, phân hạng khách hàng tự động (SILVER → GOLD → PLATINUM → DIAMOND) dựa trên giao dịch thực tế.
- Triển khai gamification qua nhiệm vụ (mission) với hai loại phần thưởng (điểm và voucher), bảng xếp hạng (leaderboard).
- Xây dựng giao diện quản trị web (Next.js) với kiến trúc BFF, phân quyền hiển thị theo vai trò.
- Triển khai toàn bộ hệ thống bằng Docker Compose với 15+ container hoạt động ổn định.
- Tích hợp hệ thống giám sát (Prometheus + Grafana) và logging tập trung (ELK Stack).

### 1.2. Đối chiếu với mục tiêu ban đầu

| STT | Mục tiêu đề ra | Kết quả | Đánh giá |
|-----|----------------|---------|----------|
| 1 | Phân tích nghiệp vụ voucher và loyalty | Khảo sát 6 hệ thống (Shopee, MoMo, TCH, Starbucks, Smile.io, Voucherify) | Đạt |
| 2 | Thiết kế kiến trúc Microservices | 5 service, Database per Service, API Gateway | Đạt |
| 3 | Xây dựng backend 5 service | Tất cả service hoạt động đúng chức năng | Đạt |
| 4 | Giao tiếp gRPC + Kafka | 4 gRPC connections, 3 Kafka topics | Đạt |
| 5 | Bảo mật OAuth2/OIDC + RBAC | Keycloak + Spring Security, 5 roles | Đạt |
| 6 | Giám sát và logging | Prometheus + Grafana + ELK Stack | Đạt |

### 1.3. Đánh giá ưu điểm

Về kiến trúc:
- Tách biệt rõ ràng giữa các service, mỗi service có thể phát triển, triển khai, và scale độc lập.
- Kết hợp giao tiếp đồng bộ (gRPC cho truy vấn cần response ngay) và bất đồng bộ (Kafka cho event processing), tối ưu hiệu năng và độ tin cậy.
- Database per Service đảm bảo loose coupling, cho phép thay đổi schema mà không ảnh hưởng service khác.

Về nghiệp vụ:
- Quy trình Maker/Checker là điểm khác biệt so với các hệ thống khảo sát, đảm bảo kiểm soát chất lượng voucher.
- Kết hợp đồng bộ 4 yếu tố (loyalty + voucher + mission + leaderboard) trong một nền tảng duy nhất.
- Hỗ trợ đối tác (Partner) tham gia tạo voucher/mission với data isolation.

Về công nghệ:
- Sử dụng các công nghệ chuẩn công nghiệp (Spring Boot, Keycloak, Kong, Kafka, gRPC), dễ bảo trì và tuyển dụng.
- Container hóa hoàn toàn bằng Docker Compose, reproducible trên mọi môi trường.
- Audit log đầy đủ cho mọi thao tác, hỗ trợ truy vết và compliance.

### 1.4. Đánh giá hạn chế

- Chưa triển khai unit test và integration test đầy đủ cho tất cả service, chỉ có test cơ bản.
- Notification Service chưa tích hợp kênh gửi thông báo thực tế (email, push notification), hiện chỉ là Kafka consumer ghi log.
- Chưa có giao diện dành riêng cho Customer (mobile app hoặc web app khách hàng), chỉ có API.
- Chưa triển khai trên môi trường cloud production thực tế (chỉ Docker Compose local).
- Chưa có cơ chế auto-scaling và load balancing cho từng service.
- Rate limiting hiện tại ở mức cơ bản, chưa có WAF hoặc IDS.

## 2. Hướng phát triển

### 2.1. Nâng cao chất lượng và kiểm thử

- Bổ sung unit test (JUnit 5 + Mockito) cho tất cả service layer, đạt coverage > 80%.
- Viết integration test với Testcontainers để test gRPC và Kafka flow end-to-end.
- Triển khai performance test với JMeter hoặc Gatling để đánh giá throughput và latency dưới tải cao.
- Thiết lập CI/CD pipeline (GitHub Actions hoặc GitLab CI) tự động build, test, và deploy.

### 2.2. Mở rộng tính năng nghiệp vụ

- Phát triển cơ chế voucher cá nhân hóa: gợi ý voucher dựa trên lịch sử mua hàng và hành vi khách hàng.
- Bổ sung A/B testing cho voucher campaign: so sánh hiệu quả giữa các chiến dịch khác nhau.
- Thêm chương trình referral (giới thiệu bạn bè): khách hàng nhận thưởng khi giới thiệu người mới.
- Hỗ trợ voucher có điều kiện phức tạp hơn: combo voucher, voucher theo danh mục sản phẩm, voucher theo thời gian (happy hour).

### 2.3. Phát triển giao diện khách hàng

- Xây dựng mobile app cho Customer sử dụng React Native hoặc Flutter, hỗ trợ iOS và Android.
- Tích hợp push notification (Firebase Cloud Messaging) để thông báo voucher mới, nâng hạng, hoàn thành mission.
- Phát triển web app khách hàng (Progressive Web App) cho trải nghiệm đa nền tảng.

### 2.4. Nâng cao phân tích và báo cáo

- Xây dựng dashboard analytics: thống kê voucher redemption rate, customer retention, mission completion rate.
- Tích hợp recommendation engine (collaborative filtering hoặc content-based) để cá nhân hóa voucher.
- Phân tích cohort khách hàng theo hạng, theo thời gian đăng ký, theo hành vi chi tiêu.

### 2.5. Triển khai và vận hành production

- Migrate từ Docker Compose sang Kubernetes (K8s) để hỗ trợ auto-scaling, self-healing, và rolling update.
- Cấu hình HTTPS/TLS cho Kong API Gateway với Let's Encrypt certificate.
- Triển khai trên cloud (AWS EKS, GCP GKE, hoặc Azure AKS) với multi-AZ cho high availability.
- Thiết lập backup tự động cho PostgreSQL và disaster recovery plan.

### 2.6. Cải thiện bảo mật

- Triển khai Web Application Firewall (WAF) trước Kong Gateway.
- Bổ sung rate limiting chi tiết theo endpoint và theo user (token bucket algorithm).
- Implement IP whitelisting cho admin endpoints.
- Thêm 2FA (Two-Factor Authentication) cho tài khoản Admin và Checker.
- Mã hóa dữ liệu nhạy cảm at-rest trong database (column-level encryption).

### 2.7. Mở rộng quy mô

- Tách PostgreSQL thành cluster (Primary-Replica) cho read scaling.
- Triển khai Kafka cluster multi-broker cho fault tolerance.
- Implement CQRS (Command Query Responsibility Segregation) cho các query phức tạp (leaderboard, analytics).
- Sử dụng Redis Cluster thay vì single instance cho cache layer.
- Hỗ trợ multi-tenant: một hệ thống phục vụ nhiều doanh nghiệp khác nhau.

---

# DANH MỤC TÀI LIỆU THAM KHẢO

[1] Shopee, "Shopee Rewards - Chương trình khách hàng thân thiết", https://shopee.vn/m/shopee-rewards, truy cập 2025.

[2] MoMo, "MoMo Business - Voucher Distribution API", https://business.momo.vn, truy cập 2025.

[3] The Coffee House, "Ứng dụng The Coffee House - Chương trình thành viên", https://www.thecoffeehouse.com, truy cập 2025.

[4] Starbucks, "Starbucks Rewards Program", https://www.starbucks.com/rewards, truy cập 2025.

[5] Smile.io, "Loyalty Program Platform for E-commerce", https://smile.io, truy cập 2025.

[6] Voucherify, "API-first Promotion Engine", https://www.voucherify.io, truy cập 2025.

[7] Oracle, "Java SE 17 Documentation", https://docs.oracle.com/en/java/javase/17/, truy cập 2025.

[8] VMware, "Spring Boot Reference Documentation 3.x", https://docs.spring.io/spring-boot/docs/current/reference/html/, truy cập 2025.

[9] PostgreSQL Global Development Group, "PostgreSQL 15 Documentation", https://www.postgresql.org/docs/15/, truy cập 2025.

[10] Redis Ltd, "Redis Documentation", https://redis.io/docs/, truy cập 2025.

[11] Apache Software Foundation, "Apache Kafka Documentation", https://kafka.apache.org/documentation/, truy cập 2025.

[12] Google, "gRPC Documentation", https://grpc.io/docs/, truy cập 2025.

[13] Kong Inc, "Kong Gateway OSS Documentation", https://docs.konghq.com/gateway/latest/, truy cập 2025.

[14] Red Hat, "Keycloak Server Administration Guide", https://www.keycloak.org/documentation, truy cập 2025.

[15] Prometheus Authors, "Prometheus Documentation", https://prometheus.io/docs/introduction/overview/, truy cập 2025.

[16] Elastic, "Elasticsearch Reference 8.x", https://www.elastic.co/guide/en/elasticsearch/reference/current/, truy cập 2025.

[17] Docker Inc, "Docker Compose Documentation", https://docs.docker.com/compose/, truy cập 2025.

[18] Vercel, "Next.js Documentation", https://nextjs.org/docs, truy cập 2025.

[19] Sam Newman, "Building Microservices: Designing Fine-Grained Systems", O'Reilly Media, 2nd Edition, 2021.

[20] Chris Richardson, "Microservices Patterns: With examples in Java", Manning Publications, 2018.
