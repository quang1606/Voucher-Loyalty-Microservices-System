# Customer Service & Identity Service - API Documentation

## Giới thiệu

### Tổng quan hệ thống

Hệ thống **Voucher & Loyalty Platform** là một nền tảng microservices phục vụ chương trình khách hàng thân thiết, bao gồm các chức năng chính:

- **Quản lý tài khoản:** Đăng ký, đăng nhập, quản lý thông tin cá nhân khách hàng
- **Voucher:** Thu thập, quản lý và sử dụng voucher giảm giá từ hệ thống hoặc đối tác
- **Nhiệm vụ (Missions):** Hoàn thành nhiệm vụ chi tiêu/mua hàng để nhận thưởng điểm hoặc voucher
- **Tích điểm Loyalty:** Tự động tích điểm khi thanh toán, nâng hạng thành viên
- **Bảng xếp hạng:** Xếp hạng khách hàng theo tổng điểm tích lũy
- **Thanh toán:** Xử lý thanh toán hóa đơn kết hợp áp dụng voucher

### Kiến trúc

Hệ thống gồm các service chính:

| Service            | Port | Mô tả                                      |
|--------------------|------|---------------------------------------------|
| API Gateway (Kong) | 8000 | Điểm vào duy nhất, routing & CORS          |
| Identity Service   | 8081 | Xác thực, phân quyền (Keycloak)            |
| Customer Service   | 8084 | Quản lý khách hàng, voucher, mission, payment |
| Voucher Service    | 8082 | Quản lý voucher gốc, mission gốc           |
| Loyalty Service    | 8083 | Tích điểm, nâng hạng                       |

### Routing qua API Gateway

| Prefix          | Service          |
|-----------------|------------------|
| `/api/identity` | Identity Service |
| `/api/customers`| Customer Service |
| `/api/vouchers` | Voucher Service  |

### Xác thực & Phân quyền

- Sử dụng **OAuth2 / Keycloak** với JWT token
- Sau khi đăng nhập, client nhận `accessToken` (Bearer) và `refreshToken`
- Gắn header `Authorization: Bearer <accessToken>` cho mọi request cần xác thực
- Khi `accessToken` hết hạn → gọi API refresh để lấy token mới
- Role `CUSTOMER` được gán tự động khi đăng ký qua API register

### Đối tượng sử dụng tài liệu

Tài liệu này dành cho **Frontend Developer** để tích hợp giao diện người dùng (Customer App) với backend, bao gồm:
- Tất cả endpoint cần thiết cho luồng người dùng cuối (end-user)
- Chi tiết request/response format
- Mã lỗi và cách xử lý
- Flow tích hợp gợi ý

---

## Thông tin kết nối

> **Base URL qua Gateway:** `http://localhost:8000`
>
> **Identity Service trực tiếp:** `http://localhost:8081`
>
> **Customer Service trực tiếp:** `http://localhost:8084`
>
> **Authentication:** Header `Authorization: Bearer <accessToken>` (lấy từ Identity Service login)
>
> **Role yêu cầu:** `CUSTOMER` (trừ khi ghi chú khác)

---

## Response Format chung

**Thành công:**
```json
{
  "status": 0,
  "code": "success",
  "message": "Success",
  "data": { ... }
}
```

**Lỗi:**
```json
{
  "status": 1,
  "code": "ERROR_CODE",
  "message": "Mô tả lỗi",
  "data": null
}
```

---

## Enum Values

| Enum                  | Values                               |
|-----------------------|--------------------------------------|
| CustomerTier          | ALL, SILVER, GOLD, PLATINUM, DIAMOND |
| CustomerStatus        | ACTIVE, LOCKED                       |
| CustomerVoucherStatus | AVAILABLE, USED, EXPIRED             |
| CustomerMissionStatus | IN_PROGRESS, COMPLETED, CLAIMED      |
| DiscountType          | FIXED, PERCENT                       |
| CreatorType           | SYSTEM, PARTNER                      |
| TargetType            | AMOUNT, COUNT                        |
| RewardType            | POINT, VOUCHER                       |

---

## 1. Customer Profile

### 1.1 Lấy thông tin khách hàng

```
GET /api/customers/profile/{customerId}
```

