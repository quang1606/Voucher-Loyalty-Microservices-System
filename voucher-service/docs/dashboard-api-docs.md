# Dashboard API Documentation

## Base URL
```
/api/dashboard
```

## Authentication
All endpoints require authentication with appropriate roles.

---

## 1. Get Voucher Monthly Statistics

### Endpoint
```http
GET /api/dashboard/voucher-monthly-stats
```

### Description
Lấy thống kê voucher theo tháng trong năm

### Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| year | Integer | Yes | Năm cần thống kê (ví dụ: 2024) |

### Request Example
```http
GET /api/dashboard/voucher-monthly-stats?year=2024
```

### Response
```json
{
  "status": 200,
  "code": "SUCCESS",
  "message": "Success",
  "data": [
    {
      "month": 1,
      "total": 150
    },
    {
      "month": 2,
      "total": 200
    },
    {
      "month": 3,
      "total": 180
    }
  ]
}
```

### Response Fields
| Field | Type | Description |
|-------|------|-------------|
| month | Integer | Tháng (1-12) |
| total | Long | Tổng số voucher được approve trong tháng |

---

## 2. Get Voucher Request Statistics

### Endpoint
```http
GET /api/dashboard/voucher-request-stats
```

### Description
Lấy thống kê tổng quan về voucher request

### Parameters
None

### Request Example
```http
GET /api/dashboard/voucher-request-stats
```

### Response
```json
{
  "status": 200,
  "code": "SUCCESS",
  "message": "Success",
  "data": {
    "totalRequests": 1000,
    "completedRequests": 750,
    "incompleteRequests": 250
  }
}
```

### Response Fields
| Field | Type | Description |
|-------|------|-------------|
| totalRequests | Long | Tổng số voucher request |
| completedRequests | Long | Số voucher request đã hoàn thành |
| incompleteRequests | Long | Số voucher request chưa hoàn thành |

---

## 3. Get Mission Statistics

### Endpoint
```http
GET /api/dashboard/mission-stats
```

### Description
Lấy thống kê tổng quan về mission

### Parameters
None

### Request Example
```http
GET /api/dashboard/mission-stats
```

### Response
```json
{
  "status": 200,
  "code": "SUCCESS",
  "message": "Success",
  "data": {
    "totalMissions": 500,
    "completedMissions": 300,
    "incompleteMissions": 200
  }
}
```

### Response Fields
| Field | Type | Description |
|-------|------|-------------|
| totalMissions | Long | Tổng số mission |
| completedMissions | Long | Số mission đã hoàn thành (APPROVED, REJECTED, FAILED, FINISH, CANCELLED) |
| incompleteMissions | Long | Số mission chưa hoàn thành (totalMissions - completedMissions) |

---

## Error Responses

### Common Error Format
```json
{
  "status": 400,
  "code": "ERROR_CODE",
  "message": "Error description",
  "data": null
}
```

### Common Error Codes
| Status Code | Error Code | Description |
|-------------|------------|-------------|
| 400 | BAD_REQUEST | Invalid request parameters |
| 401 | UNAUTHORIZED | Authentication required |
| 403 | FORBIDDEN | Insufficient permissions |
| 500 | INTERNAL_SERVER_ERROR | Server error |

---

## Frontend Integration Examples

### JavaScript/Axios Examples

#### 1. Get Voucher Monthly Stats
```javascript
const getVoucherMonthlyStats = async (year) => {
  try {
    const response = await axios.get(`/api/dashboard/voucher-monthly-stats?year=${year}`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    return response.data.data;
  } catch (error) {
    console.error('Error fetching voucher monthly stats:', error);
    throw error;
  }
};

// Usage
getVoucherMonthlyStats(2024).then(data => {
  console.log('Monthly stats:', data);
  // Process data for charts
  const months = data.map(item => item.month);
  const totals = data.map(item => item.total);
});
```

#### 2. Get Voucher Request Stats
```javascript
const getVoucherRequestStats = async () => {
  try {
    const response = await axios.get('/api/dashboard/voucher-request-stats', {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    return response.data.data;
  } catch (error) {
    console.error('Error fetching voucher request stats:', error);
    throw error;
  }
};

// Usage
getVoucherRequestStats().then(data => {
  console.log('Request stats:', data);
  // Update dashboard widgets
  document.getElementById('total-requests').textContent = data.totalRequests;
  document.getElementById('completed-requests').textContent = data.completedRequests;
  document.getElementById('incomplete-requests').textContent = data.incompleteRequests;
});
```

#### 3. Get Mission Stats
```javascript
const getMissionStats = async () => {
  try {
    const response = await axios.get('/api/dashboard/mission-stats', {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    return response.data.data;
  } catch (error) {
    console.error('Error fetching mission stats:', error);
    throw error;
  }
};

// Usage
getMissionStats().then(data => {
  console.log('Mission stats:', data);
  // Create pie chart or progress bars
  const completionRate = (data.completedMissions / data.totalMissions * 100).toFixed(1);
  console.log(`Mission completion rate: ${completionRate}%`);
});
```

