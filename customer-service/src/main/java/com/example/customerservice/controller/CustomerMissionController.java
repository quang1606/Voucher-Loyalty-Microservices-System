package com.example.customerservice.controller;

import com.example.common.BaseResponse;
import com.example.customerservice.entity.CustomerMission;
import com.example.customerservice.repository.CustomerMissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers/missions")
@RequiredArgsConstructor
public class CustomerMissionController {

    private final CustomerMissionRepository customerMissionRepository;

    @GetMapping("/{customerId}")
    public ResponseEntity<BaseResponse<List<CustomerMission>>> getMyMissions(@PathVariable Long customerId) {
        List<CustomerMission> missions = customerMissionRepository.findByCustomerId(customerId);
        return ResponseEntity.ok(BaseResponse.<List<CustomerMission>>builder()
                .status(200).code("SUCCESS").message("OK").data(missions).build());
    }
}
