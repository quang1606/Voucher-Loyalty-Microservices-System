package com.example.customerservice.repository;

import com.example.customerservice.entity.CustomerVoucher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerVoucherRepository extends JpaRepository<CustomerVoucher, Long> {
    List<CustomerVoucher> findByCustomerId(Long customerId);
}
