package com.example.customerservice.service;

import com.example.customerservice.constant.CustomerMissionStatus;
import com.example.customerservice.dto.response.CustomerMissionListResponse;
import com.example.customerservice.dto.response.MissionResponse;
import org.springframework.data.domain.Pageable;

public interface CustomerMissionService {
    MissionResponse getAvailableMissions(int page, int size);
    CustomerMissionListResponse getCustomerMissions(Long customerId,
                                                  CustomerMissionStatus status, Pageable pageable);
}