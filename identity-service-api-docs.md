# Identity Service - API Documentation

> Base URL: `http://localhost:8081`

---

## Response chung

Tất cả API đều trả về cấu trúc:

```json
{
  "status": 0,        // 0 = success, 1 = error
  "code": "success",  // "success" | "error"
  "message": "...",
  "data": { ... }     // null nếu không có data
}
```

### Error Response

```json
{
  "status": 1,
  "code": "error",
  "message": "Mô tả lỗi",
  "data": null
}
```

| HTTP Status | Ý nghĩa |
|---|---|
| 401 | Chưa xác thực hoặc token không hợp lệ |
| 403 | Không có quyền truy cập tài nguyên này |
| 404 | Không tìm thấy tài nguyên |
| 400 | Request không hợp lệ |
| 500 | Lỗi server |

---

## Enum Values

| Enum | Values |
|---|---|
| Role | `ADMIN`, `MERCHANT`, `CUSTOMER`, `MAKER`, `CHECKER` |
| MerchantCategory | `FOOD`, `BEVERAGE`, `FASHION`, `ELECTRONICS`, `BEAUTY`, `HEALTH`, `EDUCATION`, `ENTERTAINMENT`, `TRAVEL`, `OTHER` |
| MerchantStatus | `ACTIVE`, `LOCKED` |
| CustomerTier | `BRONZE`, `SILVER`, `GOLD`, `PLATINUM`, `DIAMOND` |

---

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

**Response (200):**

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
POST /api/v1/auth/refresh
```

**Request Body:**

```json
{
  "refreshToken": "eyJhbGciOiJIUzUxMiIs..."
}
```

**Response (200):**

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

**Response (200):**

```json
{
  "status": 0,
  "code": "success",
  "message": "Success",
  "data": {
    "allowedPages": ["dashboard", "reports", "settings"]
  }
}
```

---

## 2. PROFILE (Yêu cầu Bearer Token)

> Header: `Authorization: Bearer <accessToken>`

### 2.1 Lấy thông tin profile

```
GET /api/v1/profile
```

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
    "storeName": "string | null",
    "phone": "string | null",
    "category": "FOOD | null",
    "balance": 0.00,
    "tier": "BRONZE | null",
    "point": 0
  }
}
```

> Các field `storeName`, `phone`, `category` chỉ có khi user là MERCHANT.
> Các field `balance`, `tier`, `point` chỉ có khi user là CUSTOMER.

---

### 2.2 Cập nhật profile

```
PUT /api/v1/profile
```

**Request Body:**

```json
{
  "firstName": "string",
  "lastName": "string",
  "email": "string",
  "phone": "string",
  "storeName": "string"
}
```

> Chỉ gửi các field cần cập nhật. `phone`, `storeName` chỉ áp dụng cho MERCHANT.

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

## 3. ROLES (Yêu cầu Bearer Token)

> Header: `Authorization: Bearer <accessToken>`

### 3.1 Lấy tất cả roles

```
GET /api/v1/roles
```

**Response (200):**

```json
{
  "status": 0,
  "code": "success",
  "message": "Success",
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

**Response (201):**

```json
{
  "status": 0,
  "code": "success",
  "message": "Success",
  "data": null
}
```

---

### 3.3 Lấy role theo tên

```
GET /api/v1/roles/{roleName}
```

**Response (200):**

```json
{
  "status": 0,
  "code": "success",
  "message": "Success",
  "data": {
    "id": "uuid",
    "name": "ADMIN",
    "description": "Administrator role",
    "attributes": {
      "allowed-pages": ["dashboard", "settings"]
    }
  }
}
```

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

### 3.5 Xóa role

```
DELETE /api/v1/roles/{roleName}
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

## 4. SYSTEM USERS (Yêu cầu Bearer Token)

> Header: `Authorization: Bearer <accessToken>`

### 4.1 Lấy tất cả users

```
GET /api/v1/system-users
```

**Response (200):**

```json
{
  "status": 0,
  "code": "success",
  "message": "Success",
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

### 4.2 Tạo user (ADMIN, MERCHANT, MAKER, CHECKER)

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

> `role` chấp nhận: `ADMIN`, `MERCHANT`, `MAKER`, `CHECKER` (CUSTOMER đăng ký qua `/api/v1/auth/register`)

**Response (201):**

```json
{
  "status": 0,
  "code": "success",
  "message": "Tạo user thành công",
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

**Request Body:**

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

> Chỉ gửi các field cần cập nhật. `storeName`, `phone`, `category`, `status` chỉ áp dụng cho MERCHANT.

**Response (200):**

```json
{
  "status": 0,
  "code": "success",
  "message": "Cập nhật user thành công",
  "data": null
}
```

---

### 4.4 Xóa user

```
DELETE /api/v1/system-users/{id}
```

**Response (200):**

```json
{
  "status": 0,
  "code": "success",
  "message": "Xóa user thành công",
  "data": null
}
```

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

**Response (200):**

```json
{
  "status": 0,
  "code": "success",
  "message": "Reset mật khẩu thành công",
  "data": null
}
```
