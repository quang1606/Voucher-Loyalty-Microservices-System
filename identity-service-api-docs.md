# Voucher Loyalty Microservices - API Documentation

---

## Base URLs

| Service | URL |
|---|---|
| Identity Service | `http://localhost:8081` |
| Voucher Service | `http://localhost:8082` |

---

## Response chung

```json
{
  "status": 0,
  "code": "success",
  "message": "...",
  "data": { ... }
}
```

### Error Response

```json
{
  "status": 1,
  "code": "ERROR_CODE",
  "message": "Mô tả lỗi",
  "data": null
}
```

| HTTP Status | Ý nghĩa |
|---|---|
| 200 | Thành công |
| 400 | Request không hợp lệ |
| 401 | Chưa xác thực hoặc token không hợp lệ |
| 403 | Không có quyền truy cập |
| 404 | Không tìm thấy tài nguyên |
| 500 | Lỗi server |

---

## Xác thực

Tất cả API (trừ Auth) yêu cầu header:

```
Authorization: Bearer <accessToken>
```

---

## Enum Values

### Identity Service

| Enum | Values |
|---|---|
| Role | `ADMIN`, `MERCHANT`, `CUSTOMER`, `MAKER`, `CHECKER`, `PARTNER` |
| MerchantCategory | `FOOD`, `BEVERAGE`, `FASHION`, `ELECTRONICS`, `BEAUTY`, `HEALTH`, `EDUCATION`, `ENTERTAINMENT`, `TRAVEL`, `OTHER` |
| MerchantStatus | `ACTIVE`, `LOCKED` |
| CustomerTier | `BRONZE`, `SILVER`, `GOLD`, `PLATINUM`, `DIAMOND`, `ALL` |

### Voucher Service

| Enum | Values |
|---|---|
| DiscountType | `FIXED`, `PERCENT` |
| VoucherPurpose | `REWARD`, `HUNT` |
| CreatorType | `PARTNER`, `SYSTEM` |
| RequestMode | `SINGLE`, `EXCEL` |
| RequestStatus | `DRAFT`, `CANCELLED`, `PENDING_APPROVE`, `INIT`, `APPROVED`, `REJECTED`, `PROCESSING`, `FAILED`, `FINISH`, `SUCCESS` |
| VoucherStatus | `ACTIVE`, `INACTIVE`, `EXPIRED` |
| ConfirmAction | `APPROVED`, `REJECTED` |
| RewardType | `POINT`, `VOUCHER` |
| TaskStatus | `CANCELLED`, `PENDING_APPROVE`, `INIT`, `APPROVED`, `REJECTED`, `FAILED`, `FINISH` |

---

# IDENTITY SERVICE

## 1. AUTH (Public - Không cần token)

### 1.1 Đăng nhập

```
POST /api/v1/auth/login
```

**Request Body:**

```json
{
  "username": "string",
  "password": "string"
}
```

**Response:**

```json
{
  "status": 0,
  "code": "success",
  "message": "Success",
  "data": {
    "accessToken": "eyJhbGciOiJSUzI1NiIs...",
    "refreshToken": "eyJhbGciOiJIUzUxMiIs...",
    "expiresIn": 300,
    "tokenType": "Bearer"
  }
}
```

> FE lưu `accessToken` để gọi các API khác, lưu `refreshToken` để refresh khi token hết hạn.

---

### 1.2 Đăng ký Customer

```
POST /api/v1/auth/register
```

**Request Body:**

```json
{
  "username": "string",
  "password": "string",
  "email": "string",
  "firstName": "string",
  "lastName": "string"
}
```

**Response:**

```json
{
  "data": {
    "id": "uuid"
  }
}
```

---

### 1.3 Refresh Token

```
POST /api/v1/auth/refresh
```

**Request Body:**

```json
{
  "refreshToken": "eyJhbGciOiJIUzUxMiIs..."
}
```

**Response:** Giống response Login (1.1)

> FE gọi khi `accessToken` hết hạn (status 401). Cập nhật lại `accessToken` mới.

---

### 1.4 Lấy danh sách trang được phép

```
POST /api/v1/auth/allowed-pages
```

**Request Body:**

```json
{
  "roles": ["ADMIN", "MERCHANT"]
}
```

**Response:**

```json
{
  "data": {
    "allowedPages": ["dashboard", "reports", "settings"]
  }
}
```

> FE dùng để ẩn/hiện menu sidebar theo role.

---

## 2. PROFILE

### 2.1 Lấy thông tin profile

```
GET /api/v1/profile
```

