<!--
QUY ĐỊNH TRÌNH BÀY ĐỒ ÁN (áp dụng khi chuyển sang Word)
==========================================================
- Lề trên: 2,5 cm | Lề dưới: 2,5 cm | Lề trái: 3 cm | Lề phải: 2 cm
- Font chữ: Times New Roman | Cỡ chữ nội dung: 13pt | Unicode
- Giãn dòng: 1.2 lines | Căn lề đoạn văn: Justify | Thụt đầu dòng: 1 cm
- Heading 1 (Chương): 18pt, Bold, Center, Before 0pt, After 12pt, bắt đầu trang mới
- Heading 2 (Mục): 16pt, Bold, Left, Before 6pt, After 6pt, không thụt đầu dòng
- Heading 3 (Tiểu mục): 14pt, Bold, Left, Before 6pt, After 6pt, không thụt đầu dòng
- Tên bảng: đặt phía trên bảng
- Tên hình: đặt phía dưới hình
- Đánh số bảng/hình gắn với số chương (VD: Hình 1.4, Bảng 3.1)
-->

# DANH MỤC TỪ VIẾT TẮT

| STT | Từ viết tắt | Tiếng Anh | Giải nghĩa |
|-----|-------------|-----------|-------------|
| 1 | API | Application Programming Interface | Giao diện lập trình ứng dụng |
| 2 | REST | Representational State Transfer | Kiến trúc truyền tải dữ liệu qua HTTP |
| 3 | gRPC | Google Remote Procedure Call | Giao thức gọi thủ tục từ xa hiệu năng cao |
| 4 | JWT | JSON Web Token | Chuẩn mã hóa thông tin xác thực dạng JSON |
| 5 | OAuth2 | Open Authorization 2.0 | Giao thức ủy quyền mở phiên bản 2.0 |
| 6 | OIDC | OpenID Connect | Giao thức xác thực dựa trên OAuth2 |
| 7 | RBAC | Role-Based Access Control | Kiểm soát truy cập dựa trên vai trò |
| 8 | SSO | Single Sign-On | Đăng nhập một lần cho nhiều hệ thống |
| 9 | IAM | Identity and Access Management | Quản lý danh tính và truy cập |
| 10 | ACID | Atomicity, Consistency, Isolation, Durability | Tính nguyên tử, nhất quán, cô lập, bền vững |
| 11 | JVM | Java Virtual Machine | Máy ảo Java |
| 12 | JIT | Just-In-Time Compilation | Biên dịch tức thời |
| 13 | LTS | Long-Term Support | Hỗ trợ dài hạn |
| 14 | ORM | Object-Relational Mapping | Ánh xạ đối tượng - quan hệ |
| 15 | JPA | Java Persistence API | API lưu trữ dữ liệu Java |
| 16 | DTO | Data Transfer Object | Đối tượng truyền tải dữ liệu |
| 17 | BFF | Backend-For-Frontend | Backend phục vụ riêng cho Frontend |
| 18 | UI | User Interface | Giao diện người dùng |
| 19 | UML | Unified Modeling Language | Ngôn ngữ mô hình hóa thống nhất |
| 20 | ELK | Elasticsearch, Logstash, Kibana | Bộ công cụ logging tập trung |
| 21 | CORS | Cross-Origin Resource Sharing | Chia sẻ tài nguyên giữa các nguồn gốc khác nhau |
| 22 | TTL | Time-To-Live | Thời gian sống (của cache/token) |
| 23 | CRUD | Create, Read, Update, Delete | Tạo, Đọc, Cập nhật, Xóa |
| 24 | CI/CD | Continuous Integration / Continuous Deployment | Tích hợp liên tục / Triển khai liên tục |
| 25 | OLTP | Online Transaction Processing | Xử lý giao dịch trực tuyến |
| 26 | OLAP | Online Analytical Processing | Xử lý phân tích trực tuyến |
| 27 | CSS | Cascading Style Sheets | Ngôn ngữ định dạng giao diện web |
| 28 | DOM | Document Object Model | Mô hình đối tượng tài liệu |
| 29 | F&B | Food and Beverage | Thực phẩm và đồ uống |

