# CHƯƠNG 3: XÂY DỰNG VÀ PHÁT TRIỂN ỨNG DỤNG

## 3.1. Tổng quan sản phẩm

### 3.1.1. Giới thiệu hệ thống

Hệ thống "Quản lý Voucher và Chương trình Khách hàng Thân thiết" là một nền tảng backend hoàn chỉnh được xây dựng theo kiến trúc Microservices, nhằm giải quyết bài toán quản lý voucher giảm giá, chương trình tích điểm, phân hạng khách hàng, và gamification (nhiệm vụ) cho các doanh nghiệp thương mại điện tử và bán lẻ.

Hệ thống được thiết kế với mục tiêu:
- Tách biệt các nghiệp vụ thành các service độc lập, dễ bảo trì và mở rộng.
- Hỗ trợ quy trình phê duyệt nhiều bước (Maker/Checker) đảm bảo kiểm soát chất lượng.
- Xử lý bất đồng bộ cho các tác vụ không yêu cầu phản hồi tức thì (tích điểm, thông báo).
- Đảm bảo hiệu năng cao khi số lượng voucher và giao dịch tăng lên.

### 3.1.2. Kiến trúc tổng quan hệ thống

Hệ thống bao gồm 5 service ứng dụng chính, được hỗ trợ bởi các thành phần hạ tầng (infrastructure):

**Các service ứng dụng:**
- Identity Service: Xác thực và quản lý người dùng.
- Voucher Service: Quản lý vòng đời voucher và nhiệm vụ.
- Customer Service: Phục vụ khách hàng cuối (thu thập voucher, thanh toán).
- Loyalty Service: Quản lý điểm thưởng và nhiệm vụ nội bộ.
- Notification Service: Xử lý thông báo sự kiện.

**Các thành phần hạ tầng:**
- Kong API Gateway: Điểm vào duy nhất cho client.
- PostgreSQL: Cơ sở dữ liệu quan hệ.
- Redis: Cache và session.
- Apache Kafka: Message broker cho giao tiếp bất đồng bộ.
- Keycloak: Máy chủ xác thực OAuth2/OIDC.
- ELK Stack: Logging tập trung.
- Prometheus + Grafana: Giám sát hiệu năng.


### 3.1.3. Sơ đồ kiến trúc triển khai

