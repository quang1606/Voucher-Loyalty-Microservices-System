package com.example.voucherservice.repository;

import com.example.voucherservice.constant.RequestStatus;
import com.example.voucherservice.entity.VoucherRequestEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VoucherRequestRepository extends JpaRepository<VoucherRequestEntity, Long>,
    JpaSpecificationExecutor<VoucherRequestEntity> {

    Optional<VoucherRequestEntity> findByRequestId(String requestId);

    boolean existsByRequestIdAndStatusIn(String requestId, List<RequestStatus> statuses);
}