---

# PHẦN MỞ ĐẦU

## 1. Lý do chọn đề tài

Trong bối cảnh thương mại điện tử và bán lẻ tại Việt Nam phát triển mạnh mẽ, việc giữ chân khách hàng trở thành yếu tố then chốt quyết định sự thành bại của doanh nghiệp. Các chương trình khách hàng thân thiết (loyalty program), hệ thống voucher giảm giá, và gamification đã chứng minh hiệu quả trong việc tăng tỷ lệ quay lại và giá trị vòng đời khách hàng. Tuy nhiên, phần lớn các giải pháp hiện có trên thị trường đều là hệ thống đóng, chỉ phục vụ cho một nền tảng cụ thể (Shopee Rewards, MoMo, The Coffee House) hoặc có chi phí triển khai cao, khó tùy biến cho doanh nghiệp vừa và nhỏ.

Bên cạnh đó, xu hướng phát triển phần mềm hiện đại đòi hỏi các hệ thống phải đáp ứng được khả năng mở rộng (scalability), tính sẵn sàng cao (high availability), và khả năng tích hợp linh hoạt với các hệ thống bên ngoài. Kiến trúc Microservices cùng các công nghệ như Apache Kafka, gRPC, Redis Cache đã trở thành tiêu chuẩn trong việc xây dựng các hệ thống phân tán quy mô lớn.

Xuất phát từ những nhu cầu thực tiễn trên, nhóm quyết định chọn đề tài "Xây dựng hệ thống quản lý Voucher và Chương trình Khách hàng Thân thiết theo kiến trúc Microservices" nhằm:

- Nghiên cứu và áp dụng kiến trúc Microservices vào bài toán thực tế trong lĩnh vực thương mại điện tử.
- Xây dựng một hệ thống backend hoàn chỉnh có khả năng quản lý vòng đời voucher, tích điểm, phân hạng khách hàng, và gamification (nhiệm vụ).
- Tích hợp quy trình phê duyệt nhiều bước (Maker/Checker) đảm bảo kiểm soát chất lượng phát hành voucher.
- Ứng dụng các công nghệ hiện đại phổ biến trong ngành công nghiệp phần mềm: Spring Boot, Apache Kafka, gRPC, Redis, PostgreSQL, Docker, Kong API Gateway, Keycloak, ELK Stack, Prometheus/Grafana.

## 2. Mục tiêu và phạm vi đề tài

### 2.1. Mục tiêu

- Phân tích nghiệp vụ quản lý voucher và chương trình khách hàng thân thiết dựa trên khảo sát các hệ thống thực tế tại Việt Nam và quốc tế.
- Thiết kế kiến trúc hệ thống phân tán theo mô hình Microservices, đảm bảo tính module hóa, dễ bảo trì và mở rộng.
- Xây dựng hệ thống backend bao gồm 5 service: Identity Service, Voucher Service, Customer Service, Loyalty Service, và Notification Service.
- Triển khai giao tiếp đồng bộ (gRPC) và bất đồng bộ (Apache Kafka) giữa các service.
- Đảm bảo bảo mật thông qua xác thực OAuth2/OpenID Connect với Keycloak và phân quyền dựa trên vai trò (RBAC).
- Xây dựng hệ thống giám sát, logging tập trung phục vụ vận hành và debug.

### 2.2. Phạm vi

- Hệ thống bao gồm phần backend (API) và giao diện quản trị (frontend admin).
- Hỗ trợ 5 vai trò người dùng: Admin, Maker, Checker, Partner, và Customer.
- Các chức năng chính: quản lý voucher (tạo, phê duyệt, phân phối), quản lý nhiệm vụ (mission), tích điểm và phân hạng khách hàng, thanh toán và áp dụng voucher, bảng xếp hạng (leaderboard).

