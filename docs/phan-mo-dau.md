# PHẦN MỞ ĐẦU

## 1. Lý do chọn đề tài

Trong bối cảnh thương mại điện tử và bán lẻ tại Việt Nam phát triển mạnh mẽ, việc giữ chân khách hàng trở thành yếu tố then chốt quyết định sự thành bại của doanh nghiệp. Các chương trình khách hàng thân thiết (loyalty program), hệ thống voucher giảm giá, và gamification đã chứng minh hiệu quả trong việc tăng tỷ lệ quay lại và giá trị vòng đời khách hàng. Tuy nhiên, phần lớn các giải pháp hiện có trên thị trường đều là hệ thống đóng, chỉ phục vụ cho một nền tảng cụ thể (Shopee Rewards, MoMo, The Coffee House) hoặc có chi phí triển khai cao, khó tùy biến cho doanh nghiệp vừa và nhỏ.

Bên cạnh đó, xu hướng phát triển phần mềm hiện đại đòi hỏi các hệ thống phải đáp ứng được khả năng mở rộng (scalability), tính sẵn sàng cao (high availability), và khả năng tích hợp linh hoạt với các hệ thống bên ngoài. Kiến trúc Microservices cùng các công nghệ như Apache Kafka, gRPC, Redis Cache đã trở thành tiêu chuẩn trong việc xây dựng các hệ thống phân tán quy mô lớn.

Xuất phát từ những nhu cầu thực tiễn trên, nhóm quyết định chọn đề tài **"Xây dựng hệ thống quản lý Voucher và Chương trình Khách hàng Thân thiết theo kiến trúc Microservices"** nhằm:

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

- Hệ thống tập trung vào phần backend (API), không bao gồm giao diện người dùng (frontend).
- Hỗ trợ 4 vai trò người dùng: Maker, Checker, Partner, và Customer.
- Các chức năng chính: quản lý voucher (tạo, phê duyệt, phân phối), quản lý nhiệm vụ (mission), tích điểm và phân hạng khách hàng, thanh toán và áp dụng voucher, bảng xếp hạng (leaderboard).

## 3. Phương pháp nghiên cứu

- **Khảo sát thực tế**: Nghiên cứu các hệ thống loyalty và voucher đang hoạt động (Shopee Rewards, MoMo, The Coffee House, Starbucks Rewards, Smile.io) để rút ra các nghiệp vụ cốt lõi và bài học kinh nghiệm.
- **Phân tích hướng đối tượng**: Sử dụng UML (Use Case Diagram, Class Diagram, Sequence Diagram, Activity Diagram) để mô hình hóa hệ thống.
- **Thiết kế kiến trúc**: Áp dụng các design pattern phổ biến trong Microservices (API Gateway, Event-Driven Architecture, Database per Service, gRPC cho inter-service communication).
- **Phát triển Agile**: Xây dựng hệ thống theo từng module, kiểm thử liên tục, và triển khai bằng Docker Compose.

## 4. Cấu trúc đồ án

Đồ án được tổ chức thành 3 chương chính:

**Chương 1: Khảo sát nghiệp vụ bài toán**
Trình bày kết quả khảo sát các hệ thống quản lý voucher và chương trình khách hàng thân thiết tại Việt Nam (Shopee, MoMo, The Coffee House) và quốc tế (Starbucks Rewards, Smile.io). Từ đó xác định các nghiệp vụ cốt lõi, ưu nhược điểm của từng hệ thống, và đề xuất hướng phát triển cho đồ án.

**Chương 2: Phân tích thiết kế hệ thống**
Xác định các đối tượng người dùng (Maker, Checker, Partner, Customer), phân tích yêu cầu chức năng và phi chức năng. Thiết kế kiến trúc tổng quan hệ thống Microservices, thiết kế cơ sở dữ liệu, thiết kế API, và mô hình hóa bằng các sơ đồ UML (Use Case, Class Diagram, Sequence Diagram, Activity Diagram).

**Chương 3: Xây dựng và phát triển ứng dụng**
Trình bày chi tiết quá trình cài đặt và triển khai hệ thống: công nghệ sử dụng, cấu trúc mã nguồn từng service, cách thức giao tiếp giữa các service (gRPC, Kafka), cơ chế bảo mật, caching, logging, monitoring, và containerization bằng Docker. Kèm theo kết quả demo và đánh giá hệ thống.
