# Mock Invoice API - Documentation cho Frontend

> **Base URL:** `http://localhost:8082`
>
> **Authentication:** Tất cả API yêu cầu header `Authorization: Bearer <accessToken>`
>
> **Role yêu cầu:** `MERCHANT` hoặc `ADMIN` (tùy API)

---

## Response Format chung

```json
{
  "status": 0,
  "code": "success",
  "message": "Success",
  "data": { ... }
}
```

---

## 1. Tạo hóa đơn mẫu

> Role: `MERCHANT`

```
POST /api/invoices
```

**Request Body:**
```json
{
  "title": "Cà phê sáng",
  "nameStore": "Coffee House",
  "amount": 150000.00
}
```

| Field     | Type       | Bắt buộc | Validation              |
|-----------|------------|----------|--------------------------|
| title     | String     | ✅       | Không được trống         |
| nameStore | String     | ✅       | Không được trống         |
| amount    | BigDecimal | ✅       | Phải lớn hơn 0          |

**Response (200):**
```json
{
  "status": 0,
  "code": "success",
  "message": "Invoice created successfully",
  "data": {
    "id": 1,
    "title": "Cà phê sáng",
    "nameStore": "Coffee House",
    "amount": 150000.00,
    "createdAt": "2024-03-15T10:30:00",
    "updatedAt": "2024-03-15T10:30:00"
  }
}
```

---

## 2. Danh sách hóa đơn

> Role: `MERCHANT` hoặc `ADMIN`

```
GET /api/invoices
```

**Query Params:**

| Param     | Type   | Bắt buộc | Default        | Mô tả                              |
|-----------|--------|----------|----------------|-------------------------------------|
| nameStore | String | ❌       |                | Lọc theo tên cửa hàng              |
| title     | String | ❌       |                | Lọc theo tiêu đề                   |
| page      | int    | ❌       | 0              | Số trang (bắt đầu từ 0)            |
| size      | int    | ❌       | 20             | Số item/trang                       |
| sort      | String | ❌       | createdAt,desc | Sắp xếp: `field,asc` hoặc `field,desc` |

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
        "title": "Cà phê sáng",
        "nameStore": "Coffee House",
        "amount": 150000.00,
        "createdAt": "2024-03-15T10:30:00",
        "updatedAt": "2024-03-15T10:30:00"
      },
      {
        "id": 2,
        "title": "Trà chiều",
        "nameStore": "Coffee House",
        "amount": 85000.00,
        "createdAt": "2024-03-14T15:00:00",
        "updatedAt": "2024-03-14T15:00:00"
      }
    ],
    "totalElements": 25,
    "totalPages": 2,
    "currentPage": 0,
    "pageSize": 20
  }
}
```

---

## 3. Chi tiết hóa đơn

> Role: `MERCHANT` hoặc `ADMIN`

```
GET /api/invoices/{id}
```

| Param | Vị trí | Type | Bắt buộc | Mô tả      |
|-------|--------|------|----------|-------------|
| id    | path   | Long | ✅       | ID hóa đơn |

**Response (200):**
```json
{
  "status": 0,
  "code": "success",
  "message": "Success",
  "data": {
    "id": 1,
    "title": "Cà phê sáng",
    "nameStore": "Coffee House",
    "amount": 150000.00,
    "createdAt": "2024-03-15T10:30:00",
    "updatedAt": "2024-03-15T10:30:00"
  }
}
```

**Lỗi (404):**
```json
{
  "status": 1,
  "code": "NOT_FOUND",
  "message": "Invoice not found",
  "data": null
}
```

---

## Flow cho Frontend

### Merchant tạo hóa đơn:
```
1. Merchant đăng nhập → lấy accessToken
2. POST /api/invoices → tạo hóa đơn mẫu
3. GET /api/invoices → xem danh sách hóa đơn đã tạo
```

### Customer thanh toán (customer-service):
```
1. Customer gọi GET /api/customers/invoices (customer-service:8084) → xem danh sách hóa đơn
2. Chọn hóa đơn → POST /api/v1/payments/process (customer-service:8084) → thanh toán
```

> **Lưu ý:** Customer xem hóa đơn qua customer-service (port 8084), không gọi trực tiếp voucher-service.
> Merchant quản lý hóa đơn qua voucher-service (port 8082).