## 3. Phương pháp nghiên cứu

- Khảo sát thực tế: Nghiên cứu các hệ thống loyalty và voucher đang hoạt động (Shopee Rewards, MoMo, The Coffee House, Starbucks Rewards, Smile.io) để rút ra các nghiệp vụ cốt lõi và bài học kinh nghiệm.
- Phân tích hướng đối tượng: Sử dụng UML (Use Case Diagram, Class Diagram, Sequence Diagram, Activity Diagram) để mô hình hóa hệ thống.
- Thiết kế kiến trúc: Áp dụng các design pattern phổ biến trong Microservices (API Gateway, Event-Driven Architecture, Database per Service, gRPC cho inter-service communication).
- Phát triển Agile: Xây dựng hệ thống theo từng module, kiểm thử liên tục, và triển khai bằng Docker Compose.

## 4. Cấu trúc đồ án

Đồ án được tổ chức thành 4 chương chính:

Chương 1: Khảo sát nghiệp vụ bài toán — Trình bày kết quả khảo sát các hệ thống quản lý voucher và chương trình khách hàng thân thiết tại Việt Nam (Shopee Rewards, MoMo, The Coffee House) và quốc tế (Starbucks Rewards, Smile.io, Voucherify). Phân tích ưu nhược điểm, xác định khoảng trống thị trường, khảo sát nhu cầu 5 nhóm người dùng, và xác định yêu cầu chức năng/phi chức năng cho hệ thống.

Chương 2: Phân tích và thiết kế hệ thống — Phân tích yêu cầu chi tiết, xác định 5 tác nhân và 27 use case. Thiết kế hệ thống bằng các sơ đồ UML: biểu đồ Use Case (tổng quát và chi tiết theo 5 nhóm chức năng), 10 biểu đồ hoạt động, 10 biểu đồ trình tự, biểu đồ trạng thái. Thiết kế cơ sở dữ liệu (4 database, 11 bảng) và kiến trúc phần mềm Microservices.

Chương 3: Phát triển hệ thống — Trình bày chi tiết quá trình triển khai: kiến trúc tổng thể (14 thành phần), phát triển frontend (Next.js, React, TypeScript với kiến trúc BFF), phát triển backend (5 microservices Java/Spring Boot, gRPC, Kafka), đánh chỉ mục cơ sở dữ liệu, tích hợp 3 luồng nghiệp vụ chính (voucher, mission, nâng hạng), và đánh giá kết quả.

Chương 4: Bảo mật hệ thống — Trình bày giải pháp bảo mật toàn diện: xác thực JWT theo chuẩn OAuth2/OIDC (Keycloak), phân quyền RBAC kết hợp data-level access control, bảo mật API Gateway (Kong), bảo mật giao tiếp inter-service, đảm bảo toàn vẹn dữ liệu (Maker/Checker, audit log), và bảo mật phía frontend.


---

# CHƯƠNG 1: KHẢO SÁT NGHIỆP VỤ BÀI TOÁN

## 1.1. Khảo sát nghiệp vụ thực tế

### 1.1.1. Bối cảnh và đặt vấn đề

Trong bối cảnh cạnh tranh ngày càng gay gắt của thị trường thương mại điện tử và bán lẻ, việc giữ chân khách hàng trở thành yếu tố sống còn đối với doanh nghiệp. Theo xu hướng hiện nay, các doanh nghiệp không chỉ cạnh tranh về giá cả và chất lượng sản phẩm mà còn phải xây dựng mối quan hệ lâu dài với khách hàng thông qua các chương trình khách hàng thân thiết (loyalty program), hệ thống voucher giảm giá, và các hoạt động gamification nhằm tăng tương tác.

