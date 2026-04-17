package com.example.voucherservice.repository;

import com.example.voucherservice.constant.DiscountType;
import com.example.voucherservice.constant.RequestStatus;
import com.example.voucherservice.entity.VoucherRequestEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoucherRequestRepository extends JpaRepository<VoucherRequestEntity, Long> {

    Optional<VoucherRequestEntity> findByRequestId(String requestId);

    boolean existsByRequestIdAndStatusIn(String requestId, List<RequestStatus> statuses);

    @Query("SELECT r FROM VoucherRequestEntity r WHERE "
            + "(CAST(:status AS string) IS NULL OR r.status = :status) "
            + "AND (CAST(:fromDate AS string) IS NULL OR r.createdTime >= :fromDate) "
            + "AND (CAST(:toDate AS string) IS NULL OR r.createdTime <= :toDate) "
            + "AND (CAST(:createdBy AS string) IS NULL OR r.createdBy = :createdBy) "
            + "ORDER BY r.createdTime DESC")
    Page<VoucherRequestEntity> findByFilters(
            @Param("status") RequestStatus status,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("createdBy") String createdBy,
            Pageable pageable);
}
