package com.example.voucherservice.repository;

import com.example.voucherservice.entity.VoucherDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoucherRepository extends JpaRepository<VoucherDetailEntity, Long> {
}