| Param      | Vị trí | Type | Bắt buộc | Mô tả              |
|------------|--------|------|----------|---------------------|
| customerId | path   | UUID | ✅       | ID user của customer |

**Response:**
```json
{
  "status": 0,
  "code": "success",
  "message": "Success",
  "data": {
    "id": 1,
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "fullName": "Nguyen Van A",
    "balance": 500000.00,
    "totalPoints": 1200,
    "tier": "SILVER",
    "status": "ACTIVE",
    "createdAt": "2024-01-15T10:30:00"
  }
}
```

**Lỗi có thể gặp:**
| Code               | Mô tả                    |
|--------------------|--------------------------|
| CUSTOMER_NOT_FOUND | Không tìm thấy customer  |

---

## 2. Vouchers

### 2.1 Danh sách voucher khả dụng (kèm trạng thái đã thu thập)

> Hiển thị tất cả voucher mà customer có thể collect, dựa trên tier của customer.

```
GET /api/customers/vouchers/available/with-status
```

| Param      | Vị trí | Type | Bắt buộc | Default | Mô tả                |
|------------|--------|------|----------|---------|----------------------|
| customerId | query  | Long | ✅       |         | ID customer (số)     |
| page       | query  | int  | ❌       | 0       | Số trang (bắt đầu 0)|
| size       | query  | int  | ❌       | 20      | Số item/trang        |

**Response:**
```json
{
  "status": 0,
  "code": "success",
  "message": "Success",
  "data": {
    "data": [
      {
        "id": 1,
        "voucherCode": "VC-ABC123",
        "voucherName": "Giảm 50k đơn từ 200k",
        "description": "Áp dụng cho tất cả cửa hàng",
        "customerTier": "ALL",
        "discountType": "FIXED",
        "discountValue": "50000",
        "maxDiscount": "50000",
        "minOrderValue": "200000",
        "totalStock": 100,
        "availableStock": 45,
        "maxCollect": 2,
        "startDate": 1700000000000,
        "endDate": 1710000000000,
        "status": "ACTIVE",
        "createdAt": 1700000000000,
        "collected": false
      }
    ],
    "totalElements": 50,
    "totalPages": 3
  }
}
```

**Giải thích fields:**
| Field          | Mô tả                                                    |
|----------------|----------------------------------------------------------|
| collected      | `true` = customer đã collect voucher này, `false` = chưa |
| discountType   | `FIXED` = giảm cố định, `PERCENT` = giảm %              |
| maxDiscount    | Giảm tối đa (chỉ có ý nghĩa khi discountType = PERCENT) |
| minOrderValue  | Giá trị đơn hàng tối thiểu để áp dụng                   |
| maxCollect     | Số lần sử dụng tối đa khi collect                        |
| startDate/endDate | Epoch milliseconds                                    |

---

### 2.2 Thu thập voucher

> Customer collect voucher vào kho của mình.

```
POST /api/customers/vouchers/collect/{voucherId}
```

| Param      | Vị trí | Type | Bắt buộc | Mô tả        |
|------------|--------|------|----------|---------------|
| voucherId  | path   | Long | ✅       | ID voucher    |
| customerId | query  | Long | ✅       | ID customer   |

**Request Body:** Không có

**Response:**
```json
{
  "status": 0,
  "code": "success",
  "message": "Voucher collected successfully",
  "data": "Voucher has been added to your collection"
}
```

**Lỗi có thể gặp:**
| Code                     | Mô tả                          |
|--------------------------|--------------------------------|
| VOUCHER_ALREADY_COLLECTED| Đã collect voucher này rồi     |
| VOUCHER_OUT_OF_STOCK     | Voucher đã hết                 |

---

### 2.3 Danh sách voucher đã thu thập của customer

> Hiển thị kho voucher của customer, có thể filter theo trạng thái.

```
GET /api/customers/vouchers/list
```

| Param      | Vị trí | Type   | Bắt buộc | Default         | Mô tả                              |
|------------|--------|--------|----------|-----------------|-------------------------------------|
| customerId | query  | Long   | ❌       |                 | Filter theo customer                |
| voucherId  | query  | Long   | ❌       |                 | Filter theo voucher                 |
| status     | query  | String | ❌       |                 | AVAILABLE, USED, EXPIRED            |
| page       | query  | int    | ❌       | 0               | Số trang                            |
| size       | query  | int    | ❌       | 20              | Số item/trang                       |
| sort       | query  | String | ❌       | obtainedAt,desc | Sắp xếp: `field,asc` hoặc `field,desc` |

