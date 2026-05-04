package com.example.customerservice.repository;

import com.example.customerservice.constant.CreatorType;
import com.example.customerservice.constant.CustomerVoucherStatus;
import com.example.customerservice.entity.CustomerVoucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CustomerVoucherRepository extends JpaRepository<CustomerVoucher, Long> {
    List<CustomerVoucher> findByCustomerId(Long customerId);
    
    Page<CustomerVoucher> findByCustomerId(Long customerId, Pageable pageable);
    
    Page<CustomerVoucher> findByCustomerIdAndStatus(Long customerId, CustomerVoucherStatus status, Pageable pageable);
    
    @Query("SELECT cv FROM CustomerVoucher cv WHERE " +
           "(:customerId IS NULL OR cv.customerId = :customerId) AND " +
           "(:voucherId IS NULL OR cv.voucherId = :voucherId) AND " +
           "(:status IS NULL OR cv.status = :status)")
    Page<CustomerVoucher> findByFilters(@Param("customerId") Long customerId,
                                      @Param("voucherId") Long voucherId,
                                      @Param("status") CustomerVoucherStatus status,
                                      Pageable pageable);
    
    Optional<CustomerVoucher> findByCustomerIdAndVoucherId(Long customerId, Long voucherId);

    @Query("SELECT cv FROM CustomerVoucher cv WHERE cv.customerId = :customerId " +
           "AND cv.status = 'AVAILABLE' " +
           "AND (cv.nameStore = :nameStore OR cv.creatorType = :systemType)")
    List<CustomerVoucher> findAvailableByCustomerAndStore(@Param("customerId") Long customerId,
                                                         @Param("nameStore") String nameStore,
                                                         @Param("systemType") CreatorType systemType);
}