**Response:**

```json
{
  "data": {
    "id": "uuid",
    "username": "string",
    "email": "string",
    "firstName": "string",
    "lastName": "string",
    "storeName": "string | null",
    "phone": "string | null",
    "category": "FOOD | null",
    "balance": 0.00,
    "tier": "BRONZE | null",
    "point": 0
  }
}
```

> `storeName`, `phone`, `category` → chỉ có khi role = MERCHANT/PARTNER
> `balance`, `tier`, `point` → chỉ có khi role = CUSTOMER

---

### 2.2 Cập nhật profile

```
PUT /api/v1/profile
```

**Request Body:** (chỉ gửi field cần cập nhật)

```json
{
  "firstName": "string",
  "lastName": "string",
  "email": "string",
  "phone": "string",
  "storeName": "string"
}
```

**Response:** `data: null`

---

### 2.3 Đổi mật khẩu

```
PUT /api/v1/profile/password
```

**Request Body:**

```json
{
  "currentPassword": "string",
  "newPassword": "string"
}
```

**Response:** `data: null`

---

## 3. ROLES

### 3.1 Lấy tất cả roles

```
GET /api/v1/roles
```

**Response:**

```json
{
  "data": [
    {
      "id": "uuid",
      "name": "ADMIN",
      "description": "Administrator role",
      "attributes": {
        "allowed-pages": ["dashboard", "settings"]
      }
    }
  ]
}
```

---

### 3.2 Tạo role

```
POST /api/v1/roles
```

**Request Body:**

```json
{
  "name": "string",
  "description": "string",
  "attributes": {
    "allowedPages": ["dashboard", "reports"]
  }
}
```

**Response:** `data: null`

---

### 3.3 Lấy role theo tên

```
GET /api/v1/roles/{roleName}
```

**Response:** Giống 1 item trong 3.1

---

### 3.4 Cập nhật role

```
PUT /api/v1/roles/{roleName}
```

**Request Body:**

```json
{
  "description": "string"
}
```

**Response:** `data: null`

---

### 3.5 Xóa role

```
DELETE /api/v1/roles/{roleName}
```

**Response:** `data: null`

---

### 3.6 Cập nhật attributes của role

```
PUT /api/v1/roles/{roleName}/attributes
```

**Request Body:**

```json
{
  "allowedPages": ["dashboard", "reports", "settings"]
}
```

**Response:** `data: null`

---

## 4. SYSTEM USERS

### 4.1 Lấy tất cả users

```
GET /api/v1/system-users
```

**Response:**

```json
{
  "data": [
    {
      "id": "uuid",
      "username": "string",
      "email": "string",
      "firstName": "string",
      "lastName": "string",
      "enabled": true,
      "storeName": "string | null",
      "phone": "string | null",
      "category": "FOOD | null",
      "status": "ACTIVE | null",
      "balance": 0.00,
      "tier": "BRONZE | null",
      "point": 0
    }
  ]
}
```

---

### 4.2 Tạo user

```
POST /api/v1/system-users
```

**Request Body (Admin/Maker/Checker):**

```json
{
  "username": "string",
  "password": "string",
  "email": "string",
  "firstName": "string",
  "lastName": "string",
  "enabled": true,
  "role": "ADMIN"
}
```

**Request Body (Merchant):**

```json
{
  "username": "string",
  "password": "string",
  "email": "string",
  "firstName": "string",
  "lastName": "string",
  "enabled": true,
  "role": "MERCHANT",
  "storeName": "string",
  "phone": "string",
  "category": "FOOD"
}
```

**Response:**

```json
{
  "data": {
    "id": "uuid"
  }
}
```

---

### 4.3 Cập nhật user

```
PUT /api/v1/system-users/{id}
```

**Request Body:** (chỉ gửi field cần cập nhật)

```json
{
  "email": "string",
  "firstName": "string",
  "lastName": "string",
  "enabled": true,
  "storeName": "string",
  "phone": "string",
  "category": "BEVERAGE",
  "status": "ACTIVE"
}
```

**Response:** `data: null`

---

### 4.4 Xóa user

```
DELETE /api/v1/system-users/{id}
```

**Response:** `data: null`

---

### 4.5 Reset mật khẩu

```
POST /api/v1/system-users/{id}/reset-password
```

**Request Body:**

```json
{
  "password": "string"
}
```

**Response:** `data: null`

---

# VOUCHER SERVICE

## 5. VOUCHERS

### 5.1 Tạo voucher

> Role: `MAKER`, `PARTNER`

