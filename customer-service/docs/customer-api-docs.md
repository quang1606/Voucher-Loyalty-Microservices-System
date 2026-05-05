# Customer Service & Identity Service - API Documentation (cho Frontend)

> Base URL qua API Gateway: `http://<host>:8000`
>
> - Identity Service: `/api/identity`
> - Customer Service: `/api/customers`

---

## Cấu trúc Response chung

Tất cả API đều trả về format:

```json
{
  "status": 0,
  "code": "success",
  "message": "Success",
  "data": { ... }
}
```

---

## Mã lỗi (Error Codes)

| status | code             | message                  |
|--------|------------------|--------------------------|
| 0      | success          | Success                  |
| 1      | error            | General error            |
| 1001   | VALIDATION_ERROR | Validation failed        |
| 1002   | UNAUTHORIZED     | Unauthorized             |
| 1003   | NOT_FOUND        | Resource not found       |
| 1004   | CONFLICT         | Resource already exists  |
| 1005   | BAD_REQUEST      | Bad request              |
| 1006   | INTERNAL_ERROR   | Internal server error    |

---

## 1. IDENTITY SERVICE - Authentication

### 1.1 Đăng nhập

```
POST /api/identity/api/v1/auth/login
```

**Request Body:**
```json
{
  "username": "string (required)",
  "password": "string (required)"
}
```

**Response (200):**
```json
{
  "status": 0,
  "code": "success",
  "message": "Success",
  "data": {
    "accessToken": "string",
    "refreshToken": "string",
    "expiresIn": 3600,
    "tokenType": "Bearer"
  }
}
```

---

### 1.2 Đăng ký tài khoản khách hàng

```
POST /api/identity/api/v1/auth/register
```

**Request Body:**
```json
{
  "username": "string (required)",
  "password": "string (required, min 6 ký tự)",
  "email": "string (required, email hợp lệ)",
  "firstName": "string (required)",
  "lastName": "string (required)"
}
```

**Response (201):**
```json
{
  "status": 0,
  "code": "success",
  "message": "Đăng ký thành công",
  "data": {
    "id": "uuid"
  }
}
```

---

### 1.3 Refresh Token

```
POST /api/identity/api/v1/auth/refresh
```

**Request Body:**
```json
{
  "refreshToken": "string (required)"
}
```

**Response (200):**
```json
{
  "status": 0,
  "code": "success",
  "message": "Success",
  "data": {
    "accessToken": "string",
    "refreshToken": "string",
    "expiresIn": 3600,
    "tokenType": "Bearer"
  }
}
```

---

### 1.4 Đổi mật khẩu

```
PUT /api/identity/api/v1/profile/password
```

**Headers:** `Authorization: Bearer <accessToken>`

**Request Body:**
```json
{
  "currentPassword": "string (required)",
  "newPassword": "string (required, min 6 ký tự)"
}
```

**Response (200):**
```json
{
  "status": 0,
  "code": "success",
  "message": "Success",
  "data": null
}
```

---

### 1.5 Lấy thông tin Profile

```
GET /api/identity/api/v1/profile
```

**Headers:** `Authorization: Bearer <accessToken>`

**Response (200):**
```json
{
  "status": 0,
  "code": "success",
  "message": "Success",
  "data": {
    "id": "uuid",
    "username": "string",
    "email": "string",
    "firstName": "string",
    "lastName": "string",
    "role": "CUSTOMER | ADMIN | PARTNER",
    "storeName": "string | null",
    "phone": "string | null",
    "category": "string | null"
  }
}
```

---

### 1.6 Cập nhật Profile

```
PUT /api/identity/api/v1/profile
```

**Headers:** `Authorization: Bearer <accessToken>`

**Request Body:**
```json
{
  "firstName": "string (optional)",
  "lastName": "string (optional)",
  "email": "string (optional)",
  "phone": "string (optional)",
  "storeName": "string (optional)"
}
```

**Response (200):**
```json
{
  "status": 0,
  "code": "success",
  "message": "Success",
  "data": null
}
```