```
┌─────────────────────────────────────────────────────────────────────┐
│                         Docker Compose                               │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌────────────┐     ┌────────────┐     ┌─────────────────────┐      │
│  │   Kong      │     │  Keycloak  │     │ Kafka + Zookeeper   │      │
│  │  Gateway    │     │  (OAuth2)  │     │ (Event Streaming)   │      │
│  │  :8000      │     │  :8180     │     │ :9092               │      │
│  └──────┬─────┘     └────────────┘     └─────────────────────┘      │
│         │                                                            │
│  ┌──────┼──────────────────────────────────────────────────┐        │
│  │      │            Application Layer                      │        │
│  │      ├──► Identity Service     (:8081) ─── REST + gRPC  │        │
│  │      ├──► Voucher Service      (:8082) ─── REST + gRPC  │        │
│  │      ├──► Customer Service     (:8084) ─── REST + gRPC  │        │
│  │      ├──► Loyalty Service      (internal) ── gRPC only  │        │
│  │      └──► Notification Service (internal) ── Kafka only │        │
│  └─────────────────────────────────────────────────────────┘        │
│                                                                      │
│  ┌────────────┐     ┌────────────┐     ┌─────────────────────┐      │
│  │ PostgreSQL │     │   Redis    │     │     ELK Stack       │      │
│  │   :5432    │     │   :6379    │     │ ES:9200 Kibana:5601 │      │
│  └────────────┘     └────────────┘     └─────────────────────┘      │
│                                                                      │
│  ┌─────────────────────────────────────┐                            │
│  │  Prometheus (:9090) + Grafana (:3002)│                            │
│  └─────────────────────────────────────┘                            │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 3.2. Lựa chọn công nghệ và lý do

### 3.2.1. Ngôn ngữ lập trình: Java 17

**Lý do lựa chọn:**

Java là ngôn ngữ lập trình hướng đối tượng được sử dụng rộng rãi nhất trong phát triển ứng dụng doanh nghiệp (enterprise). Phiên bản Java 17 là bản LTS (Long-Term Support) mới nhất tại thời điểm phát triển, đảm bảo được hỗ trợ dài hạn từ Oracle.

- **Hệ sinh thái phong phú:** Java có hàng nghìn thư viện và framework hỗ trợ mọi khía cạnh của phát triển phần mềm, từ xử lý dữ liệu, bảo mật, đến giao tiếp mạng.
- **Hiệu năng cao:** JVM (Java Virtual Machine) với JIT (Just-In-Time) compilation cho hiệu năng gần bằng ngôn ngữ native, phù hợp cho hệ thống xử lý nhiều giao dịch đồng thời.
- **Type-safe:** Hệ thống kiểu tĩnh giúp phát hiện lỗi sớm tại compile-time, giảm bug runtime trong hệ thống phức tạp nhiều service.
- **Đa luồng (Multi-threading):** Java hỗ trợ xử lý đa luồng mạnh mẽ, phù hợp cho việc xử lý batch voucher (100 records/batch) và xử lý nhiều request đồng thời.
- **Cộng đồng lớn:** Dễ dàng tìm kiếm tài liệu, giải pháp cho các vấn đề kỹ thuật.

**So sánh với các lựa chọn khác:**
- So với Node.js: Java mạnh hơn về type safety và xử lý đa luồng, phù hợp hơn cho hệ thống enterprise phức tạp.
- So với Go: Java có hệ sinh thái framework (Spring) phong phú hơn cho microservices, ORM mạnh hơn.
- So với Python: Java có hiệu năng runtime cao hơn đáng kể, phù hợp hơn cho hệ thống xử lý giao dịch.


### 3.2.2. Framework: Spring Boot 3.4.4

**Lý do lựa chọn:**

Spring Boot là framework phổ biến nhất cho phát triển ứng dụng Java, đặc biệt trong kiến trúc microservices. Spring Boot 3.x yêu cầu Java 17+ và hỗ trợ Jakarta EE 10, đại diện cho thế hệ mới nhất của Spring ecosystem.

- **Auto-configuration:** Spring Boot tự động cấu hình các thành phần dựa trên dependency có trong classpath, giảm đáng kể boilerplate code. Ví dụ: thêm `spring-boot-starter-data-jpa` sẽ tự động cấu hình DataSource, EntityManager, Transaction Manager.
- **Starter dependencies:** Hệ thống starter giúp quản lý dependency dễ dàng. Trong dự án sử dụng: `spring-boot-starter-web` (REST API), `spring-boot-starter-data-jpa` (ORM), `spring-boot-starter-security` (bảo mật), `spring-boot-starter-data-redis` (cache).
- **Embedded server:** Mỗi service chạy trên embedded Tomcat, không cần cài đặt application server riêng, phù hợp cho container hóa.
- **Spring Security:** Tích hợp sẵn framework bảo mật mạnh mẽ, hỗ trợ OAuth2 Resource Server để verify JWT token từ Keycloak.
- **Spring Data JPA:** Cung cấp repository pattern, Specification API cho dynamic query, giảm code truy vấn database.
- **Actuator:** Cung cấp health check endpoints, metrics cho Prometheus, hỗ trợ giám sát production.
- **Profile-based configuration:** Dễ dàng quản lý cấu hình cho các môi trường khác nhau (dev, staging, production).

**So sánh với các framework khác:**
- So với Quarkus: Spring Boot có cộng đồng lớn hơn, nhiều tài liệu hơn, hệ sinh thái plugin phong phú hơn.
- So với Micronaut: Spring Boot có learning curve thấp hơn cho người đã quen Spring ecosystem.
- So với Jakarta EE (Payara/WildFly): Spring Boot nhẹ hơn, khởi động nhanh hơn, phù hợp hơn cho microservices.

### 3.2.3. Cơ sở dữ liệu: PostgreSQL 15

**Lý do lựa chọn:**

PostgreSQL là hệ quản trị cơ sở dữ liệu quan hệ mã nguồn mở mạnh mẽ nhất hiện nay, được sử dụng rộng rãi trong các hệ thống enterprise.

- **ACID Compliance:** PostgreSQL đảm bảo tính toàn vẹn dữ liệu với đầy đủ thuộc tính ACID (Atomicity, Consistency, Isolation, Durability). Điều này đặc biệt quan trọng trong hệ thống voucher nơi cần đảm bảo: khi khách hàng thu thập voucher, stock phải được giảm chính xác; khi thanh toán, số dư và điểm phải được cập nhật đồng bộ.
- **Hiệu năng cao:** PostgreSQL xử lý tốt cả OLTP (Online Transaction Processing) với nhiều giao dịch nhỏ đồng thời, và OLAP (Online Analytical Processing) cho các truy vấn phân tích phức tạp (thống kê voucher, leaderboard).
- **JSON support:** Hỗ trợ kiểu dữ liệu JSONB cho các trường linh hoạt (metadata, cấu hình).
- **Full-text search:** Hỗ trợ tìm kiếm toàn văn, hữu ích cho tìm kiếm voucher theo tên/mô tả.
- **Partitioning:** Hỗ trợ phân vùng bảng, hữu ích khi bảng vouchers hoặc transactions tăng lên hàng triệu records.
- **Mã nguồn mở:** Không tốn chi phí license, phù hợp cho đồ án và startup.
- **Docker-friendly:** Image `postgres:15-alpine` nhẹ (~80MB), khởi động nhanh.

**So sánh với các lựa chọn khác:**
- So với MySQL: PostgreSQL có hỗ trợ ACID tốt hơn, JSON support mạnh hơn, và tuân thủ SQL standard chặt chẽ hơn.
- So với MongoDB: Dữ liệu voucher có cấu trúc rõ ràng (schema cố định), quan hệ giữa các bảng phức tạp (request → detail, customer → voucher), nên RDBMS phù hợp hơn NoSQL.
- So với Oracle: PostgreSQL miễn phí, hiệu năng tương đương cho quy mô dự án này.


### 3.2.4. Cache: Redis 7

**Lý do lựa chọn:**

Redis là hệ thống lưu trữ dữ liệu in-memory nhanh nhất hiện nay, được sử dụng làm cache layer giữa application và database.

- **Tốc độ cực nhanh:** Redis lưu trữ dữ liệu trên RAM, thời gian truy vấn < 1ms, nhanh hơn hàng trăm lần so với truy vấn PostgreSQL. Trong hệ thống này, Redis cache:
  - Danh sách voucher khả dụng (tránh query phức tạp mỗi lần customer mở app).
  - Leaderboard (sorted set, O(log N) cho mỗi thao tác).
  - Thông tin session/token.
- **Cấu trúc dữ liệu phong phú:** Redis hỗ trợ String, Hash, List, Set, Sorted Set, Stream. Sorted Set đặc biệt phù hợp cho leaderboard (xếp hạng theo điểm).
- **TTL (Time-To-Live):** Tự động xóa cache hết hạn, phù hợp cho cache voucher có thời hạn.
- **Pub/Sub:** Hỗ trợ publish/subscribe cho real-time notification (bổ sung cho Kafka).
- **Eviction policy:** Cấu hình `allkeys-lru` (Least Recently Used) đảm bảo khi memory đầy, dữ liệu ít truy cập nhất sẽ bị xóa tự động.
- **Nhẹ và ổn định:** Image `redis:7-alpine` chỉ ~30MB, giới hạn 64MB memory trong dự án.

**Ứng dụng cụ thể trong hệ thống:**
- Cache leaderboard: Sử dụng Sorted Set với score = totalPoints, tránh query ORDER BY trên bảng lớn.
- Cache voucher available: Lưu danh sách voucher theo tier, invalidate khi có voucher mới được approve.
- Rate limiting: Giới hạn số lần collect voucher trong khoảng thời gian ngắn.

### 3.2.5. Message Broker: Apache Kafka

**Lý do lựa chọn:**

Apache Kafka là nền tảng streaming phân tán được thiết kế cho throughput cao và độ tin cậy tuyệt đối, phù hợp cho giao tiếp bất đồng bộ giữa các microservices.

- **Đảm bảo không mất message (Durability):** Kafka lưu message trên disk với replication, đảm bảo không mất dữ liệu ngay cả khi broker crash. Trong hệ thống voucher, điều này cực kỳ quan trọng: khi customer thanh toán, event tích điểm KHÔNG ĐƯỢC mất.
- **Throughput cao:** Kafka xử lý hàng triệu message/giây, phù hợp khi hệ thống scale lên nhiều giao dịch đồng thời.
- **Consumer Group:** Nhiều instance của cùng một service có thể chia nhau xử lý message, hỗ trợ horizontal scaling.
- **Replay capability:** Consumer có thể đọc lại message từ offset cũ, hữu ích khi cần reprocess (ví dụ: Loyalty Service bị lỗi, sau khi fix có thể replay lại các event chưa xử lý).
- **Decoupling:** Service producer không cần biết service consumer là ai, giảm coupling giữa các service.

**Các Kafka topic trong hệ thống:**

| Topic | Producer | Consumer | Mục đích |
|---|---|---|---|
| loyalty-point-topic | Customer Service | Loyalty Service | Tích điểm sau thanh toán |
| tier-upgrade-topic | Loyalty Service | Customer Service | Thông báo nâng hạng |
| voucher-used-topic | Customer Service | Voucher Service | Cập nhật stock voucher |

**So sánh với các lựa chọn khác:**
- So với RabbitMQ: Kafka có throughput cao hơn, hỗ trợ replay message, phù hợp hơn cho event-driven architecture. RabbitMQ phù hợp hơn cho task queue đơn giản.
- So với ActiveMQ: Kafka hiện đại hơn, cộng đồng lớn hơn, hiệu năng tốt hơn.
- So với Redis Pub/Sub: Kafka đảm bảo durability (message không mất), Redis Pub/Sub là fire-and-forget.

### 3.2.6. Giao tiếp inter-service: gRPC + Protocol Buffers

**Lý do lựa chọn:**

gRPC là framework RPC (Remote Procedure Call) hiệu năng cao do Google phát triển, sử dụng Protocol Buffers làm ngôn ngữ định nghĩa interface và format serialization.

- **Hiệu năng vượt trội so với REST:**
  - Protocol Buffers serialize dữ liệu dạng binary, nhỏ hơn 3-10 lần so với JSON.
  - Sử dụng HTTP/2 với multiplexing, header compression, giảm latency đáng kể.
  - Trong hệ thống: Voucher Service gọi Identity Service lấy storeName hàng nghìn lần/ngày, gRPC giúp giảm latency từ ~5ms (REST) xuống ~1ms.
- **Contract-first (Schema-first):** File `.proto` định nghĩa rõ ràng interface giữa các service, đóng vai trò như "hợp đồng" giữa team phát triển. Khi thay đổi API, compiler sẽ báo lỗi nếu client chưa cập nhật.
- **Code generation:** Từ file `.proto`, tự động generate code Java cho cả client và server, giảm lỗi do viết tay.
- **Streaming:** gRPC hỗ trợ bidirectional streaming, hữu ích cho các tác vụ như stream danh sách mission.
- **Phù hợp cho internal communication:** gRPC không cần human-readable (khác REST/JSON), tối ưu cho giao tiếp máy-máy giữa các service nội bộ.

**Ứng dụng trong hệ thống:**
```
Voucher Service ──gRPC──► Identity Service  (getNameStore, getPartner)
Voucher Service ──gRPC──► Loyalty Service   (createMission, updateStatus)
Customer Service ──gRPC──► Voucher Service  (searchVouchersByTier)
Customer Service ──gRPC──► Loyalty Service  (getMissions, addPoints)
```

**So sánh với REST cho internal communication:**
- REST + JSON: Human-readable nhưng chậm hơn (text serialization), không có contract enforcement.
- gRPC + Protobuf: Nhanh hơn, type-safe, nhưng khó debug bằng mắt (binary format).
- Kết luận: Dùng REST cho client-facing API (dễ tích hợp frontend), gRPC cho internal (hiệu năng).


### 3.2.7. API Gateway: Kong 3.6

**Lý do lựa chọn:**

Kong là API Gateway mã nguồn mở phổ biến nhất, đóng vai trò là điểm vào duy nhất (single entry point) cho tất cả request từ client.

- **Routing tập trung:** Thay vì client phải biết địa chỉ từng service, client chỉ cần gọi đến Kong (:8000), Kong tự động route đến service đích dựa trên path:
  - `/api/identity/*` → Identity Service (:8081)
  - `/api/vouchers/*` → Voucher Service (:8082)
  - `/api/customers/*` → Customer Service (:8084)
- **Plugin ecosystem:** Kong cung cấp hàng trăm plugin cho các cross-cutting concerns:
  - `cors`: Xử lý Cross-Origin Resource Sharing cho frontend.
  - `tcp-log`: Gửi access log đến Logstash để phân tích.
  - `prometheus`: Export metrics cho monitoring.
- **Declarative configuration:** Cấu hình Kong bằng file YAML (`kong.yml`), dễ version control và tái tạo.
- **Database-less mode:** Chạy không cần database riêng (mode `off`), giảm complexity.
- **Hiệu năng cao:** Kong được xây dựng trên Nginx + LuaJIT, xử lý hàng chục nghìn request/giây.

**So sánh với các lựa chọn khác:**
- So với Spring Cloud Gateway: Kong có plugin ecosystem phong phú hơn, không phụ thuộc Java, hiệu năng cao hơn.
- So với Nginx (thuần): Kong cung cấp sẵn các tính năng API management (rate limiting, auth, logging) mà Nginx cần cấu hình thủ công.
- So với AWS API Gateway: Kong là self-hosted, không phụ thuộc cloud provider, phù hợp cho đồ án.

### 3.2.8. Xác thực: Keycloak 23.0

**Lý do lựa chọn:**

Keycloak là máy chủ quản lý danh tính và truy cập (Identity and Access Management - IAM) mã nguồn mở do Red Hat phát triển, hỗ trợ đầy đủ các chuẩn OAuth2, OpenID Connect, SAML.

- **Chuẩn OAuth2/OIDC:** Keycloak implement đầy đủ OAuth2 Authorization Framework và OpenID Connect, đảm bảo bảo mật theo chuẩn công nghiệp. Hệ thống sử dụng flow:
  - Resource Owner Password Credentials (cho login).
  - Refresh Token (cho duy trì session).
- **Quản lý user tập trung:** Keycloak quản lý toàn bộ user, role, permission tại một nơi. Identity Service tương tác với Keycloak qua Admin REST API để CRUD user.
- **JWT Token:** Keycloak phát hành JWT access token chứa thông tin user (userId, username, roles). Các service khác chỉ cần verify signature bằng public key, không cần gọi lại Keycloak.
- **Role-Based Access Control (RBAC):** Hỗ trợ phân quyền theo role (ADMIN, MAKER, CHECKER, PARTNER, CUSTOMER) được nhúng trong JWT token.
- **Single Sign-On (SSO):** Nếu mở rộng thêm frontend hoặc mobile app, Keycloak hỗ trợ SSO sẵn.
- **Realm isolation:** Mỗi realm là một không gian cô lập, có thể tạo realm riêng cho từng môi trường (dev, staging, prod).

**So sánh với các lựa chọn khác:**
- So với tự implement JWT: Keycloak cung cấp sẵn token management, refresh flow, revocation, giảm rủi ro bảo mật khi tự viết.
- So với Auth0: Keycloak là self-hosted và miễn phí, Auth0 là SaaS có phí.
- So với Firebase Auth: Keycloak linh hoạt hơn, hỗ trợ custom role/permission phức tạp hơn.

### 3.2.9. Monitoring: Prometheus + Grafana

**Lý do lựa chọn:**

Prometheus và Grafana là bộ đôi monitoring tiêu chuẩn trong hệ sinh thái cloud-native và microservices.

**Prometheus:**
- **Pull-based model:** Prometheus chủ động pull metrics từ các service endpoint (`/actuator/prometheus`), không cần service push metrics.
- **Time-series database:** Lưu trữ metrics theo thời gian, hỗ trợ query mạnh mẽ với PromQL.
- **Service discovery:** Tự động phát hiện service mới trong Docker network.
- **Alerting:** Hỗ trợ cấu hình alert khi metrics vượt ngưỡng (CPU > 80%, response time > 2s).

**Grafana:**
- **Dashboard trực quan:** Tạo dashboard đẹp với nhiều loại biểu đồ (line, bar, gauge, heatmap).
- **Multi-datasource:** Kết nối được cả Prometheus (metrics) và Elasticsearch (logs).
- **Template variables:** Dashboard có thể filter theo service, time range.

**Metrics được thu thập:**
- JVM metrics: heap memory, GC time, thread count.
- HTTP metrics: request count, response time, error rate per endpoint.
- Kong metrics: upstream latency, request per second, status code distribution.
- Custom metrics: voucher created/approved count, payment success rate.

### 3.2.10. Logging: ELK Stack (Elasticsearch + Logstash + Kibana)

**Lý do lựa chọn:**

Trong kiến trúc microservices với 5+ service, việc đọc log từng container riêng lẻ là không khả thi. ELK Stack cung cấp giải pháp logging tập trung.

- **Elasticsearch:** Lưu trữ và index log, hỗ trợ full-text search. Khi cần debug lỗi, có thể search theo requestId, userId, error message across tất cả service.
- **Logstash:** Thu thập log từ Kong (tcp-log plugin) và các service, transform và forward đến Elasticsearch.
- **Kibana:** Giao diện web để tìm kiếm, filter, và visualize log. Hỗ trợ tạo dashboard theo error rate, slow queries.

**Luồng log:**
```
Kong (tcp-log) ──► Logstash (:5044) ──► Elasticsearch (:9200) ──► Kibana (:5601)
```

### 3.2.11. Container hóa: Docker + Docker Compose

**Lý do lựa chọn:**

Docker là công nghệ container hóa tiêu chuẩn, Docker Compose là công cụ orchestration cho môi trường development và testing.

- **Môi trường nhất quán:** Mỗi service được đóng gói trong Docker image với đầy đủ dependency, đảm bảo chạy giống nhau trên mọi máy (dev, CI/CD, production).
- **Isolation:** Mỗi service chạy trong container riêng, không xung đột dependency.
- **Reproducible:** Chỉ cần `docker-compose up` là toàn bộ hệ thống (15+ container) khởi động đầy đủ.
- **Resource management:** Giới hạn memory cho từng container (128M-800M), tránh một service chiếm hết tài nguyên.
- **Health check:** Docker Compose hỗ trợ health check, đảm bảo PostgreSQL/Redis/Kafka sẵn sàng trước khi application service khởi động.
- **Volume persistence:** Mount volume cho database và monitoring data, giữ dữ liệu khi restart container.

**Cấu hình resource trong dự án:**

| Container | Memory Limit | Lý do |
|---|---|---|
| PostgreSQL | 256M | Database chính, cần đủ memory cho buffer pool |
| Redis | 128M | In-memory cache, giới hạn 64MB data |
| Kafka | 768M | Message broker, cần memory cho page cache |
| Keycloak | 384M | Auth server, JVM-based |
| Elasticsearch | 512M | Full-text search engine, memory-intensive |
| Kibana | 800M | Web UI, Node.js-based |
| Application services | 256M mỗi service | Spring Boot JVM (Xms128m-Xmx192m) |
| Kong | 128M | Nginx-based, rất nhẹ |


---

## 3.3. Backend – Xử lý logic và API

### 3.3.1. Cấu trúc project (Multi-module Maven)

Dự án sử dụng Maven multi-module để quản lý 7 module trong một repository duy nhất (monorepo). Parent POM quản lý version chung cho tất cả dependency, đảm bảo tính nhất quán.

```
backend-parent/                     (Parent POM)
├── pom.xml                         (Quản lý version, dependency chung)
├── common/                         (Module chia sẻ: BaseResponse, BaseException, BaseErrorCode)
├── proto/                          (Module gRPC: .proto files → generated Java code)
├── identity-service/               (Service xác thực - Port 8081)
├── voucher-service/                (Service voucher + mission - Port 8082)
├── loyalty-service/                (Service loyalty - gRPC internal)
├── customer-service/               (Service khách hàng - Port 8084)
├── notification-service/           (Service thông báo - Kafka consumer)
├── api-gateway/                    (Kong YAML configuration)
├── docker-compose.yml              (Orchestration toàn bộ hệ thống)
├── scripts/                        (SQL init, seed data)
└── infra/                          (Cấu hình Prometheus, Logstash)
```

**Ưu điểm của multi-module:**
- Module `common` chứa code dùng chung (BaseResponse, BaseException), tránh duplicate.
- Module `proto` chứa file `.proto` và generated code, tất cả service reference cùng một version.
- Mỗi service có `pom.xml` riêng, chỉ khai báo dependency cần thiết.
- Build toàn bộ project bằng một lệnh: `mvn clean package -DskipTests`.

### 3.3.2. Kiến trúc bên trong mỗi service (Layered Architecture)

Mỗi service tuân theo kiến trúc phân lớp rõ ràng, tách biệt trách nhiệm:

```
┌─────────────────────────────────────────────┐
│              Controller Layer                 │
│  (REST endpoints, request validation)        │
├─────────────────────────────────────────────┤
│              Service Layer                    │
│  (Business logic, orchestration)             │
│  ├── impl/      (Implementations)           │
│  ├── helper/    (Utility methods)            │
│  └── strategy/  (Strategy pattern)           │
├─────────────────────────────────────────────┤
│              Repository Layer                 │
│  (Data access, JPA repositories)             │
│  └── specification/ (Dynamic queries)        │
├─────────────────────────────────────────────┤
│              Entity Layer                     │
│  (JPA entities, database mapping)            │
├─────────────────────────────────────────────┤
│              DTO Layer                        │
│  ├── request/   (Input DTOs)                │
│  ├── response/  (Output DTOs)               │
│  └── event/     (Kafka event DTOs)          │
├─────────────────────────────────────────────┤
│              Cross-cutting                    │
│  ├── configuration/ (Security, Redis, Kafka)│
│  ├── constant/      (Enums)                 │
│  ├── exception/     (Error handling)        │
│  ├── grpc/          (gRPC client/server)    │
│  └── mapper/        (Entity ↔ DTO)         │
└─────────────────────────────────────────────┘
```

### 3.3.3. Design Patterns sử dụng

**a) Strategy Pattern (Voucher Service)**

Hệ thống hỗ trợ hai loại giảm giá (FIXED và PERCENT) với logic xử lý khác nhau. Thay vì dùng if-else, áp dụng Strategy Pattern:

- `VoucherRequestStrategy` (interface): Định nghĩa các method chung.
- `FixedVoucherStrategy`: Xử lý voucher giảm giá cố định.
- `PercentVoucherStrategy`: Xử lý voucher giảm giá phần trăm.
- `VoucherRequestStrategyFactory`: Chọn strategy phù hợp dựa trên discountType.

Ưu điểm: Dễ mở rộng thêm loại giảm giá mới (ví dụ: CASHBACK) mà không sửa code cũ (Open/Closed Principle).

**b) Specification Pattern (Dynamic Query)**

Các API danh sách voucher/mission có nhiều filter tùy chọn (status, date range, creatorType, storeName...). Sử dụng JPA Specification để build query động:

```java
Specification<VoucherRequestEntity> spec = VoucherRequestSpecification.withFilters(
    listStatus, fromDate, toDate, createdBy, requestMode, creatorType, voucherPurpose, storeName);
Page<VoucherRequestEntity> result = repository.findAll(spec, pageable);
```

Ưu điểm: Tránh viết nhiều method query trong repository, dễ thêm filter mới.

**c) Builder Pattern (Response DTO)**

Sử dụng Lombok `@Builder` cho tất cả response DTO, giúp code tạo response rõ ràng và dễ đọc.

**d) Template Method Pattern (Batch Processing)**

Khi approve voucher, hệ thống xử lý VoucherDetail theo batch 100 records. Logic batch processing được tái sử dụng cho cả approve và reject.

### 3.3.4. Chi tiết Identity Service

**Vai trò:** Quản lý xác thực, phân quyền, và thông tin người dùng.

**API Endpoints:**

| Method | Endpoint | Chức năng | Quyền truy cập |
|---|---|---|---|
| POST | /api/v1/auth/login | Đăng nhập, nhận JWT token | Public |
| POST | /api/v1/auth/register | Đăng ký tài khoản customer | Public |
| POST | /api/v1/auth/refresh | Làm mới access token | Public |
| POST | /api/v1/auth/allowed-pages | Lấy danh sách trang được phép | Authenticated |
| GET | /api/v1/profile | Xem profile cá nhân | Authenticated |
| PUT | /api/v1/profile | Cập nhật profile | Authenticated |
| PUT | /api/v1/profile/password | Đổi mật khẩu | Authenticated |
| GET | /api/v1/system-users | Danh sách tất cả users | ADMIN |
| POST | /api/v1/system-users | Tạo user mới | ADMIN |
| PUT | /api/v1/system-users/{id} | Cập nhật user | ADMIN |
| DELETE | /api/v1/system-users/{id} | Xóa user | ADMIN |
| GET | /api/v1/roles | Danh sách roles | ADMIN |
| POST | /api/v1/roles | Tạo role | ADMIN |

**Đặc điểm triển khai:**
- Tích hợp Keycloak Admin REST API: Khi tạo user, đồng thời tạo trên Keycloak realm và lưu vào database nội bộ.
- gRPC Server: Cung cấp service cho Voucher Service và Customer Service truy vấn thông tin partner (storeName, partnerId).
- Đồng bộ dữ liệu: User được lưu cả trên Keycloak (xác thực) và database nội bộ (thông tin bổ sung như storeName, category, balance).

### 3.3.5. Chi tiết Voucher Service

**Vai trò:** Quản lý toàn bộ vòng đời voucher và nhiệm vụ (mission).

**API Endpoints:**

| Method | Endpoint | Chức năng | Quyền |
|---|---|---|---|
| POST | /api/v1/vouchers | Tạo voucher đơn lẻ | MAKER, PARTNER |
| POST | /api/v1/vouchers/excel | Tạo voucher từ Excel | MAKER, PARTNER |
| GET | /api/v1/vouchers | Danh sách voucher requests | MAKER, CHECKER, PARTNER |
| GET | /api/v1/vouchers/details | Danh sách voucher chi tiết | MAKER, CHECKER, PARTNER |
| GET | /api/v1/vouchers/{id} | Chi tiết một request | MAKER, CHECKER, PARTNER |
| PUT | /api/v1/vouchers/{id}/submit | Gửi phê duyệt | MAKER, PARTNER |
| PUT | /api/v1/vouchers/{id}/confirm | Phê duyệt/từ chối | CHECKER |
| PUT | /api/v1/vouchers/{id}/cancel | Hủy yêu cầu | MAKER, PARTNER |
| POST | /api/v1/missions/missions | Tạo mission | MAKER, PARTNER |
| GET | /api/v1/missions/missions/{id} | Chi tiết mission | MAKER, CHECKER, PARTNER |
| GET | /api/v1/missions/search | Tìm kiếm missions | MAKER, CHECKER, PARTNER |
| PUT | /api/v1/missions/{id}/submit | Submit mission | MAKER, PARTNER |
| PUT | /api/v1/missions/{id}/confirm | Confirm mission | CHECKER |
| PUT | /api/v1/missions/{id}/cancel | Cancel mission | MAKER, PARTNER |
| GET | /api/v1/audit-logs | Xem audit log | ADMIN, CHECKER |

**Đặc điểm triển khai:**
- **Batch processing:** Khi Checker approve, hệ thống xử lý VoucherDetail theo batch 100 records, tránh timeout khi có hàng nghìn voucher trong một request.
- **Excel import:** Sử dụng Apache POI đọc file .xlsx, validate từng dòng, lưu theo batch.
- **Audit logging:** Mọi thao tác (create, submit, confirm, cancel) đều được ghi vào audit_logs bằng `@Async` để không ảnh hưởng response time.
- **Data isolation:** Partner chỉ thấy voucher/mission do mình tạo (filter theo createdBy + storeName).
- **Mission-Voucher coupling:** Khi tạo mission với rewardType=VOUCHER, hệ thống tự động tạo VoucherRequest + VoucherDetail kèm theo (purpose=REWARD).

### 3.3.6. Chi tiết Customer Service

**Vai trò:** Phục vụ khách hàng cuối - thu thập voucher, thanh toán, tham gia nhiệm vụ.

**API Endpoints:**

| Method | Endpoint | Chức năng |
|---|---|---|
| GET | /api/customers/profile/{id} | Xem profile khách hàng |
| GET | /api/customers/vouchers/available/with-status | Voucher khả dụng (theo tier) |
| POST | /api/customers/vouchers/collect/{id} | Thu thập voucher |
| GET | /api/customers/vouchers/list | Kho voucher cá nhân |
| GET | /api/customers/vouchers/applicable | Voucher áp dụng được cho đơn hàng |
| GET | /api/customers/missions | Danh sách mission + tiến độ |
| POST | /api/customers/missions/claim-reward | Nhận thưởng mission |
| POST | /api/v1/payments/process | Xử lý thanh toán |
| GET | /api/customers/leaderboard | Bảng xếp hạng |
| GET | /api/customers/invoices | Danh sách hóa đơn mẫu |

**Đặc điểm triển khai:**
- **Voucher filtering by tier:** Khi customer xem voucher khả dụng, hệ thống gọi gRPC đến Voucher Service lấy danh sách voucher phù hợp với hạng (tier) của customer. Ví dụ: customer hạng GOLD thấy voucher tier ALL + SILVER + GOLD.
- **Payment flow:** Khi thanh toán:
  1. Validate voucher (còn hạn, đủ minOrderValue, còn lượt dùng).
  2. Tính discount amount.
  3. Lưu Transaction.
  4. Cập nhật CustomerVoucher (giảm availableUsage).
  5. Publish `LoyaltyPointEvent` qua Kafka (tích điểm bất đồng bộ).
- **Kafka Producer:** Sau mỗi giao dịch thành công, publish event để Loyalty Service tích điểm và cập nhật mission progress.
- **Kafka Consumer:** Lắng nghe `TierUpgradeEvent` để cập nhật hạng trong CustomerProfile khi Loyalty Service nâng hạng.
- **Leaderboard:** Sử dụng Redis Sorted Set, query top N customers theo totalPoints.

### 3.3.7. Chi tiết Loyalty Service

**Vai trò:** Quản lý điểm thưởng, hạng thành viên, và thực thi nhiệm vụ.

**gRPC Methods:**

| Method | Input | Output | Mô tả |
|---|---|---|---|
| CreateMission | CreateMissionRequest | MissionResponse | Tạo mission entity |
| UpdateMissionStatus | id, newStatus | UpdateStatusResponse | Cập nhật trạng thái |
| GetMissionById | missionId | MissionDetailResponse | Lấy chi tiết |
| SearchMissions | filters, pageable | SearchMissionResponse | Tìm kiếm |

**Kafka Consumer:**
- Lắng nghe `LoyaltyPointEvent`: Tính điểm (1 point / 1.000 VND), cộng vào CustomerEntity.totalPoints.
- Kiểm tra ngưỡng nâng hạng: Nếu totalPoints đạt ngưỡng → nâng tier → publish `TierUpgradeEvent`.
- Cập nhật mission progress: Nếu customer đang tham gia mission, cập nhật currentProgress.

**Quy tắc nâng hạng:**

| Hạng | Điểm tối thiểu |
|---|---|
| SILVER | 0 (mặc định) |
| GOLD | 1.000 |
| PLATINUM | 5.000 |
| DIAMOND | 10.000 |

### 3.3.8. Luồng giao tiếp giữa các service

**A. Giao tiếp đồng bộ (gRPC):**

```
┌──────────────┐  getNameStore()  ┌──────────────────┐
│Voucher Service├────────────────►│ Identity Service  │
└──────┬───────┘                  └──────────────────┘
       │ createMission()
       │ updateMissionStatus()
       ▼
┌──────────────┐
│Loyalty Service│
└──────────────┘
       ▲
       │ getMissions()
       │ searchVouchersByTier()
┌──────┴───────┐
│Customer Service│
└──────────────┘
```

**B. Giao tiếp bất đồng bộ (Kafka):**

```
Customer Service ──► [loyalty-point-topic] ──► Loyalty Service
                                                    │
                                                    ▼ (nếu nâng hạng)
Loyalty Service  ──► [tier-upgrade-topic]  ──► Customer Service
Customer Service ──► [voucher-used-topic]  ──► Voucher Service
All topics       ──────────────────────────► Notification Service
```

### 3.3.9. Bảo mật và phân quyền

**Luồng xác thực chi tiết:**

```
1. Client ──POST /auth/login──► Identity Service
2. Identity Service ──verify──► Keycloak
3. Keycloak ──JWT token──► Identity Service ──► Client
4. Client ──request + Bearer token──► Kong Gateway
5. Kong ──forward──► Target Service
6. Service ──verify JWT signature──► Keycloak Public Key
7. Spring Security ──extract roles──► @PreAuthorize check
```

**Cơ chế phân quyền:**

Mỗi endpoint được bảo vệ bằng annotation Spring Security:

```java
@PreAuthorize("hasRole('CHECKER')")          // Chỉ Checker
@PreAuthorize("hasAnyRole('MAKER', 'PARTNER')")  // Maker hoặc Partner
```

Ngoài ra, logic trong service layer kiểm tra thêm:
- Partner chỉ xem dữ liệu của mình: `if (isPartner()) filter by createdBy`.
- Checker chỉ thấy trạng thái phù hợp: PENDING_APPROVE, APPROVED, REJECTED, FINISH.

### 3.3.10. Cơ sở dữ liệu

**Phân chia database theo service:**

| Database | Service | Bảng chính |
|---|---|---|
| identity_db | Identity Service | users, partner |
| voucher_db | Voucher Service | voucher_requests, vouchers, audit_logs, mock_invoices |
| loyalty_db | Loyalty Service | tasks (missions), customer_points |
| customer_db | Customer Service | customer_profiles, customer_vouchers, customer_missions, transactions |

**Nguyên tắc Database per Service:**
- Mỗi service sở hữu database riêng, không service nào truy cập trực tiếp database của service khác.
- Khi cần dữ liệu từ service khác, phải gọi qua gRPC hoặc Kafka event.
- Đảm bảo loose coupling: có thể thay đổi schema của một service mà không ảnh hưởng service khác.

### 3.3.11. Xử lý lỗi và Exception Handling

Hệ thống sử dụng cơ chế xử lý lỗi thống nhất qua module `common`:

- `BaseException`: Custom exception chứa httpStatus, errorCode, description.
- `BaseResponse<T>`: Response wrapper thống nhất với status, code, message, data.
- `GlobalExceptionHandler`: Bắt tất cả exception và trả về format chuẩn.

**Response format thống nhất:**

```json
// Thành công
{ "status": 0, "code": "success", "message": "Success", "data": {...} }

// Lỗi
{ "status": 1, "code": "VOUCHER_NOT_FOUND", "message": "Voucher not found", "data": null }
```

Điều này giúp frontend xử lý response một cách nhất quán, không cần xử lý riêng cho từng API.