Nhóm đã tiến hành khảo sát các hệ thống quản lý voucher và chương trình khách hàng thân thiết đang hoạt động trên thị trường Việt Nam và quốc tế để hiểu rõ nghiệp vụ thực tế, từ đó xác định hướng phát triển cho đồ án.

### 1.1.2. Khảo sát các hệ thống tại Việt Nam

#### a) Shopee Rewards (Shopee)

Shopee Rewards là chương trình khách hàng thân thiết của sàn thương mại điện tử Shopee, hoạt động dựa trên mức chi tiêu và số đơn hàng của người dùng. Hệ thống phân hạng khách hàng thành 4 cấp: Classic, Silver, Gold, Platinum. Mỗi hạng được hưởng các quyền lợi khác nhau bao gồm voucher sinh nhật, voucher miễn phí vận chuyển, voucher giảm giá độc quyền, và ưu tiên hỗ trợ khách hàng cho hạng Platinum. Hạng thành viên được đánh giá lại mỗi 6 tháng dựa trên số đơn hàng hoàn thành [1].

Ưu điểm: Tích hợp chặt chẽ với hệ sinh thái Shopee, tự động nâng hạng, voucher đa dạng.

Hạn chế: Chỉ áp dụng trong hệ sinh thái Shopee, thiếu yếu tố gamification sâu (mission, leaderboard).

#### b) MoMo (M_Service)

MoMo là ví điện tử phổ biến nhất Việt Nam với hơn 23 triệu người dùng hoạt động, được chấp nhận tại hơn 80% cửa hàng F&B và 70% siêu thị. MoMo tích hợp chương trình tích điểm thưởng khi thanh toán, hệ thống voucher giảm giá từ đối tác, và các mini-game gamification (lắc xì, vòng quay may mắn). MoMo cũng cung cấp API phân phối voucher cho đối tác (Voucher Distribution API) cho phép doanh nghiệp tích hợp hệ thống voucher vào quy trình thanh toán [2].

Ưu điểm: Hệ sinh thái rộng, tích hợp thanh toán trực tiếp, gamification hấp dẫn.

Hạn chế: Chương trình loyalty chưa có hệ thống phân hạng rõ ràng, voucher chủ yếu do đối tác cung cấp chứ không có quy trình phê duyệt nội bộ.

#### c) The Coffee House App

Ứng dụng The Coffee House xây dựng chương trình thành viên với hệ thống tích điểm (BEAN) trên mỗi giao dịch, phân hạng thành viên, và các nhiệm vụ (mission) thú vị để nhận quà tặng miễn phí. Ứng dụng có khoảng 500.000 người dùng, sử dụng dữ liệu khách hàng để cá nhân hóa trải nghiệm và theo dõi hành trình mua sắm [3].

Ưu điểm: Kết hợp tốt giữa loyalty và gamification (mission), giao diện thân thiện, tích hợp đặt hàng.

Hạn chế: Chỉ áp dụng cho chuỗi The Coffee House, không có cơ chế cho đối tác bên ngoài tham gia, thiếu quy trình phê duyệt voucher nhiều bước.

### 1.1.3. Khảo sát các hệ thống quốc tế

#### a) Starbucks Rewards

Starbucks Rewards là một trong những chương trình loyalty thành công nhất thế giới với hơn 30 triệu thành viên sử dụng ứng dụng di động. Hệ thống tích Stars (điểm) trên mỗi giao dịch, cho phép đổi thưởng đồ uống miễn phí, và tích hợp sâu với ứng dụng di động (đặt hàng, thanh toán, theo dõi thưởng) [4].

Ưu điểm: Cá nhân hóa cao, tích hợp mobile app mượt mà, phân hạng rõ ràng, gamification qua challenges.

Hạn chế: Hệ thống đóng (chỉ cho Starbucks), chi phí phát triển và vận hành rất lớn.

#### b) Smile.io