```
POST /api/v1/vouchers
```

**Request Body:**

```json
{
  "voucherName": "Summer Sale 50K",
  "description": "Discount 50K for orders over 200K",
  "voucherPurpose": "HUNT",
  "customerTier": "ALL",
  "discountType": "FIXED",
  "discountValue": 50000,
  "maxDiscount": 50000,
  "minOrderValue": 200000,
  "totalStock": 100,
  "maxCollect": 1,
  "startDate": "2026-05-01T00:00:00",
  "endDate": "2026-06-30T23:59:59"
}
```

| Field | Type | Required | Note |
|---|---|---|---|
| voucherName | string | ✅ | |
| description | string | ✅ | |
| voucherPurpose | enum | SYSTEM only | `REWARD`, `HUNT` |
| customerTier | enum | SYSTEM only | `ALL`, `BRONZE`, `SILVER`, `GOLD`, `PLATINUM`, `DIAMOND` |
| discountType | enum | ✅ | `FIXED`, `PERCENT` |
| discountValue | number | ✅ | |
| maxDiscount | number | | Bắt buộc khi `PERCENT` |
| minOrderValue | number | | Bắt buộc khi `FIXED` |
| totalStock | integer | ✅ | |
| maxCollect | integer | | |
| startDate | datetime | ✅ | ISO format |
| endDate | datetime | ✅ | Phải ở tương lai |

**Response:** `data: null`

---

### 5.2 Tạo voucher bằng Excel

> Role: `MAKER`, `PARTNER`

```
POST /api/v1/vouchers/excel
Content-Type: multipart/form-data
```

| Field | Type | Required |
|---|---|---|
| file | file (.xlsx) | ✅ |
| voucherPurpose | string | |

**Response:** `data: null`

---

### 5.3 Lấy danh sách voucher requests

> Role: `MAKER`, `CHECKER`, `PARTNER`

```
GET /api/v1/vouchers
```

**Query Parameters:**

| Param | Type | Required | Note |
|---|---|---|---|
| status | enum | | `INIT`, `PENDING_APPROVE`, `APPROVED`, `REJECTED`, ... |
| requestMode | enum | | `SINGLE`, `EXCEL` |
| creatorType | enum | | `PARTNER`, `SYSTEM` |
| voucherPurpose | enum | | `REWARD`, `HUNT` |
| storeName | string | | |
| fromDate | datetime | | ISO format |
| toDate | datetime | | ISO format |
| page | integer | | default: 0 |
| size | integer | | default: 20 |

**Response:**

```json
{
  "data": {
    "content": [
      {
        "id": 1,
        "requestId": "VOUCHER_1234567890",
        "requestMode": "SINGLE",
        "creatorType": "SYSTEM",
        "voucherPurpose": "HUNT",
        "fileName": null,
        "status": "INIT",
        "reason": null,
        "totalVoucher": 5,
        "statusCounts": [
          { "requestStatus": "INIT", "count": 3 },
          { "requestStatus": "APPROVED", "count": 2 }
        ],
        "createdTime": "2026-05-01T10:00:00",
        "createdBy": "admin",
        "updatedTime": "2026-05-01T10:00:00",
        "updatedBy": "admin",
        "confirmedTime": null,
        "confirmedBy": null,
        "storeName": "Coffee Shop"
      }
    ],
    "totalElements": 50,
    "totalPages": 3,
    "page": 0,
    "size": 20
  }
}
```

---

### 5.4 Lấy danh sách voucher details

> Role: `MAKER`, `CHECKER`, `PARTNER`

```
GET /api/v1/vouchers/details
```

**Query Parameters:**

| Param | Type | Required |
|---|---|---|
| creatorType | enum | |
| customerTier | enum | |
| discountType | enum | |
| voucherPurpose | enum | |
| voucherStatus | enum | `ACTIVE`, `INACTIVE`, `EXPIRED` |
| storeName | string | |
| fromDate | datetime | |
| toDate | datetime | |
| page | integer | |
| size | integer | |

**Response:**

```json
{
  "data": {
    "content": [
      {
        "id": 1,
        "voucherCode": "VC-ABC123",
        "requestId": "VOUCHER_1234567890",
        "voucherName": "Summer Sale 50K",
        "description": "Discount 50K",
        "customerTier": "ALL",
        "discountType": "FIXED",
        "discountValue": 50000,
        "maxDiscount": 50000,
        "minOrderValue": 200000,
        "totalStock": 100,
        "availableStock": 95,
        "requestStatus": "APPROVED",
        "maxCollect": 1,
        "startDate": "2026-05-01T00:00:00",
        "endDate": "2026-06-30T23:59:59",
        "status": "ACTIVE",
        "errorMessage": null,
        "createdAt": "2026-05-01T10:00:00"
      }
    ],
    "totalElements": 100,
    "totalPages": 5,
    "page": 0,
    "size": 20
  }
}
```

