package com.example.customerservice.controller;

import com.example.common.BaseErrorCode;
import com.example.common.BaseResponse;
import com.example.customerservice.constant.CustomerMissionStatus;
import com.example.customerservice.dto.response.CustomerMissionListResponse;
import com.example.customerservice.dto.response.MissionResponse;
import com.example.customerservice.dto.response.CustomerMissionResponse;
import com.example.customerservice.entity.CustomerMission;
import com.example.customerservice.mapper.CustomerMissionMapper;
import com.example.customerservice.repository.CustomerMissionRepository;
import com.example.customerservice.service.CustomerMissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers/missions")
@RequiredArgsConstructor
public class CustomerMissionController {

    private final CustomerMissionRepository customerMissionRepository;
    private final CustomerMissionService customerMissionService;

    @GetMapping("/{customerId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BaseResponse<List<CustomerMissionResponse>>> getMyMissions(@PathVariable Long customerId) {
        List<CustomerMission> missions = customerMissionRepository.findByCustomerId(customerId);
        return ResponseEntity.ok(BaseResponse.<List<CustomerMissionResponse>>builder()
                .status(BaseErrorCode.SUCCESS.getErrorNumCode())
                .code(BaseErrorCode.SUCCESS.getErrorCode())
                .message(BaseErrorCode.SUCCESS.getErrorDescription())
                .data(CustomerMissionMapper.toResponseList(missions))
                .build());
    }

    @GetMapping("/available")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BaseResponse<MissionResponse>> getAvailableMissions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(BaseResponse.<MissionResponse>builder()
                .status(BaseErrorCode.SUCCESS.getErrorNumCode())
                .code(BaseErrorCode.SUCCESS.getErrorCode())
                .message(BaseErrorCode.SUCCESS.getErrorDescription())
                .data(customerMissionService.getAvailableMissions(page, size))
                .build());
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BaseResponse<CustomerMissionListResponse>> getCustomerMissions(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) CustomerMissionStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "startedAt,desc") String sort) {

        // Parse sort parameter
        String[] sortParts = sort.split(",");
        Sort.Direction direction = sortParts.length > 1 && "asc".equalsIgnoreCase(sortParts[1]) 
            ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParts[0]));

        CustomerMissionListResponse response = customerMissionService.getCustomerMissions(
                customerId, status, pageable);

        return ResponseEntity.ok(BaseResponse.<CustomerMissionListResponse>builder()
                .status(BaseErrorCode.SUCCESS.getErrorNumCode())
                .code(BaseErrorCode.SUCCESS.getErrorCode())
                .message(BaseErrorCode.SUCCESS.getErrorDescription())
                .data(response)
                .build());
    }
}