Smile.io là nền tảng loyalty dành cho thương mại điện tử (chủ yếu Shopify), cung cấp giải pháp tích điểm, phân hạng VIP, và chương trình giới thiệu (referral) [5].

Ưu điểm: Dễ triển khai, hỗ trợ nhiều nền tảng e-commerce, có hệ thống VIP tiers.

Hạn chế: Thiếu gamification sâu (không có mission/leaderboard), khả năng tùy chỉnh hạn chế ở gói miễn phí, không có quy trình phê duyệt voucher.

#### c) Voucherify

Voucherify là nền tảng API-first chuyên về quản lý voucher, coupon, và chương trình khuyến mãi cho doanh nghiệp lớn. Hệ thống hỗ trợ tạo voucher hàng loạt, quy tắc phân phối phức tạp, và tích hợp qua API với bất kỳ hệ thống nào [6].

Ưu điểm: API-first linh hoạt, hỗ trợ quy mô lớn, logic phân phối voucher phức tạp.

Hạn chế: Yêu cầu kỹ thuật cao để tích hợp, chi phí enterprise, không có giao diện người dùng cuối.

### 1.1.4. Bảng so sánh tổng hợp

Bảng 1.1: So sánh tính năng giữa các hệ thống khảo sát

| Tính năng | Shopee | MoMo | The Coffee House | Starbucks | Smile.io | Đồ án |
|---|---|---|---|---|---|---|
| Phân hạng khách hàng | Có | Không | Có | Có | Có | Có |
| Tích điểm thưởng | Có | Có | Có | Có | Có | Có |
| Voucher giảm giá | Có | Có | Có | Có | Có | Có |
| Quy trình phê duyệt (Maker/Checker) | Không | Không | Không | Không | Không | Có |
| Mission/Gamification | Không | Có | Có | Có | Không | Có |
| Bảng xếp hạng (Leaderboard) | Không | Không | Không | Không | Không | Có |
| Đối tác tạo voucher/mission | Không | Có | Không | Không | Không | Có |
| Thanh toán tích hợp | Có | Có | Có | Có | Không | Có |
| Audit Log | N/A | N/A | N/A | N/A | Không | Có |

### 1.1.5. Nhận xét và khoảng trống

Qua khảo sát, nhóm nhận thấy các khoảng trống mà các hệ thống hiện tại chưa giải quyết đồng bộ:

Thứ nhất, thiếu quy trình phê duyệt voucher nhiều bước. Hầu hết các hệ thống không có cơ chế Maker/Checker để kiểm soát việc phát hành voucher, dẫn đến rủi ro phát hành sai hoặc lạm dụng.

Thứ hai, thiếu sự kết hợp đồng bộ giữa loyalty, voucher, mission và leaderboard. Các hệ thống thường chỉ mạnh ở 1-2 khía cạnh, chưa có giải pháp tích hợp đầy đủ cả 4 yếu tố trong một nền tảng.

Thứ ba, thiếu cơ chế cho đối tác (Partner) tham gia tạo voucher và mission.

Thứ tư, thiếu hệ thống giám sát và audit log chi tiết cho mọi thao tác liên quan đến voucher và mission.

Đây chính là cơ sở để nhóm xây dựng hệ thống với đầy đủ các tính năng trên.

## 1.2. Khảo sát nhu cầu người dùng

### 1.2.1. Xác định đối tượng người dùng

Bảng 1.2: Các đối tượng người dùng của hệ thống

| STT | Nhóm người dùng | Vai trò | Mô tả |
|---|---|---|---|
| 1 | Khách hàng (Customer) | Người dùng cuối | Mua sắm, tích điểm, thu thập voucher, tham gia nhiệm vụ |
| 2 | Quản trị viên (Admin) | Quản lý toàn hệ thống | Quản lý tài khoản, phân quyền, giám sát hoạt động |
| 3 | Người tạo (Maker) | Tạo nội dung | Tạo voucher, tạo nhiệm vụ, gửi yêu cầu phê duyệt |
| 4 | Người duyệt (Checker) | Phê duyệt nội dung | Xem xét và phê duyệt/từ chối yêu cầu từ Maker |
| 5 | Đối tác (Partner) | Nhà cung cấp dịch vụ | Tạo voucher và nhiệm vụ cho cửa hàng của mình |