---

### 5.5 Lấy chi tiết voucher request theo ID

> Role: `MAKER`, `CHECKER`, `PARTNER`

```
GET /api/v1/vouchers/{id}
```

**Query Parameters:**

| Param | Type | Required |
|---|---|---|
| voucherName | string | |
| status | enum | |
| page | integer | |
| size | integer | |

**Response:**

```json
{
  "data": {
    "id": 1,
    "requestId": "VOUCHER_1234567890",
    "requestMode": "SINGLE",
    "creatorType": "SYSTEM",
    "voucherPurpose": "HUNT",
    "status": "INIT",
    "voucherDetailResponses": [
      {
        "id": 1,
        "voucherCode": "VC-ABC123",
        "voucherName": "Summer Sale 50K",
        "discountType": "FIXED",
        "discountValue": 50000,
        "status": "ACTIVE"
      }
    ],
    "totalElements": 5,
    "totalPages": 1,
    "page": 0,
    "size": 20
  }
}
```

---

### 5.6 Submit voucher (gửi duyệt)

> Role: `MAKER`, `PARTNER`

```
PUT /api/v1/vouchers/{id}/submit
```

**Response:** `data: null`

> Chuyển status từ `INIT` → `PENDING_APPROVE`

---

### 5.7 Confirm voucher (duyệt/từ chối)

> Role: `CHECKER`

```
PUT /api/v1/vouchers/{id}/confirm
```

**Request Body:**

```json
{
  "action": "APPROVED"
}
```

Hoặc từ chối:

```json
{
  "action": "REJECTED",
  "reason": "Discount value too high"
}
```

| Field | Type | Required | Note |
|---|---|---|---|
| action | enum | ✅ | `APPROVED`, `REJECTED` |
| reason | string | Khi REJECTED | Lý do từ chối |

**Response:** `data: null`

---

### 5.8 Cancel voucher

> Role: `MAKER`, `PARTNER`

```
PUT /api/v1/vouchers/{id}/cancel
```

**Response:** `data: null`

> Chỉ cancel được khi status = `INIT`

---

## 6. MISSIONS

### 6.1 Tạo mission

> Role: `MAKER`, `PARTNER`

```
POST /api/v1/missions/missions
```

**Request Body:**

```json
{
  "missionName": "Spend 500K",
  "missionDescription": "Complete orders totaling 500K to earn reward",
  "targetValue": 500000,
  "rewardType": "POINT",
  "rewardValue": "100",
  "partnerId": null,
  "missionStartDate": "2026-05-01T00:00:00",
  "missionEndDate": "2026-06-30T23:59:59",
  "voucherName": "Mission Voucher",
  "description": "Voucher from mission",
  "discountType": "FIXED",
  "discountValue": 30000,
  "maxDiscount": 30000,
  "minOrderValue": 100000,
  "totalStock": 50,
  "maxCollect": 1,
  "startDate": "2026-05-01T00:00:00",
  "endDate": "2026-06-30T23:59:59"
}
```

| Field | Type | Required | Note |
|---|---|---|---|
| missionName | string | ✅ | |
| missionDescription | string | ✅ | |
| targetValue | number | ✅ | Phải > 0 |
| rewardType | enum | ✅ | `POINT`, `VOUCHER` |
| rewardValue | string | ✅ | Số point hoặc voucher campaign ID |
| partnerId | long | | |
| missionStartDate | datetime | ✅ | Không được ở quá khứ |
| missionEndDate | datetime | ✅ | Phải sau startDate |
| + các field voucher | | | Kế thừa từ CreateVoucherRequest |

**Response:** `data: null`

---

### 6.2 Lấy chi tiết mission

> Role: `MAKER`, `CHECKER`, `PARTNER`

```
GET /api/v1/missions/missions/{id}
```

**Response:**

