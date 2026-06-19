# SLIDE BÁO VỆ ĐỒ ÁN – NỘI DUNG TRÌNH BÀY

---

## SLIDE 1: TRANG BÌA

**Trên slide:** Tên đề tài, tên sinh viên, GVHD, lớp, năm

**Nói:**
Kính chào thầy/cô, em xin trình bày đồ án tốt nghiệp với đề tài "Xây dựng hệ thống quản lý Voucher và Chương trình Khách hàng Thân thiết theo kiến trúc Microservices". Hệ thống được xây dựng bằng Java Spring Boot, giao tiếp qua gRPC và Kafka, triển khai bằng Docker.

---

## SLIDE 2: NỘI DUNG TRÌNH BÀY

**Trên slide:** Danh sách mục trình bày

**Nói:**
Bài trình bày gồm các phần: Lý do chọn đề tài, bài toán cần giải quyết, kiến trúc hệ thống, các chức năng chính, công nghệ sử dụng, kết quả đạt được, và hướng phát triển.

---

## SLIDE 3: LÝ DO CHỌN ĐỀ TÀI

**Trên slide:**
- TMĐT & bán lẻ VN tăng trưởng → giữ chân KH là yếu tố sống còn
- Các hệ thống hiện có đều đóng, chi phí cao, khó tùy biến
- Chưa có giải pháp kết hợp đồng bộ Voucher + Loyalty + Mission + Maker/Checker

**Nói:**
Trong bối cảnh thương mại điện tử phát triển mạnh, việc giữ chân khách hàng qua các chương trình loyalty, voucher, gamification rất quan trọng. Tuy nhiên các hệ thống hiện tại như Shopee Rewards, MoMo, The Coffee House đều là hệ thống đóng, chỉ phục vụ riêng nền tảng đó, chi phí cao và khó tùy biến cho doanh nghiệp vừa nhỏ. Đặc biệt chưa có hệ thống nào kết hợp đồng bộ cả voucher, loyalty, mission, leaderboard cùng quy trình phê duyệt nhiều bước. Đây là lý do em chọn đề tài này.

---

## SLIDE 4: KHOẢNG TRỐNG THỊ TRƯỜNG

**Trên slide:** Bảng so sánh tính năng

| Tính năng | Shopee | MoMo | TCH | Đồ án |
|---|:---:|:---:|:---:|:---:|
| Phân hạng & tích điểm | ✓ | ✓ | ✓ | ✓ |
| Quy trình Maker/Checker | ✗ | ✗ | ✗ | ✓ |
| Mission + Leaderboard | ✗ | ✗ | ✗ | ✓ |
| Partner tạo voucher | ✗ | ✓ | ✗ | ✓ |
| Audit Log | ✗ | ✗ | ✗ | ✓ |

**Nói:**
Em đã khảo sát 6 hệ thống trong và ngoài nước. Bảng so sánh cho thấy không hệ thống nào có quy trình phê duyệt Maker/Checker để kiểm soát phát hành voucher, không có bảng xếp hạng, và thiếu audit log. Đồ án của em lấp đầy các khoảng trống này bằng cách tích hợp tất cả trong một nền tảng duy nhất.

---

## SLIDE 5: ĐỀ TÀI GIẢI QUYẾT ĐƯỢC GÌ

**Trên slide:**
1. Quy trình Maker/Checker → kiểm soát phát hành voucher
2. Nền tảng tích hợp: Loyalty + Voucher + Mission + Leaderboard
3. Partner tự tạo voucher/mission (data isolation)
4. Microservices → scale độc lập, dễ mở rộng
5. Bảo mật OAuth2, RBAC 5 vai trò
6. Audit log & monitoring toàn diện

**Nói:**
Hệ thống giải quyết 6 vấn đề chính. Thứ nhất là quy trình phê duyệt nhiều bước, tránh rủi ro phát hành voucher sai. Thứ hai là tích hợp đồng bộ tất cả yếu tố loyalty trong một hệ thống. Thứ ba là cho phép đối tác tham gia tạo voucher với cơ chế phân quyền dữ liệu. Thứ tư là kiến trúc Microservices cho phép scale từng module riêng. Thứ năm là bảo mật theo chuẩn công nghiệp. Và cuối cùng là hệ thống giám sát và audit log đầy đủ.

---

## SLIDE 6: KIẾN TRÚC HỆ THỐNG

**Trên slide:** Sơ đồ kiến trúc (hình vẽ)

```
Client → Kong Gateway → [Identity | Voucher | Customer | Loyalty | Notification]
                              ↕ gRPC        ↕ Kafka
                   PostgreSQL | Redis | Keycloak | ELK | Prometheus
```

**Nói:**
Hệ thống gồm 5 microservice giao tiếp qua 2 kênh: gRPC cho truy vấn đồng bộ cần response ngay, và Kafka cho xử lý bất đồng bộ như tích điểm, nâng hạng. Client gọi qua Kong API Gateway, Gateway route đến service đích. Mỗi service có database riêng theo nguyên tắc Database per Service. Toàn bộ triển khai bằng Docker Compose với hơn 15 container.

---

## SLIDE 7: CHỨC NĂNG - QUẢN TRỊ (Admin/Maker/Checker)

**Trên slide:**
- Admin: quản lý user, phân quyền, dashboard
- Maker/Partner: tạo voucher (đơn lẻ/Excel), tạo mission, gửi duyệt
- Checker: phê duyệt/từ chối, xem audit log
- Quy trình: INIT → PENDING_APPROVE → APPROVED → FINISH