### React Hook Example
```javascript
import { useState, useEffect } from 'react';
import axios from 'axios';

const useDashboardStats = () => {
  const [voucherStats, setVoucherStats] = useState(null);
  const [missionStats, setMissionStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchStats = async () => {
      try {
        setLoading(true);
        const [voucherResponse, missionResponse] = await Promise.all([
          axios.get('/api/dashboard/voucher-request-stats'),
          axios.get('/api/dashboard/mission-stats')
        ]);
        
        setVoucherStats(voucherResponse.data.data);
        setMissionStats(missionResponse.data.data);
      } catch (err) {
        setError(err);
      } finally {
        setLoading(false);
      }
    };

    fetchStats();
  }, []);

  return { voucherStats, missionStats, loading, error };
};

// Usage in component
const DashboardComponent = () => {
  const { voucherStats, missionStats, loading, error } = useDashboardStats();

  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error: {error.message}</div>;

  return (
    <div>
      <div>Total Voucher Requests: {voucherStats?.totalRequests}</div>
      <div>Total Missions: {missionStats?.totalMissions}</div>
    </div>
  );
};
```

---

## Chart Integration Examples

### Chart.js Example for Monthly Stats
```javascript
const createMonthlyChart = async (year) => {
  const data = await getVoucherMonthlyStats(year);
  
  const ctx = document.getElementById('monthlyChart').getContext('2d');
  new Chart(ctx, {
    type: 'line',
    data: {
      labels: data.map(item => `Tháng ${item.month}`),
      datasets: [{
        label: 'Voucher được approve',
        data: data.map(item => item.total),
        borderColor: 'rgb(75, 192, 192)',
        backgroundColor: 'rgba(75, 192, 192, 0.2)',
        tension: 0.1
      }]
    },
    options: {
      responsive: true,
      plugins: {
        title: {
          display: true,
          text: `Thống kê Voucher năm ${year}`
        }
      },
      scales: {
        y: {
          beginAtZero: true
        }
      }
    }
  });
};
```

### Pie Chart for Mission Stats
```javascript
const createMissionPieChart = async () => {
  const data = await getMissionStats();
  
  const ctx = document.getElementById('missionPieChart').getContext('2d');
  new Chart(ctx, {
    type: 'pie',
    data: {
      labels: ['Đã hoàn thành', 'Chưa hoàn thành'],
      datasets: [{
        data: [data.completedMissions, data.incompleteMissions],
        backgroundColor: [
          'rgba(54, 162, 235, 0.8)',
          'rgba(255, 99, 132, 0.8)'
        ],
        borderColor: [
          'rgba(54, 162, 235, 1)',
          'rgba(255, 99, 132, 1)'
        ],
        borderWidth: 1
      }]
    },
    options: {
      responsive: true,
      plugins: {
        title: {
          display: true,
          text: 'Thống kê Mission'
        },
        legend: {
          position: 'bottom'
        }
      }
    }
  });
};
```

---

## Notes for Frontend Developers

1. **Authentication**: Tất cả API đều yêu cầu authentication token trong header
2. **Error Handling**: Luôn handle error cases và hiển thị thông báo phù hợp cho user
3. **Loading States**: Hiển thị loading indicator khi đang fetch data
4. **Caching**: Có thể cache data trong một khoảng thời gian ngắn để tránh gọi API liên tục
5. **Responsive**: Đảm bảo charts và widgets responsive trên các thiết bị khác nhau
6. **Real-time Updates**: Có thể implement auto-refresh hoặc WebSocket để cập nhật real-time

## API Testing

### Postman Collection
```json
{
  "info": {
    "name": "Dashboard API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Get Voucher Monthly Stats",
      "request": {
        "method": "GET",
        "header": [
          {
            "key": "Authorization",
            "value": "Bearer {{token}}"
          }
        ],
        "url": {
          "raw": "{{baseUrl}}/api/dashboard/voucher-monthly-stats?year=2024",
          "host": ["{{baseUrl}}"],
          "path": ["api", "dashboard", "voucher-monthly-stats"],
          "query": [
            {
              "key": "year",
              "value": "2024"
            }
          ]
        }
      }
    },
    {
      "name": "Get Voucher Request Stats",
      "request": {
        "method": "GET",
        "header": [
          {
            "key": "Authorization",
            "value": "Bearer {{token}}"
          }
        ],
        "url": {
          "raw": "{{baseUrl}}/api/dashboard/voucher-request-stats",
          "host": ["{{baseUrl}}"],
          "path": ["api", "dashboard", "voucher-request-stats"]
        }
      }
    },
    {
      "name": "Get Mission Stats",
      "request": {
        "method": "GET",
        "header": [
          {
            "key": "Authorization",
            "value": "Bearer {{token}}"
          }
        ],
        "url": {
          "raw": "{{baseUrl}}/api/dashboard/mission-stats",
          "host": ["{{baseUrl}}"],
          "path": ["api", "dashboard", "mission-stats"]
        }
      }
    }
  ]
}
```