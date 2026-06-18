# API Documentation – Customer Frontend

> Base URL: `http://localhost:8000`
>
> Identity: `/api/identity/api/v1`
>
> Customer: `/api/customers/api/customers`
>
> Payment: `/api/customers/api/v1`

---

## Response format chung

```json
{ "status": 0, "code": "success", "message": "Success", "data": { ... } }
```

```json
{ "status": 1002, "code": "UNAUTHORIZED", "message": "...", "data": null }
```

---

## 1. Xác thực (Identity Service)

### 1.1 Đăng ký

```
POST /api/identity/api/v1/auth/register
```

Request:
```json
{
  "username": "string (required)",
  "password": "string (required, min 6)",
  "email": "string (required, email format)",
  "firstName": "string (required)",
  "lastName": "string (required)"
}
```

Response (201):
```json
{
  "status": 0,
  "code": "success",
  "message": "Đăng ký thành công",
  "data": { "id": "uuid" }
}
```

Lỗi:
| Code | Mô tả |
|------|--------|
| VALIDATION_ERROR | Thiếu field hoặc sai format |
| CONFLICT | Username/email đã tồn tại |

---

### 1.2 Đăng nhập

```
POST /api/identity/api/v1/auth/login
```

Request:
```json
{
  "username": "string (required)",
  "password": "string (required)"
}
```

Response (200):
```json
{
  "status": 0,
  "code": "success",
  "message": "Success",
  "data": {
    "accessToken": "eyJhbGciOi...",
    "refreshToken": "eyJhbGciOi...",
    "expiresIn": 300,
    "tokenType": "Bearer"
  }
}
```

Lỗi:
| Code | Mô tả |
|------|--------|
| UNAUTHORIZED | Sai username hoặc password |

---

### 1.3 Refresh Token

```
POST /api/identity/api/v1/auth/refresh
```

Request:
```json
{
  "refreshToken": "string (required)"
}
```

Response (200): Giống response login (accessToken + refreshToken mới).

Lỗi:
| Code | Mô tả |
|------|--------|
| UNAUTHORIZED | Refresh token hết hạn hoặc không hợp lệ → redirect login |

---

### 1.4 Đổi mật khẩu

```
PUT /api/identity/api/v1/profile/password
```

Headers: `Authorization: Bearer <accessToken>`

Request:
```json
{
  "currentPassword": "string (required)",
  "newPassword": "string (required, min 6)"
}
```

Response (200):
```json
{ "status": 0, "code": "success", "message": "Success", "data": null }
```

Lỗi:
| Code | Mô tả |
|------|--------|
| UNAUTHORIZED | Mật khẩu cũ sai |
| VALIDATION_ERROR | Mật khẩu mới < 6 ký tự |

---

### 1.5 Xem Profile

```
GET /api/identity/api/v1/profile
```

Headers: `Authorization: Bearer <accessToken>`

Response (200):
```json
{
  "status": 0,
  "code": "success",
  "message": "Success",
  "data": {
    "id": "uuid",
    "username": "customer01",
    "email": "customer@mail.com",
    "firstName": "Nguyen",
    "lastName": "Van A",
    "role": "CUSTOMER",
    "storeName": null,
    "phone": null,
    "category": null
  }
}
```

---

## 2. Customer Profile

### 2.1 Xem thông tin khách hàng

```
GET /api/customers/api/customers/profile/{customerId}
```

| Param | Type | Mô tả |
|-------|------|--------|
| customerId | UUID | userId từ JWT token |

Response:
```json
{
  "status": 0,
  "code": "success",
  "data": {
    "id": 1,
    "userId": "uuid",
    "fullName": "Nguyen Van A",
    "balance": 500000.00,
    "totalPoints": 1200,
    "tier": "GOLD",
    "status": "ACTIVE",
    "createdAt": "2024-01-15T10:30:00"
  }
}
```

---

## 3. Voucher

### 3.1 Danh sách voucher khả dụng

```
GET /api/customers/api/customers/vouchers/available/with-status?customerId={id}&page=0&size=20
```

Headers: `Authorization: Bearer <accessToken>`

Response:
```json
{
  "data": {
    "data": [
      {
        "id": 1,
        "voucherCode": "VCH-ABC123",
        "voucherName": "Giảm 50k",
        "description": "Đơn từ 200k",
        "customerTier": "ALL",
        "discountType": "FIXED",
        "discountValue": "50000",
        "maxDiscount": "50000",
        "minOrderValue": "200000",
        "totalStock": 100,
        "availableStock": 45,
        "maxCollect": 2,
        "startDate": "1700000000000",
        "endDate": "1710000000000",
        "status": "ACTIVE",
        "createdAt": "1700000000000",
        "collected": false
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
POST /api/customers/api/customers/vouchers/collect/{voucherId}?customerId={id}
```

Headers: `Authorization: Bearer <accessToken>`