```json
{
  "data": {
    "mission": {
      "missionId": 1,
      "requestId": "VOUCHER_1234567890",
      "missionName": "Spend 500K",
      "missionDescription": "Complete orders totaling 500K",
      "targetValue": 500000,
      "rewardType": "POINT",
      "rewardValue": "100",
      "partnerId": null,
      "startDate": "2026-05-01T00:00:00",
      "endDate": "2026-06-30T23:59:59",
      "status": "INIT",
      "createdDate": null,
      "updatedDate": null
    },
    "voucherDetail": {
      "id": 1,
      "voucherCode": "VC-ABC123",
      "requestId": "VOUCHER_1234567890",
      "voucherName": "Mission Voucher",
      "description": "Voucher from mission",
      "customerTier": "ALL",
      "discountType": "FIXED",
      "discountValue": 30000,
      "maxDiscount": 30000,
      "minOrderValue": 100000,
      "totalStock": 50,
      "availableStock": 50,
      "requestStatus": "INIT",
      "maxCollect": 1,
      "startDate": "2026-05-01T00:00:00",
      "endDate": "2026-06-30T23:59:59",
      "status": "INACTIVE",
      "errorMessage": null,
      "createdAt": "2026-05-01T10:00:00"
    }
  }
}
```

> `voucherDetail` có thể `null` nếu không tìm thấy voucher theo requestId.

---

### 6.3 Tìm kiếm missions

> Role: `MAKER`, `CHECKER`, `PARTNER`

```
GET /api/v1/missions/search
```

**Query Parameters:**

| Param | Type | Required | Note |
|---|---|---|---|
| nameStore | string | | Tên cửa hàng |
| rewardType | enum | | `POINT`, `VOUCHER` |
| taskStatus | enum | | `INIT`, `PENDING_APPROVE`, `APPROVED`, ... |
| page | integer | | default: 0 |
| size | integer | | default: 20 |

**Response:**

```json
{
  "data": {
    "data": [
      {
        "missionId": 1,
        "requestId": "VOUCHER_1234567890",
        "missionName": "Spend 500K",
        "missionDescription": "Complete orders totaling 500K",
        "targetValue": 500000,
        "rewardType": "POINT",
        "rewardValue": "100",
        "partnerId": null,
        "startDate": "2026-05-01T00:00:00",
        "endDate": "2026-06-30T23:59:59",
        "status": "INIT"
      }
    ],
    "totalElements": 10,
    "totalPages": 1,
    "page": 0,
    "size": 20
  }
}
```

> CHECKER chỉ thấy: `PENDING_APPROVE`, `APPROVED`, `FAILED`, `FINISH`, `REJECTED`
> PARTNER chỉ thấy missions của store mình.

---

### 6.4 Submit mission

> Role: `MAKER`, `PARTNER`

```
PUT /api/v1/missions/missions/{id}/submit
```

**Response:** `data: null`

---

### 6.5 Confirm mission

> Role: `CHECKER`

```
PUT /api/v1/missions/missions/{id}/confirm
```

**Request Body:**

```json
{
  "action": "APPROVED"
}
```

Hoặc:

```json
{
  "action": "REJECTED",
  "reason": "Target value too low"
}
```

**Response:** `data: null`

---

### 6.6 Cancel mission

> Role: `MAKER`, `PARTNER`

```
PUT /api/v1/missions/missions/{id}/cancel
```

**Response:** `data: null`

---

## 7. AUDIT LOGS

> Role: `ADMIN`, `CHECKER`

### 7.1 Lấy audit logs

```
GET /api/v1/audit-logs
```

**Query Parameters:**

| Param | Type | Required |
|---|---|---|
| userRole | string | |
| userId | string | |
| fromDate | datetime | |
| toDate | datetime | |
| page | integer | |
| size | integer | |

**Response:**

```json
{
  "data": {
    "content": [
      {
        "id": 1,
        "userId": "uuid",
        "userRole": "MAKER",
        "action": "CREATE_VOUCHER",
        "detail": "Created voucher request VOUCHER_123",
        "createdAt": "2026-05-01T10:00:00"
      }
    ],
    "totalElements": 100,
    "totalPages": 5,
    "number": 0,
    "size": 20
  }
}
```

---

## Flow nghiệp vụ cho FE

### Voucher Flow

```
Tạo voucher (INIT) → Submit (PENDING_APPROVE) → Confirm APPROVED/REJECTED
                    → Cancel (CANCELLED)
```

### Mission Flow

```
Tạo mission (INIT) → Submit (PENDING_APPROVE) → Confirm APPROVED/REJECTED
                    → Cancel (CANCELLED)
```

### Auth Flow

```
Login → Lưu accessToken + refreshToken
      → Gọi API với Bearer token
      → Khi 401 → Gọi Refresh Token → Cập nhật accessToken
      → Khi refresh cũng 401 → Redirect về Login
```