---

## 2. CUSTOMER SERVICE - Customer Profile

### 2.1 Lấy thông tin khách hàng

```
GET /api/customers/api/customers/profile/{customerId}
```

**Path Params:** `customerId` (UUID)

**Response (200):**
```json
{
  "status": 0,
  "code": "success",
  "message": "Success",
  "data": {
    "id": 1,
    "userId": "uuid",
    "fullName": "string",
    "balance": 100000.00,
    "totalPoints": 500,
    "tier": "BRONZE | SILVER | GOLD | PLATINUM",
    "status": "ACTIVE | INACTIVE",
    "createdAt": "2024-01-01T00:00:00"
  }
}
```

---

## 3. CUSTOMER SERVICE - Vouchers

### 3.1 Danh sách voucher khả dụng (có trạng thái đã thu thập)

```
GET /api/customers/api/customers/vouchers/available/with-status
```

**Headers:** `Authorization: Bearer <accessToken>` (Role: CUSTOMER)

**Query Params:**
| Param      | Type | Default | Mô tả         |
|------------|------|---------|----------------|
| customerId | Long | required| ID khách hàng  |
| page       | int  | 0       | Trang          |
| size       | int  | 20      | Số lượng/trang |

**Response (200):**
```json
{
  "status": 0,
  "code": "success",
  "message": "Success",
  "data": {
    "data": [
      {
        "id": 1,
        "voucherCode": "VOUCHER001",
        "voucherName": "Giảm 50k",
        "description": "Giảm 50k cho đơn từ 200k",
        "customerTier": "BRONZE",
        "discountType": "FIXED | PERCENTAGE",
        "discountValue": "50000",
        "maxDiscount": "50000",
        "minOrderValue": "200000",
        "totalStock": 100,
        "availableStock": 50,
        "maxCollect": 1,
        "startDate": 1700000000000,
        "endDate": 1710000000000,
        "status": "ACTIVE",
        "createdAt": 1700000000000,
        "collected": true
      }
    ],
    "totalElements": 50,
    "totalPages": 3
  }
}
```

---

### 3.2 Thu thập voucher

```
POST /api/customers/api/customers/vouchers/collect/{voucherId}
```

**Headers:** `Authorization: Bearer <accessToken>` (Role: CUSTOMER)

**Path Params:** `voucherId` (Long)

**Query Params:**
| Param      | Type | Mô tả        |
|------------|------|---------------|
| customerId | Long | ID khách hàng |

**Response (200):**
```json
{
  "status": 0,
  "code": "success",
  "message": "Voucher collected successfully",
  "data": "Voucher has been added to your collection"
}
```

---

### 3.3 Danh sách voucher của khách hàng

```
GET /api/customers/api/customers/vouchers/list
```

**Headers:** `Authorization: Bearer <accessToken>` (Role: CUSTOMER)

**Query Params:**
| Param      | Type                  | Default          | Mô tả                              |
|------------|-----------------------|------------------|-------------------------------------|
| customerId | Long                  | optional         | ID khách hàng                       |
| voucherId  | Long                  | optional         | ID voucher                          |
| status     | String                | optional         | ACTIVE, USED, EXPIRED               |
| page       | int                   | 0                | Trang                               |
| size       | int                   | 20               | Số lượng/trang                      |
| sort       | String                | obtainedAt,desc  | Sắp xếp (field,direction)          |

**Response (200):**
```json
{
  "status": 0,
  "code": "success",
  "message": "Success",
  "data": {
    "data": [
      {
        "id": 1,
        "customerId": 1,
        "voucherId": 10,
        "availableUsage": 1,
        "voucherCode": "VOUCHER001",
        "nameStore": "Store A",
        "creatorType": "SYSTEM | PARTNER",
        "status": "ACTIVE | USED | EXPIRED",
        "obtainedAt": "2024-01-01T00:00:00",
        "usedAt": null,
        "expiredAt": "2024-12-31T23:59:59",
        "isCollected": true
      }
    ],
    "totalElements": 10,
    "totalPages": 1,
    "currentPage": 0,
    "pageSize": 20
  }
}
```

