# Tài liệu Yêu cầu

## Giới thiệu

Tính năng này cung cấp API thống kê cho đợt phát voucher (voucher request). API cho phép truy vấn số lượng voucher đã được sử dụng và tổng số tiền giảm giá trong một đợt phát voucher cụ thể. Để thực hiện điều này, cần bổ sung trường `requestId` vào entity `Transaction` trong customer-service nhằm liên kết giao dịch với đợt phát voucher tương ứng.

## Thuật ngữ

- **Transaction**: Giao dịch của khách hàng, lưu trữ thông tin thanh toán bao gồm voucher đã sử dụng.
- **VoucherRequest**: Đợt phát voucher, đại diện cho một lô voucher được tạo ra trong hệ thống voucher-service.
- **RequestId**: Mã định danh duy nhất của một đợt phát voucher, dùng để liên kết giao dịch với đợt phát.
- **Statistics_API**: API thống kê cung cấp thông tin tổng hợp về việc sử dụng voucher trong một đợt phát.
- **Customer_Service**: Microservice quản lý thông tin khách hàng và giao dịch.

## Yêu cầu

### Yêu cầu 1: Bổ sung trường requestId vào Transaction

**User Story:** Là một quản trị viên, tôi muốn mỗi giao dịch có voucher được liên kết với đợt phát voucher tương ứng, để có thể thống kê theo từng đợt phát.

#### Tiêu chí chấp nhận

1. THE Transaction entity SHALL chứa trường `requestId` kiểu String với độ dài tối đa 64 ký tự
2. WHEN một giao dịch được tạo với voucher thuộc một đợt phát, THE Customer_Service SHALL lưu trữ `requestId` tương ứng vào trường `requestId` của Transaction
3. WHEN một giao dịch được tạo mà không sử dụng voucher, THE Customer_Service SHALL để trường `requestId` là null

### Yêu cầu 2: API thống kê theo đợt phát voucher

**User Story:** Là một quản trị viên, tôi muốn xem thống kê số lượng voucher đã sử dụng và tổng tiền giảm giá theo đợt phát voucher, để đánh giá hiệu quả của từng đợt phát.

#### Tiêu chí chấp nhận

1. WHEN một request GET được gửi đến endpoint thống kê với tham số `requestId`, THE Statistics_API SHALL trả về số lượng voucher đã được sử dụng trong đợt phát đó
2. WHEN một request GET được gửi đến endpoint thống kê với tham số `requestId`, THE Statistics_API SHALL trả về tổng số tiền giảm giá (tổng `discountAmount`) của các giao dịch thành công trong đợt phát đó
3. WHEN một request GET được gửi đến endpoint thống kê với tham số `requestId`, THE Statistics_API SHALL chỉ tính các giao dịch có trạng thái SUCCESS
4. THE Statistics_API SHALL trả về kết quả theo định dạng BaseResponse với các trường: `usedVoucherCount` (số lượng voucher đã dùng) và `totalDiscountAmount` (tổng tiền giảm giá)

### Yêu cầu 3: Xử lý lỗi và trường hợp ngoại lệ

**User Story:** Là một quản trị viên, tôi muốn API xử lý đúng các trường hợp lỗi, để tôi nhận được thông báo rõ ràng khi có vấn đề.

#### Tiêu chí chấp nhận

1. WHEN `requestId` không được cung cấp hoặc là chuỗi rỗng, THE Statistics_API SHALL trả về lỗi HTTP 400 với thông báo mô tả lỗi
2. WHEN không có giao dịch nào tương ứng với `requestId` được cung cấp, THE Statistics_API SHALL trả về kết quả với `usedVoucherCount` bằng 0 và `totalDiscountAmount` bằng 0
3. IF xảy ra lỗi hệ thống trong quá trình truy vấn, THEN THE Statistics_API SHALL trả về lỗi HTTP 500 với thông báo lỗi chung

### Yêu cầu 4: Phân quyền truy cập API

**User Story:** Là một quản trị viên hệ thống, tôi muốn chỉ người dùng có quyền phù hợp mới có thể truy cập API thống kê, để đảm bảo an toàn dữ liệu.

#### Tiêu chí chấp nhận

1. WHEN một request được gửi đến Statistics_API bởi người dùng có role ADMIN, THE Statistics_API SHALL xử lý request và trả về kết quả
2. WHEN một request được gửi đến Statistics_API bởi người dùng không có role ADMIN, THE Statistics_API SHALL trả về lỗi HTTP 403
