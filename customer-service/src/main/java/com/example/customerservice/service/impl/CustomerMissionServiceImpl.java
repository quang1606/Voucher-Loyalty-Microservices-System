package com.example.customerservice.service.impl;

import com.example.customerservice.constant.CustomerMissionStatus;
import com.example.customerservice.dto.response.CustomerMissionListResponse;
import com.example.customerservice.dto.response.MissionResponse;
import com.example.customerservice.entity.CustomerMission;
import com.example.customerservice.grpc.MissionGrpcClient;
import com.example.customerservice.grpc.VoucherGrpcClient;
import com.example.customerservice.mapper.CustomerMissionMapper;
import com.example.customerservice.mapper.MissionMapper;
import com.example.customerservice.repository.CustomerMissionRepository;
import com.example.customerservice.service.CustomerMissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.com.grpc.loyalty.entity.SearchMissionResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerMissionServiceImpl implements CustomerMissionService {

    private final MissionGrpcClient missionGrpcClient;
    private final VoucherGrpcClient voucherGrpcClient;
    private final CustomerMissionRepository customerMissionRepository;

    @Override
    public MissionResponse getAvailableMissions(int page, int size) {
        log.info("Getting available missions - page: {}, size: {}", page, size);
        SearchMissionResponse grpcResponse = missionGrpcClient.getMissions(page, size, "startDate,desc");
        return MissionMapper.toMissionResponse(grpcResponse, page, size, voucherGrpcClient);
    }

    @Override
    public CustomerMissionListResponse getCustomerMissions(Long customerId,
                                                         CustomerMissionStatus status, Pageable pageable) {
        log.info("Getting customer missions - customerId:: {}, status: {}, page: {}, size: {}",
                customerId, status, pageable.getPageNumber(), pageable.getPageSize());

        Page<CustomerMission> missionPage = customerMissionRepository.findByFilters(
                customerId, status, pageable);

        log.info("Found {} customer missions", missionPage.getTotalElements());

        return CustomerMissionListResponse.builder()
                .data(CustomerMissionMapper.toResponseList(missionPage.getContent()))
                .totalElements((int) missionPage.getTotalElements())
                .totalPages(missionPage.getTotalPages())
                .currentPage(missionPage.getNumber())
                .pageSize(missionPage.getSize())
                .build();
    }
}