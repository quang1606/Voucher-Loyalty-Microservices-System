package com.example.voucherservice.repository;

import com.example.voucherservice.constant.RequestStatus;
import com.example.voucherservice.constant.VoucherPurpose;
import com.example.voucherservice.dto.projection.VoucherMonthlyStatsProjection;
import com.example.voucherservice.entity.VoucherRequestEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface VoucherRequestRepository extends JpaRepository<VoucherRequestEntity, Long>,
    JpaSpecificationExecutor<VoucherRequestEntity> {

    Optional<VoucherRequestEntity> findByRequestId(String requestId);

    boolean existsByRequestIdAndStatusIn(String requestId, List<RequestStatus> statuses);

    List<VoucherRequestEntity> findByVoucherPurpose(VoucherPurpose voucherPurpose);

    @Query("SELECT MONTH(v.confirmedTime) as month, COUNT(v) as total " +
           "FROM VoucherRequestEntity v " +
           "WHERE v.status = 'APPROVED' AND YEAR(v.confirmedTime) = :year " +
           "GROUP BY MONTH(v.confirmedTime) " +
           "HAVING COUNT(v) > 0")
    List<VoucherMonthlyStatsProjection> getApprovedVouchersByMonth(Integer year);

    long countByStatus(RequestStatus status);

    @Query("SELECT COUNT(v) FROM VoucherRequestEntity v WHERE v.status IN ('APPROVED', 'REJECTED', 'FAILED', 'FINISH')")
    long countCompletedRequests();

    @Query("SELECT COUNT(v) FROM VoucherRequestEntity v WHERE v.status NOT IN ('APPROVED', 'REJECTED', 'FAILED', 'FINISH')")
    long countIncompleteRequests();
}