**Response:**
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
        "availableUsage": 2,
        "voucherCode": "VC-ABC123",
        "nameStore": "Coffee House",
        "creatorType": "PARTNER",
        "status": "AVAILABLE",
        "obtainedAt": "2024-03-15T14:30:00",
        "usedAt": null,
        "expiredAt": "2024-12-31T23:59:59",
        "isCollected": true
      }
    ],
    "totalElements": 8,
    "totalPages": 1,
    "currentPage": 0,
    "pageSize": 20
  }
}
```

**Giải thích fields:**
| Field          | Mô tả                                              |
|----------------|-----------------------------------------------------|
| availableUsage | Số lần còn có thể sử dụng                          |
| creatorType    | `SYSTEM` = hệ thống tạo, `PARTNER` = đối tác tạo  |
| status         | `AVAILABLE` = dùng được, `USED` = đã dùng, `EXPIRED` = hết hạn |
| nameStore      | Tên cửa hàng (null nếu voucher hệ thống)           |

---

### 2.4 Lấy voucher áp dụng được cho đơn hàng

> Khi customer chuẩn bị thanh toán, gọi API này để hiển thị danh sách voucher có thể áp dụng.

```
GET /api/customers/vouchers/applicable
```

| Param       | Vị trí | Type       | Bắt buộc | Mô tả                    |
|-------------|--------|------------|----------|---------------------------|
| customerId  | query  | Long       | ✅       | ID customer               |
| nameStore   | query  | String     | ✅       | Tên cửa hàng đang mua    |
| orderAmount | query  | BigDecimal | ✅       | Tổng tiền đơn hàng       |

**Response:**
```json
{
  "status": 0,
  "code": "success",
  "message": "Success",
  "data": {
    "data": [
      {
        "voucherId": 10,
        "voucherCode": "VC-ABC123",
        "voucherName": "Giảm 30%",
        "description": "Giảm 30% tối đa 100k",
        "discountType": "PERCENT",
        "discountValue": "30",
        "maxDiscount": "100000",
        "minOrderValue": "150000",
        "availableStock": 20,
        "nameStore": "Coffee House",
        "creatorType": "PARTNER",
        "applicable": true,
        "reason": null
      },
      {
        "voucherId": 11,
        "voucherCode": "VC-DEF456",
        "voucherName": "Giảm 50k",
        "description": "Giảm 50k cho đơn từ 300k",
        "discountType": "FIXED",
        "discountValue": "50000",
        "maxDiscount": "50000",
        "minOrderValue": "300000",
        "availableStock": 10,
        "nameStore": "",
        "creatorType": "SYSTEM",
        "applicable": false,
        "reason": "Đơn hàng tối thiểu 300000"
      }
    ],
    "totalElements": 2
  }
}
```

**Giải thích:**
- `applicable = true`: Voucher có thể áp dụng cho đơn hàng này
- `applicable = false`: Không áp dụng được, xem `reason` để biết lý do
- Danh sách đã sort: voucher áp dụng được lên trước

---

## 3. Missions (Nhiệm vụ)

### 3.1 Danh sách nhiệm vụ

> Hiển thị tất cả nhiệm vụ đang active kèm tiến độ của customer.

```
GET /api/customers/missions
```

| Param | Vị trí | Type | Bắt buộc | Default | Mô tả         |
|-------|--------|------|----------|---------|----------------|
| page  | query  | int  | ❌       | 0       | Số trang       |
| size  | query  | int  | ❌       | 20      | Số item/trang  |

**Response:**
```json
{
  "status": 0,
  "code": "success",
  "message": "Success",
  "data": {
    "missions": [
      {
        "missionId": 1,
        "missionName": "Chi tiêu 500k trong tuần",
        "missionDescription": "Tổng chi tiêu đạt 500.000đ để nhận 100 điểm",
        "targetValue": 500000.0,
        "targetType": "AMOUNT",
        "rewardType": "POINT",
        "rewardValue": "100",
        "partnerId": 0,
        "startDate": 1700000000000,
        "endDate": 1710000000000,
        "taskStatus": "FINISH",
        "currentProgress": 250000,
        "status": "IN_PROGRESS",
        "voucherRequest": null
      },
      {
        "missionId": 2,
        "missionName": "Mua 3 lần tại Coffee House",
        "missionDescription": "Mua 3 lần để nhận voucher giảm 100k",
        "targetValue": 3.0,
        "targetType": "COUNT",
        "rewardType": "VOUCHER",
        "rewardValue": "15",
        "partnerId": 1,
        "startDate": 1700000000000,
        "endDate": 1710000000000,
        "taskStatus": "FINISH",
        "currentProgress": 2,
        "status": "IN_PROGRESS",
        "voucherRequest": {
          "voucherCode": "VC-REWARD01",
          "voucherName": "Voucher thưởng 100k",
          "description": "Giảm 100k không điều kiện",
          "discountType": "FIXED",
          "discountValue": "100000",
          "maxDiscount": "100000",
          "minOrderValue": "0",
          "totalStock": 50,
          "availableStock": 30,
          "startDate": 1700000000000,
          "endDate": 1710000000000,
          "voucherStatus": "ACTIVE",
          "nameStore": "Coffee House"
        }
      }
    ],
    "totalElements": 2,
    "totalPages": 1,
    "currentPage": 0,
    "pageSize": 20
  }
}
```

**Giải thích fields:**
| Field           | Mô tả                                                         |
|-----------------|----------------------------------------------------------------|
| targetType      | `AMOUNT` = theo số tiền chi tiêu, `COUNT` = theo số lần mua  |
| targetValue     | Mục tiêu cần đạt                                              |
| currentProgress | Tiến độ hiện tại của customer                                  |
| status          | `IN_PROGRESS` = đang làm, `COMPLETED` = hoàn thành (chưa nhận thưởng), `CLAIMED` = đã nhận thưởng |
| rewardType      | `POINT` = thưởng điểm, `VOUCHER` = thưởng voucher            |
| rewardValue     | Nếu POINT = số điểm, nếu VOUCHER = ID voucher                |
| voucherRequest  | Chi tiết voucher thưởng (chỉ có khi rewardType = VOUCHER)     |
| partnerId       | 0 = nhiệm vụ hệ thống, > 0 = nhiệm vụ của đối tác           |

**Logic hiển thị trên FE:**
- `status = IN_PROGRESS`: Hiển thị progress bar (currentProgress / targetValue)
- `status = COMPLETED`: Hiển thị nút "Nhận thưởng"
- `status = CLAIMED`: Hiển thị "Đã nhận thưởng" (disabled)

---

### 3.2 Nhận thưởng nhiệm vụ

> Gọi khi customer bấm nút "Nhận thưởng" (chỉ khi status = COMPLETED).

```
POST /api/customers/missions/claim-reward
```

**Request Body:**
```json
{
  "missionId": 1
}
```

**Response (thưởng điểm):**
```json
{
  "status": 0,
  "code": "success",
  "message": "Success",
  "data": {
    "rewardType": "POINT",
    "rewardValue": "100 points",
    "message": "Successfully claimed 100 points"
  }
}
```

**Response (thưởng voucher):**
```json
{
  "status": 0,
  "code": "success",
  "message": "Success",
  "data": {
    "rewardType": "VOUCHER",
    "rewardValue": "Voucher thưởng 100k",
    "message": "Successfully claimed voucher: Voucher thưởng 100k"
  }
}
```

**Lỗi có thể gặp:**
| Code                       | Mô tả                              |
|----------------------------|-------------------------------------|
| CUSTOMER_NOT_FOUND         | Không tìm thấy customer            |
| CUSTOMER_MISSION_NOT_FOUND | Customer chưa tham gia nhiệm vụ này|
| MISSION_NOT_COMPLETED      | Nhiệm vụ chưa hoàn thành           |

---

## 4. Leaderboard (Bảng xếp hạng)

### 4.1 Lấy bảng xếp hạng

```
GET /api/customers/leaderboard
```

**Không có params**

**Response:**
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
      },
      {
        "customerId": 3,
        "customerName": "Tran Thi B",
        "totalPoints": 4200,
        "rank": 2
      }
    ],
    "currentCustomer": {
      "customerId": 5,
      "customerName": "Le Van C",
      "totalPoints": 1200,
      "rank": 15
    }
  }
}
```

