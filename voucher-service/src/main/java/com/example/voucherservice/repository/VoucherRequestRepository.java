package com.example.voucherservice.repository;

import com.example.voucherservice.entity.VoucherRequestEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoucherRequestRepository extends JpaRepository<VoucherRequestEntity, Long> {

    Optional<VoucherRequestEntity> findByRequestId(String requestId);
}