Response:
```json
{ "status": 0, "message": "Voucher collected successfully", "data": "Voucher has been added to your collection" }
```

Lỗi: `VOUCHER_ALREADY_COLLECTED`, `VOUCHER_OUT_OF_STOCK`

---

### 3.3 Kho voucher cá nhân

```
GET /api/customers/api/customers/vouchers/list?customerId={id}&status=AVAILABLE&page=0&size=20&sort=obtainedAt,desc
```

Headers: `Authorization: Bearer <accessToken>`

---

### 3.4 Voucher áp dụng được cho đơn hàng

```
GET /api/customers/api/customers/vouchers/applicable?customerId={id}&nameStore=Coffee House&orderAmount=500000
```

Headers: `Authorization: Bearer <accessToken>`

---

## 4. Mission

### 4.1 Danh sách nhiệm vụ

```
GET /api/customers/api/customers/missions?page=0&size=20
```

Headers: `Authorization: Bearer <accessToken>`

Response chứa `missions[]` với: missionId, missionName, targetValue, targetType, rewardType, currentProgress, status (IN_PROGRESS / COMPLETED / CLAIMED).

---

### 4.2 Nhận thưởng

```
POST /api/customers/api/customers/missions/claim-reward
```

Headers: `Authorization: Bearer <accessToken>`

Request:
```json
{ "missionId": 1 }
```

Response:
```json
{ "data": { "rewardType": "POINT", "rewardValue": "100", "message": "Successfully claimed 100 points" } }
```

Lỗi: `MISSION_NOT_COMPLETED`, `CUSTOMER_MISSION_NOT_FOUND`

---

## 5. Leaderboard

```
GET /api/customers/api/customers/leaderboard
```

Headers: `Authorization: Bearer <accessToken>`

---

## 6. Hóa đơn

```
GET /api/customers/api/customers/invoices?nameStore=&title=&page=0&size=20
```

---

## 7. Thanh toán

```
POST /api/customers/api/v1/payments/process
```

Request:
```json
{ "invoiceId": 1, "voucherId": 10, "orderAmount": 500000.00 }
```

Response (không wrap BaseResponse):
```json
{
  "transactionId": "TXN-ABC123",
  "originalAmount": 500000.00,
  "discountAmount": 50000.00,
  "finalAmount": 450000.00,
  "pointsEarned": 450,
  "status": "SUCCESS"
}
```

---

## 8. Thống kê đợt phát voucher (Admin)

### 8.1 Thống kê theo requestId

```
GET /api/customers/api/customers/transactions/stats?requestId={requestId}&month={month}&year={year}
```

Headers: `Authorization: Bearer <accessToken>` (role ADMIN)

| Param | Type | Required | Mô tả |
|-------|------|----------|--------|
| requestId | String | ✅ | Mã đợt phát voucher (vd: VOUCHER_1781032408977) |
| month | Integer | ❌ | Tháng lọc (1-12) |
| year | Integer | ❌ | Năm lọc (vd: 2026) |

Response (200):
```json
{
  "status": 0,
  "code": "success",
  "message": "Success",
  "data": {
    "usedVoucherCount": 15,
    "totalDiscountAmount": 750000.00
  }
}
```

Lỗi:
| HTTP | Code | Mô tả |
|------|------|--------|
| 400 | INVALID_REQUEST_ID | requestId trống hoặc null |
| 403 | FORBIDDEN | Không có quyền ADMIN |
| 500 | INTERNAL_SERVER_ERROR | Lỗi hệ thống |

> Nếu không có giao dịch nào, trả về `usedVoucherCount: 0`, `totalDiscountAmount: 0`

Ví dụ gọi:
```
# Thống kê tất cả
GET /api/customers/api/customers/transactions/stats?requestId=VOUCHER_1781032408977

# Lọc theo năm
GET /api/customers/api/customers/transactions/stats?requestId=VOUCHER_1781032408977&year=2026

# Lọc theo tháng + năm
GET /api/customers/api/customers/transactions/stats?requestId=VOUCHER_1781032408977&month=6&year=2026
```

---

## Flow tích hợp

```
1. Register → Login → lưu token
2. GET profile → hiển thị tier, points
3. GET vouchers/available → hiển thị danh sách
4. POST collect → thu thập voucher
5. GET invoices → chọn hóa đơn
6. GET vouchers/applicable → chọn voucher áp dụng
7. POST payments/process → thanh toán
8. GET missions → xem tiến độ
9. POST claim-reward → nhận thưởng (khi COMPLETED)
10. GET leaderboard → xem xếp hạng
```

---

## Enum

| Enum | Values |
|------|--------|
| CustomerTier | SILVER, GOLD, PLATINUM, DIAMOND |
| CustomerVoucherStatus | AVAILABLE, USED, EXPIRED |
| CustomerMissionStatus | IN_PROGRESS, COMPLETED, CLAIMED |
| DiscountType | FIXED, PERCENT |
| CreatorType | SYSTEM, PARTNER |
