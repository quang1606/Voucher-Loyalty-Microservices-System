package com.example.customerservice.repository;

import com.example.customerservice.entity.CustomerMission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CustomerMissionRepository extends JpaRepository<CustomerMission, Long> {
    List<CustomerMission> findByCustomerId(Long customerId);
}