---

### 3.4 Voucher áp dụng được cho đơn hàng

```
GET /api/customers/api/customers/vouchers/applicable
```

**Headers:** `Authorization: Bearer <accessToken>` (Role: CUSTOMER)

**Query Params:**
| Param       | Type       | Mô tả              |
|-------------|------------|---------------------|
| customerId  | Long       | ID khách hàng       |
| nameStore   | String     | Tên cửa hàng        |
| orderAmount | BigDecimal | Giá trị đơn hàng    |

**Response (200):**
```json
{
  "status": 0,
  "code": "success",
  "message": "Success",
  "data": {
    "data": [
      {
        "voucherId": 10,
        "voucherCode": "VOUCHER001",
        "voucherName": "Giảm 50k",
        "description": "Giảm 50k cho đơn từ 200k",
        "discountType": "FIXED",
        "discountValue": "50000",
        "maxDiscount": "50000",
        "minOrderValue": "200000",
        "availableStock": 50,
        "nameStore": "Store A",
        "creatorType": "SYSTEM",
        "applicable": true,
        "reason": null
      }
    ],
    "totalElements": 5
  }
}
```

---

## 4. CUSTOMER SERVICE - Missions

### 4.1 Danh sách nhiệm vụ của khách hàng

```
GET /api/customers/api/customers/missions
```

**Headers:** `Authorization: Bearer <accessToken>` (Role: CUSTOMER)

**Query Params:**
| Param | Type | Default | Mô tả         |
|-------|------|---------|----------------|
| page  | int  | 0       | Trang          |
| size  | int  | 20      | Số lượng/trang |

**Response (200):**
```json
{
  "status": 0,
  "code": "success",
  "message": "Success",
  "data": {
    "missions": [
      {
        "missionId": 1,
        "missionName": "Chi tiêu 500k",
        "missionDescription": "Chi tiêu tổng 500k để nhận thưởng",
        "targetValue": 500000.0,
        "targetType": "SPENDING | TRANSACTION_COUNT",
        "rewardType": "POINT | VOUCHER",
        "rewardValue": "100",
        "partnerId": 1,
        "startDate": 1700000000000,
        "endDate": 1710000000000,
        "taskStatus": "ACTIVE | COMPLETED | EXPIRED",
        "currentProgress": 250000,
        "status": "IN_PROGRESS | COMPLETED | REWARD_CLAIMED",
        "voucherRequest": {
          "voucherCode": "REWARD_V01",
          "voucherName": "Voucher thưởng",
          "description": "Voucher thưởng nhiệm vụ",
          "discountType": "FIXED",
          "discountValue": "100000",
          "maxDiscount": "100000",
          "minOrderValue": "0",
          "totalStock": 100,
          "availableStock": 50,
          "startDate": 1700000000000,
          "endDate": 1710000000000,
          "voucherStatus": "ACTIVE",
          "nameStore": "Store A"
        }
      }
    ],
    "totalElements": 5,
    "totalPages": 1,
    "currentPage": 0,
    "pageSize": 20
  }
}
```

---

### 4.2 Nhận thưởng nhiệm vụ

```
POST /api/customers/api/customers/missions/claim-reward
```

**Headers:** `Authorization: Bearer <accessToken>` (Role: CUSTOMER)

**Request Body:**
```json
{
  "missionId": 1
}
```

**Response (200):**
```json
{
  "status": 0,
  "code": "success",
  "message": "Success",
  "data": {
    "rewardType": "POINT | VOUCHER",
    "rewardValue": "100",
    "message": "Bạn đã nhận 100 điểm thưởng"
  }
}
```

---

## 5. CUSTOMER SERVICE - Leaderboard

### 5.1 Bảng xếp hạng

```
GET /api/customers/api/customers/leaderboard
```

**Headers:** `Authorization: Bearer <accessToken>` (Role: CUSTOMER)

