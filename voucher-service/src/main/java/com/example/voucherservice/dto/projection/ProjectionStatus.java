package com.example.voucherservice.dto.projection;

import com.example.voucherservice.constant.RequestStatus;

public interface ProjectionStatus {
    String getRequestId();
    RequestStatus getRequestStatus();
    Long getCount();
}