### 1.2.2. Nhu cầu của khách hàng (Customer)

Qua khảo sát hành vi người dùng trên các ứng dụng Shopee, MoMo, The Coffee House, nhóm tổng hợp các nhu cầu chính:

- Xem thông tin cá nhân: hạng thành viên, điểm tích lũy, số dư tài khoản.
- Duyệt và thu thập voucher giảm giá phù hợp với hạng thành viên.
- Quản lý kho voucher đã thu thập (xem trạng thái: khả dụng, đã dùng, hết hạn).
- Áp dụng voucher khi thanh toán và được gợi ý voucher phù hợp với đơn hàng.
- Tham gia nhiệm vụ (mission) với mục tiêu rõ ràng và theo dõi tiến độ trực quan.
- Nhận thưởng tự động khi hoàn thành nhiệm vụ (điểm hoặc voucher).
- So sánh thứ hạng với khách hàng khác qua bảng xếp hạng.
- Được nâng hạng tự động khi đạt đủ điểm tích lũy.

### 1.2.3. Nhu cầu của Maker và Checker

Đối với Maker (Người tạo):
- Tạo voucher đơn lẻ hoặc hàng loạt thông qua upload file Excel.
- Tạo nhiệm vụ với cấu hình linh hoạt (mục tiêu, phần thưởng, thời hạn).
- Gửi yêu cầu phê duyệt đến Checker.
- Theo dõi trạng thái yêu cầu đã gửi.

Đối với Checker (Người duyệt):
- Xem danh sách yêu cầu chờ duyệt.
- Xem chi tiết từng yêu cầu (thông tin voucher, số lượng, thời hạn).
- Phê duyệt hoặc từ chối kèm lý do.
- Xem lịch sử phê duyệt thông qua audit log.

### 1.2.4. Nhu cầu của đối tác (Partner)

- Tạo voucher riêng cho cửa hàng của mình.
- Tạo nhiệm vụ gắn với thương hiệu.
- Theo dõi số lượng voucher đã phát hành và sử dụng.
- Gửi yêu cầu phê duyệt trước khi voucher/mission được kích hoạt.

### 1.2.5. Nhu cầu của quản trị viên (Admin)

- Quản lý toàn bộ tài khoản người dùng và phân quyền.
- Tạo tài khoản cho Maker, Checker, Partner.
- Giám sát hoạt động hệ thống qua dashboard.
- Xem audit log để kiểm tra mọi thao tác.

## 1.3. Phân tích yêu cầu hệ thống

### 1.3.1. Yêu cầu chức năng

Dựa trên kết quả khảo sát, nhóm xác định các yêu cầu chức năng phân chia theo module:

Module 1 – Xác thực và Phân quyền (Identity Service): Đăng ký, đăng nhập, refresh token, phân quyền theo vai trò (ADMIN, CUSTOMER, MAKER, CHECKER, PARTNER), quản lý hồ sơ người dùng, quản lý tài khoản hệ thống.

Module 2 – Quản lý Voucher (Voucher Service): Tạo voucher đơn lẻ và hàng loạt (Excel), quy trình phê duyệt nhiều bước (INIT → PENDING_APPROVE → APPROVED/REJECTED), hỗ trợ giảm giá cố định (FIXED) và phần trăm (PERCENT), quản lý kho voucher, phân loại theo hạng khách hàng, ghi audit log.

Module 3 – Quản lý Nhiệm vụ và Loyalty (Loyalty Service): Tạo nhiệm vụ theo mục tiêu chi tiêu hoặc số lần mua, cấu hình phần thưởng (điểm hoặc voucher), tính điểm tự động khi thanh toán, nâng hạng khách hàng tự động.