**Response (200):**
```json
{
  "status": 0,
  "code": "success",
  "message": "Success",
  "data": {
    "topCustomers": [
      {
        "customerId": 1,
        "customerName": "Nguyen Van A",
        "totalPoints": 5000,
        "rank": 1
      }
    ],
    "currentCustomer": {
      "customerId": 5,
      "customerName": "Tran Van B",
      "totalPoints": 1200,
      "rank": 15
    }
  }
}
```

---

## 6. CUSTOMER SERVICE - Invoices (Hóa đơn mẫu)

### 6.1 Danh sách hóa đơn

```
GET /api/customers/api/customers/invoices
```

**Query Params:**
| Param     | Type   | Default | Mô tả            |
|-----------|--------|---------|-------------------|
| nameStore | String | optional| Lọc theo cửa hàng|
| title     | String | optional| Lọc theo tiêu đề |
| page      | int    | 0       | Trang             |
| size      | int    | 20      | Số lượng/trang    |

**Response (200):**
```json
{
  "status": 0,
  "code": "success",
  "message": "Success",
  "data": {
    "data": [
      {
        "id": 1,
        "title": "Hóa đơn cafe",
        "nameStore": "Store A",
        "amount": "150000",
        "createdAt": 1700000000000,
        "updatedAt": 1700000000000
      }
    ],
    "totalElements": 20,
    "totalPages": 1
  }
}
```

---

## 7. CUSTOMER SERVICE - Payment

### 7.1 Thanh toán

```
POST /api/customers/api/v1/payments/process
```

**Request Body:**
```json
{
  "invoiceId": 1,
  "voucherId": 10,
  "orderAmount": 500000.00
}
```

> `voucherId` có thể null nếu không áp dụng voucher.

**Response (200):**
```json
{
  "transactionId": "TXN_20240101_001",
  "originalAmount": 500000.00,
  "discountAmount": 50000.00,
  "finalAmount": 450000.00,
  "pointsEarned": 45,
  "status": "SUCCESS | FAILED"
}
```

---

## Lỗi thường gặp

### Validation Error (400)
```json
{
  "status": 1001,
  "code": "VALIDATION_ERROR",
  "message": "Validation failed",
  "data": null
}
```

### Unauthorized (401)
```json
{
  "status": 1002,
  "code": "UNAUTHORIZED",
  "message": "Unauthorized",
  "data": null
}
```

### Not Found (404)
```json
{
  "status": 1003,
  "code": "NOT_FOUND",
  "message": "Resource not found",
  "data": null
}
```

### Conflict (409)
```json
{
  "status": 1004,
  "code": "CONFLICT",
  "message": "Resource already exists",
  "data": null
}
```

---

## Ghi chú cho Frontend

1. **Authentication Flow:**
   - Gọi `/api/identity/api/v1/auth/login` để lấy `accessToken` và `refreshToken`
   - Lưu token vào localStorage/cookie
   - Gắn `Authorization: Bearer <accessToken>` vào mọi request cần xác thực
   - Khi token hết hạn (401), gọi `/api/identity/api/v1/auth/refresh` để lấy token mới
   - Nếu refresh cũng thất bại → redirect về trang login

2. **Phân quyền:**
   - Các API customer-service yêu cầu role `CUSTOMER`
   - API profile identity-service yêu cầu đã đăng nhập (bất kỳ role)

3. **Pagination:**
   - `page` bắt đầu từ 0
   - `size` mặc định 20

4. **Enum Values:**
   - `CustomerTier`: BRONZE, SILVER, GOLD, PLATINUM
   - `CustomerVoucherStatus`: ACTIVE, USED, EXPIRED
   - `DiscountType`: FIXED, PERCENTAGE
   - `CreatorType`: SYSTEM, PARTNER
   - `CustomerMissionStatus`: IN_PROGRESS, COMPLETED, REWARD_CLAIMED
   - `TargetType`: SPENDING, TRANSACTION_COUNT
   - `RewardType`: POINT, VOUCHER