**Giải thích:**
- `topCustomers`: Top N khách hàng có điểm cao nhất
- `currentCustomer`: Thông tin xếp hạng của customer đang đăng nhập

---

## 5. Invoices (Hóa đơn mẫu)

### 5.1 Danh sách hóa đơn

> Danh sách hóa đơn mẫu để customer chọn thanh toán.

```
GET /api/customers/invoices
```

| Param     | Vị trí | Type   | Bắt buộc | Default | Mô tả             |
|-----------|--------|--------|----------|---------|--------------------|
| nameStore | query  | String | ❌       |         | Lọc theo cửa hàng |
| title     | query  | String | ❌       |         | Lọc theo tiêu đề  |
| page      | query  | int    | ❌       | 0       | Số trang           |
| size      | query  | int    | ❌       | 20      | Số item/trang      |

**Response:**
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

## 6. Payment (Thanh toán)

### 6.1 Xử lý thanh toán

> Thanh toán hóa đơn, có thể áp dụng voucher. Sau thanh toán sẽ tự động:
> - Tích điểm loyalty (1 điểm / 1.000đ)
> - Cập nhật tiến độ nhiệm vụ
> - Giảm stock voucher (nếu dùng)

```
POST /api/v1/payments/process
```

**Request Body:**
```json
{
  "invoiceId": 1,
  "voucherId": 10,
  "orderAmount": 500000.00
}
```

