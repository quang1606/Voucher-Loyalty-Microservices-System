# Implementation Plan: Voucher Request Statistics API

## Tổng quan

Triển khai API thống kê đợt phát voucher trong customer-service. Bao gồm mở rộng entity Transaction, tạo DTO response mới, thêm repository query, mở rộng service và controller.

## Tasks

- [x] 1. Mở rộng Transaction entity và tạo database migration
  - [x] 1.1 Thêm trường `requestId` vào entity `Transaction`
    - Thêm field `private String requestId` với annotation `@Column(name = "request_id", length = 64)`
    - Field phải nullable (không có `nullable = false`)
    - _Requirements: 1.1, 1.2, 1.3_
  - [x] 1.2 Tạo migration script cho database
    - Tạo file SQL migration thêm cột `request_id` VARCHAR(64) vào bảng `transactions`
    - Tạo index `idx_transactions_request_id` trên cột `request_id`
    - _Requirements: 1.1_

- [ ] 2. Tạo DTO response và mở rộng repository
  - [x] 2.1 Tạo class `VoucherRequestStatsResponse`
    - Tạo file `customer-service/src/main/java/com/example/customerservice/dto/response/VoucherRequestStatsResponse.java`
    - Fields: `usedVoucherCount` (Long), `totalDiscountAmount` (BigDecimal)
    - Sử dụng annotations: `@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`
    - _Requirements: 2.4_
  - [x] 2.2 Thêm query method vào `TransactionRepository`
    - Thêm JPQL query sử dụng `@Query` annotation với COUNT và COALESCE(SUM(...), 0)
    - Query filter theo `requestId` và `status = SUCCESS`
    - _Requirements: 2.1, 2.2, 2.3_
  - [-] 2.3 Viết property test cho statistics calculation

    - **Property 2: Statistics calculation correctness**
    - Tạo random list Transaction objects với mix status và requestId
    - Verify count và sum chỉ tính SUCCESS transactions với requestId khớp
    - Sử dụng jqwik library
    - **Validates: Requirements 2.1, 2.2, 2.3, 3.2**

- [ ] 3. Mở rộng service layer
  - [ ] 3.1 Thêm method `getVoucherRequestStats` vào interface `TransactionService`
    - Signature: `VoucherRequestStatsResponse getVoucherRequestStats(String requestId)`
    - _Requirements: 2.1, 2.2_
  - [ ] 3.2 Implement method trong `TransactionServiceImpl`
    - Validate requestId: throw `BaseException` với HTTP 400 nếu null/empty/whitespace
    - Gọi repository method và trả về kết quả
    - _Requirements: 2.1, 2.2, 2.3, 3.1, 3.2_
  - [ ] 3.3 Viết property test cho invalid requestId validation

    - **Property 3: Invalid requestId validation**
    - Generate random whitespace-only strings, verify trả về lỗi 400
    - Sử dụng jqwik library
    - **Validates: Requirements 3.1**

- [ ] 4. Mở rộng controller layer
  - [ ] 4.1 Thêm endpoint `/stats` vào `TransactionController`
    - Method: GET với `@RequestParam String requestId`
    - Annotation: `@PreAuthorize("hasRole('ADMIN')")`
    - Trả về `BaseResponse<VoucherRequestStatsResponse>`
    - _Requirements: 2.1, 2.2, 2.4, 4.1, 4.2_
  - [ ] 4.2 Viết unit test cho controller endpoint

    - Test case: requestId hợp lệ trả về 200 với data đúng format
    - Test case: requestId rỗng trả về 400
    - Test case: user không có role ADMIN trả về 403
    - _Requirements: 3.1, 4.1, 4.2_

- [ ] 5. Checkpoint - Đảm bảo tất cả tests pass
  - Ensure all tests pass, ask the user if questions arise.
