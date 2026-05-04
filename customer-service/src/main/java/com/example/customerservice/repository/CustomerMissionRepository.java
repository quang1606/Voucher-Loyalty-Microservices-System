package com.example.customerservice.repository;

import com.example.customerservice.constant.CustomerMissionStatus;
import com.example.customerservice.entity.CustomerMission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerMissionRepository extends JpaRepository<CustomerMission, Long> {
    List<CustomerMission> findByCustomerId(Long customerId);
    
    Page<CustomerMission> findByCustomerId(Long customerId, Pageable pageable);
    
    Page<CustomerMission> findByCustomerIdAndStatus(Long customerId, CustomerMissionStatus status, Pageable pageable);
    
    @Query("SELECT cm FROM CustomerMission cm WHERE " +
           "(:customerId IS NULL OR cm.customerId = :customerId) AND " +
           "(:status IS NULL OR cm.status = :status)")
    Page<CustomerMission> findByFilters(@Param("customerId") Long customerId,
                                      @Param("status") CustomerMissionStatus status,
                                      Pageable pageable);
}
