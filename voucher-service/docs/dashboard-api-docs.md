# Dashboard API Documentation

> Base URL: `http://localhost:8082/api/dashboard`  
> Authentication: Bearer Token (JWT)

---

## 1. Voucher Monthly Stats

Thống kê số voucher được approve theo từng tháng trong năm.

**Endpoint:**
```
GET /api/dashboard/voucher-monthly-stats?year={year}
```

**Parameters:**

| Name | Type | Required | Description |
|------|------|----------|-------------|
| year | Integer | ✅ | Năm cần thống kê |

**Response:**
```json
{
  "status": 0,
  "code": "success",
  "message": "Success",
  "data": [
    { "month": 1, "total": 12 },
    { "month": 2, "total": 8 },
    { "month": 3, "total": 25 },
    { "month": 5, "total": 15 }
  ]
}
```

**Ghi chú:** Chỉ trả về các tháng có data (total > 0). Nếu tháng nào không có voucher approve thì không xuất hiện trong mảng.

---

## 2. Voucher Request Stats

Thống kê tổng quan voucher request: tổng số, hoàn thành, chưa hoàn thành.

**Endpoint:**
```
GET /api/dashboard/voucher-request-stats
```

**Response:**
```json
{
  "status": 0,
  "code": "success",
  "message": "Success",
  "data": {
    "totalRequests": 150,
    "completedRequests": 100,
    "incompleteRequests": 50
  }
}
```

| Field | Description |
|-------|-------------|
| totalRequests | Tổng số voucher request |
| completedRequests | Đã hoàn thành (APPROVED, REJECTED, FAILED, FINISH) |
| incompleteRequests | Chưa hoàn thành (các trạng thái còn lại) |

---

## 3. Mission Stats

Thống kê tổng quan mission: tổng số, hoàn thành, chưa hoàn thành.

**Endpoint:**
```
GET /api/dashboard/mission-stats
```

**Response:**
```json
{
  "status": 0,
  "code": "success",
  "message": "Success",
  "data": {
    "totalMissions": 80,
    "completedMissions": 50,
    "incompleteMissions": 30
  }
}
```

| Field | Description |
|-------|-------------|
| totalMissions | Tổng số mission |
| completedMissions | Đã hoàn thành (APPROVED, REJECTED, FAILED, FINISH, CANCELLED) |
| incompleteMissions | Chưa hoàn thành (total - completed) |

---

## 4. Mission Monthly Stats

Thống kê số mission được tạo theo từng tháng trong năm.

**Endpoint:**
```
GET /api/dashboard/mission-monthly-stats?year={year}
```

**Parameters:**

| Name | Type | Required | Description |
|------|------|----------|-------------|
| year | Integer | ✅ | Năm cần thống kê |

**Response:**
```json
{
  "status": 0,
  "code": "success",
  "message": "Success",
  "data": [
    { "month": 1, "total": 5 },
    { "month": 3, "total": 10 },
    { "month": 5, "total": 8 }
  ]
}
```

**Ghi chú:** Giống voucher-monthly-stats, chỉ trả về tháng có data.

---

## 5. Voucher Request Status Stats

Thống kê số lượng voucher request theo từng trạng thái cụ thể.

**Endpoint:**
```
GET /api/dashboard/voucher-request-status-stats
```

**Response:**
```json
{
  "status": 0,
  "code": "success",
  "message": "Success",
  "data": {
    "draft": 10,
    "cancelled": 5,
    "pendingApprove": 20,
    "approved": 80,
    "rejected": 12
  }
}
```

| Field | Description |
|-------|-------------|
| draft | Số request ở trạng thái nháp |
| cancelled | Số request đã hủy |
| pendingApprove | Số request đang chờ duyệt |
| approved | Số request đã duyệt |
| rejected | Số request bị từ chối |

---

## Ghi chú chung

### Phân quyền
- **MAKER / CHECKER**: Xem tất cả data
- **PARTNER**: Chỉ xem data thuộc store của mình (API tự filter theo token)

### Error Response
```json
{
  "status": 1006,
  "code": "INTERNAL_ERROR",
  "message": "Error description",
  "data": null
}
```

### Gợi ý UI

| API | Loại chart phù hợp |
|-----|-------------------|
| voucher-monthly-stats | Line chart / Bar chart (theo tháng) |
| mission-monthly-stats | Line chart / Bar chart (theo tháng) |
| voucher-request-stats | Donut chart / Stat cards |
| mission-stats | Donut chart / Stat cards |
| voucher-request-status-stats | Pie chart / Stacked bar |

### Ví dụ gọi API (Axios)

```javascript
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8082',
  headers: { Authorization: `Bearer ${token}` }
});

// Voucher monthly stats
const getVoucherMonthlyStats = (year) =>
  api.get(`/api/dashboard/voucher-monthly-stats?year=${year}`);

// Mission monthly stats
const getMissionMonthlyStats = (year) =>
  api.get(`/api/dashboard/mission-monthly-stats?year=${year}`);

// Voucher request stats (total/completed/incomplete)
const getVoucherRequestStats = () =>
  api.get('/api/dashboard/voucher-request-stats');

// Mission stats (total/completed/incomplete)
const getMissionStats = () =>
  api.get('/api/dashboard/mission-stats');

// Voucher request status stats (draft/cancelled/pending/approved/rejected)
const getVoucherRequestStatusStats = () =>
  api.get('/api/dashboard/voucher-request-status-stats');
```

### Ví dụ React Component

```jsx
import { useEffect, useState } from 'react';

const Dashboard = () => {
  const [voucherStats, setVoucherStats] = useState(null);
  const [missionStats, setMissionStats] = useState(null);
  const [statusStats, setStatusStats] = useState(null);
  const [monthlyVoucher, setMonthlyVoucher] = useState([]);
  const [monthlyMission, setMonthlyMission] = useState([]);

  useEffect(() => {
    const year = new Date().getFullYear();

    Promise.all([
      getVoucherRequestStats(),
      getMissionStats(),
      getVoucherRequestStatusStats(),
      getVoucherMonthlyStats(year),
      getMissionMonthlyStats(year),
    ]).then(([vRes, mRes, sRes, vmRes, mmRes]) => {
      setVoucherStats(vRes.data.data);
      setMissionStats(mRes.data.data);
      setStatusStats(sRes.data.data);
      setMonthlyVoucher(vmRes.data.data);
      setMonthlyMission(mmRes.data.data);
    });
  }, []);

  return (
    <div>
      {/* Stat Cards */}
      <div className="stats-grid">
        <StatCard title="Total Voucher Requests" value={voucherStats?.totalRequests} />
        <StatCard title="Completed" value={voucherStats?.completedRequests} />
        <StatCard title="Total Missions" value={missionStats?.totalMissions} />
        <StatCard title="Completed Missions" value={missionStats?.completedMissions} />
      </div>

      {/* Charts */}
      <LineChart title="Voucher Monthly" data={monthlyVoucher} />
      <LineChart title="Mission Monthly" data={monthlyMission} />
      <PieChart title="Request Status" data={statusStats} />
    </div>
  );
};
```