| Field       | Type       | Bắt buộc | Mô tả                                    |
|-------------|------------|----------|-------------------------------------------|
| invoiceId   | Long       | ❌       | ID hóa đơn                                |
| voucherId   | Long       | ❌       | ID voucher áp dụng (null = không dùng)    |
| orderAmount | BigDecimal | ✅       | Tổng tiền đơn hàng                        |

**Response:**
```json
{
  "transactionId": "TXN-A1B2C3D4E5F6",
  "originalAmount": 500000.00,
  "discountAmount": 50000.00,
  "finalAmount": 450000.00,
  "pointsEarned": 450,
  "status": "SUCCESS"
}
```

> ⚠️ **Lưu ý:** API Payment trả response trực tiếp (không wrap trong BaseResponse).

**Giải thích fields:**
| Field          | Mô tả                                    |
|----------------|-------------------------------------------|
| transactionId  | Mã giao dịch duy nhất                    |
| originalAmount | Tổng tiền gốc                            |
| discountAmount | Số tiền được giảm (0 nếu không dùng voucher) |
| finalAmount    | Số tiền thực trả                         |
| pointsEarned   | Điểm loyalty nhận được                   |
| status         | SUCCESS hoặc FAILED                      |

**Lỗi có thể gặp:**
| Code               | Mô tả                                    |
|--------------------|-------------------------------------------|
| CUSTOMER_NOT_FOUND | Không tìm thấy customer                  |
| VOUCHER_NOT_FOUND  | Customer chưa collect voucher này         |
| VOUCHER_OUT_OF_STOCK| Voucher đã hết lượt sử dụng             |
| MIN_ORDER_NOT_MET  | Đơn hàng chưa đạt giá trị tối thiểu     |

---

## Flow tích hợp cho Frontend

### Flow 1: Xem & Collect Voucher
```
1. GET /api/customers/vouchers/available/with-status → Hiển thị danh sách
2. User bấm "Thu thập" → POST /api/customers/vouchers/collect/{voucherId}
3. Reload danh sách (collected = true)
```

### Flow 2: Thanh toán với Voucher
```
1. GET /api/customers/invoices → Chọn hóa đơn
2. GET /api/customers/vouchers/applicable?customerId=X&nameStore=Y&orderAmount=Z → Hiển thị voucher áp dụng được
3. User chọn voucher (hoặc không) → POST /api/v1/payments/process
4. Hiển thị kết quả thanh toán
```

### Flow 3: Nhiệm vụ
```
1. GET /api/customers/missions → Hiển thị danh sách + progress
2. Khi status = COMPLETED → Hiển thị nút "Nhận thưởng"
3. User bấm → POST /api/customers/missions/claim-reward
4. Hiển thị thông báo thưởng
```

### Flow 4: Bảng xếp hạng
```
1. GET /api/customers/leaderboard → Hiển thị top + vị trí hiện tại
```