Module 4 – Dịch vụ Khách hàng (Customer Service): Xem hồ sơ cá nhân, duyệt và thu thập voucher, xem voucher áp dụng được cho đơn hàng, xem nhiệm vụ và tiến độ, nhận thưởng, thanh toán, bảng xếp hạng.

Module 5 – Thông báo (Notification Service): Gửi thông báo khi nâng hạng, nhận voucher thưởng, hoàn thành nhiệm vụ.

### 1.3.2. Yêu cầu phi chức năng

Bảng 1.3: Yêu cầu phi chức năng của hệ thống

| STT | Yêu cầu | Mô tả | Giải pháp |
|---|---|---|---|
| 1 | Hiệu năng | Thời gian phản hồi API dưới 500ms | Microservices, Redis cache, Kafka bất đồng bộ |
| 2 | Khả năng mở rộng | Scale từng module độc lập | Docker container, service riêng biệt |
| 3 | Bảo mật | Xác thực chuẩn công nghiệp, phân quyền chặt chẽ | Keycloak (OAuth2/OIDC), JWT, Kong Gateway |
| 4 | Độ tin cậy | Không mất dữ liệu giao dịch | Kafka message delivery, PostgreSQL ACID |
| 5 | Giám sát | Theo dõi hiệu năng real-time | Prometheus + Grafana, ELK Stack |
| 6 | Bảo trì | Dễ bảo trì và phát triển thêm | Tách biệt service, gRPC contract, audit log |

## 1.4. Kết luận chương

Qua quá trình khảo sát nghiệp vụ thực tế trên các hệ thống tại Việt Nam (Shopee Rewards, MoMo, The Coffee House) và quốc tế (Starbucks Rewards, Smile.io, Voucherify), nhóm đã xác định được các khoảng trống mà hệ thống hiện tại chưa giải quyết đồng bộ. Đặc biệt, việc kết hợp quy trình phê duyệt Maker/Checker, hệ thống loyalty đa hạng, gamification qua mission, bảng xếp hạng, và cơ chế cho đối tác tham gia trong một nền tảng duy nhất là điểm khác biệt chính của đồ án.

Dựa trên khảo sát nhu cầu của 5 nhóm người dùng, nhóm đã xác định đầy đủ các yêu cầu chức năng và phi chức năng làm cơ sở cho việc phân tích thiết kế ở Chương 2 và xây dựng phát triển ở Chương 3.
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

Bảng 2.11: Mô tả trạng thái Voucher Detail

| Trạng thái | Mô tả | Điều kiện chuyển |
|---|---|---|
| INACTIVE | Chưa kích hoạt | Mặc định khi tạo |
| ACTIVE | Đang hoạt động, customer có thể thu thập | Sau khi request APPROVED |
| EXPIRED | Đã hết hạn | endDate < thời gian hiện tại |

### 2.5.3. Trạng thái Customer Voucher

Bảng 2.12: Mô tả trạng thái Customer Voucher

| Trạng thái | Mô tả | Điều kiện chuyển |
|---|---|---|
| AVAILABLE | Đã thu thập, có thể sử dụng | Khi customer collect thành công |
| USED | Đã sử dụng hết lượt | availableUsage = 0 sau thanh toán |
| EXPIRED | Đã hết hạn | expiredAt < thời gian hiện tại |

### 2.5.4. Trạng thái Customer Mission

Bảng 2.13: Mô tả trạng thái Customer Mission

| Trạng thái | Mô tả | Điều kiện chuyển |
|---|---|---|
| IN_PROGRESS | Đang thực hiện | Khi customer tham gia mission |
| COMPLETED | Đã hoàn thành, chờ nhận thưởng | currentProgress >= targetValue |
| CLAIMED | Đã nhận thưởng | Sau khi claim reward thành công |


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
