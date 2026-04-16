package com.example.voucherservice.repository;

import com.example.voucherservice.constant.RequestStatus;
import com.example.voucherservice.entity.VoucherDetailEntity;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoucherRepository extends JpaRepository<VoucherDetailEntity, Long> {

    List<VoucherDetailEntity> findByRequestIdAndRequestStatusAndIdGreaterThanOrderByIdAsc(
            String requestId, RequestStatus requestStatus, Long id, Pageable pageable);

    boolean existsByRequestIdAndRequestStatus(String requestId, RequestStatus requestStatus);

    boolean existsByRequestIdAndDiscountTypeIsNull(String requestId);
}