**Nói:**
Phía quản trị có 3 vai trò chính. Admin quản lý toàn bộ tài khoản và phân quyền. Maker hoặc Partner tạo voucher bằng form hoặc upload Excel hàng loạt, tạo nhiệm vụ, rồi gửi phê duyệt. Checker xem xét và phê duyệt hoặc từ chối kèm lý do. Mọi thao tác đều được ghi audit log. Voucher chỉ được kích hoạt cho khách hàng sau khi Checker phê duyệt, đảm bảo kiểm soát chặt chẽ.

---

## SLIDE 8: CHỨC NĂNG - KHÁCH HÀNG (Customer)

**Trên slide:**
- Xem & thu thập voucher theo hạng thành viên
- Thanh toán áp dụng voucher giảm giá
- Tham gia mission → nhận thưởng (điểm/voucher)
- Tích điểm tự động (1pt/1.000đ), nâng hạng tự động
- Bảng xếp hạng (Leaderboard)

**Nói:**
Phía khách hàng, user có thể xem và thu thập voucher phù hợp với hạng thành viên của mình. Khi thanh toán, hệ thống tự động tích điểm với tỷ lệ 1 điểm cho mỗi 1.000 đồng, và tự động nâng hạng khi đạt ngưỡng. Khách hàng cũng có thể tham gia nhiệm vụ, hoàn thành mục tiêu để nhận thưởng. Bảng xếp hạng dùng Redis Sorted Set để hiển thị top khách hàng theo điểm.

---

## SLIDE 9: CÔNG NGHỆ SỬ DỤNG

**Trên slide:**

| Tầng | Stack |
|---|---|
| Frontend | Next.js, React, TypeScript, Tailwind |
| Backend | Java 17, Spring Boot 3.4 |
| Database | PostgreSQL 15, Redis 7 |
| Messaging | Apache Kafka |
| Inter-service | gRPC + Protobuf |
| Auth | Keycloak (OAuth2/OIDC) |
| Infra | Docker, Kong, Prometheus, Grafana, ELK |

**Nói:**
Về công nghệ, frontend dùng Next.js với kiến trúc BFF để proxy API. Backend dùng Java 17 và Spring Boot 3.4. Database là PostgreSQL cho dữ liệu chính và Redis cho cache cùng leaderboard. Giao tiếp giữa service dùng gRPC cho hiệu năng cao và Kafka cho xử lý event. Xác thực qua Keycloak theo chuẩn OAuth2. Toàn bộ hạ tầng container hóa bằng Docker, giám sát bằng Prometheus + Grafana, log tập trung bằng ELK Stack.

---

## SLIDE 10: GIAO TIẾP GIỮA CÁC SERVICE

**Trên slide:**

gRPC (đồng bộ):
- Voucher ↔ Identity: lấy tên Partner
- Voucher ↔ Loyalty: tạo mission
- Customer ↔ Voucher: tìm voucher theo hạng
- Customer ↔ Loyalty: lấy danh sách mission

Kafka (bất đồng bộ):
- loyalty-point-topic: thanh toán → tích điểm
- tier-upgrade-topic: đạt ngưỡng → nâng hạng
- voucher-used-topic: dùng voucher → giảm stock

**Nói:**
Hệ thống kết hợp 2 kiểu giao tiếp. gRPC dùng cho các trường hợp cần response ngay, ví dụ khi customer xem voucher thì Customer Service gọi gRPC sang Voucher Service để lấy danh sách theo hạng. Kafka dùng cho các luồng không cần response ngay, ví dụ sau khi thanh toán, Customer Service publish event lên Kafka, Loyalty Service consume và tự động tích điểm, kiểm tra nâng hạng. Nếu đạt ngưỡng thì publish tiếp event nâng hạng ngược về Customer Service.

---

## SLIDE 11: KẾT QUẢ ĐẠT ĐƯỢC

**Trên slide:**
- 22/22 use case hoạt động đúng
- Response time < 500ms
- Bảo mật: OAuth2 + RBAC 5 roles
- Triển khai: Docker Compose 15+ containers ổn định
- Monitoring & Logging real-time

**Nói:**
Hệ thống đã đáp ứng đầy đủ 22 chức năng đề ra. Thời gian phản hồi dưới 500ms nhờ Redis cache và gRPC binary protocol. Bảo mật theo chuẩn OAuth2 với 5 vai trò phân quyền rõ ràng. Toàn bộ 15+ container chạy ổn định trên Docker Compose. Hệ thống giám sát Prometheus + Grafana và logging ELK hoạt động real-time.

---

## SLIDE 12: KẾT LUẬN & HƯỚNG PHÁT TRIỂN

**Trên slide:**

Kết luận:
- Hoàn thành hệ thống đầy đủ chức năng
- Kiến trúc Microservices chuẩn công nghiệp
- Lấp khoảng trống: Maker/Checker + Loyalty + Mission + Leaderboard

Hướng phát triển:
- Mobile app (React Native)
- Kubernetes + Cloud
- AI recommendation
- Test coverage > 80%

**Nói:**
Tóm lại, đồ án đã hoàn thành việc xây dựng hệ thống quản lý voucher và loyalty hoàn chỉnh theo kiến trúc Microservices, giải quyết được khoảng trống mà các hệ thống hiện tại chưa đáp ứng. Về hướng phát triển, em dự định xây dựng mobile app cho khách hàng, migrate sang Kubernetes để auto-scaling, bổ sung AI recommendation để cá nhân hóa voucher, và nâng test coverage lên trên 80%. Em xin cảm ơn thầy/cô đã lắng nghe.
