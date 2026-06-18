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
           "WHERE v.status  IN ('APPROVED', 'REJECTED', 'FAILED', 'FINISH') AND YEAR(v.confirmedTime) = :year " +
           "GROUP BY MONTH(v.confirmedTime) " +
           "HAVING COUNT(v) > 0")
    List<VoucherMonthlyStatsProjection> getApprovedVouchersByMonth(Integer year);

    @Query("SELECT MONTH(v.confirmedTime) as month, COUNT(v) as total " +
           "FROM VoucherRequestEntity v " +
           "WHERE v.status  IN ('APPROVED', 'REJECTED', 'FAILED', 'FINISH') AND YEAR(v.confirmedTime) = :year " +
           "AND v.storeName = :storeName " +
           "GROUP BY MONTH(v.confirmedTime) " +
           "HAVING COUNT(v) > 0")
    List<VoucherMonthlyStatsProjection> getApprovedVouchersByMonthAndStore(Integer year, String storeName);

    long countByStatus(RequestStatus status);

    @Query("SELECT COUNT(v) FROM VoucherRequestEntity v WHERE v.status IN ('APPROVED', 'REJECTED', 'FAILED', 'FINISH')")
    long countCompletedRequests();

    @Query("SELECT COUNT(v) FROM VoucherRequestEntity v WHERE v.status NOT IN ('APPROVED', 'REJECTED', 'FAILED', 'FINISH')")
    long countIncompleteRequests();

    @Query("SELECT COUNT(v) FROM VoucherRequestEntity v WHERE v.storeName = :storeName")
    long countByStoreName(String storeName);

    @Query("SELECT COUNT(v) FROM VoucherRequestEntity v WHERE v.status IN ('APPROVED', 'REJECTED', 'FAILED', 'FINISH') AND v.storeName = :storeName")
    long countCompletedRequestsByStore(String storeName);

    @Query("SELECT COUNT(v) FROM VoucherRequestEntity v WHERE v.status NOT IN ('APPROVED', 'REJECTED', 'FAILED', 'FINISH') AND v.storeName = :storeName")
    long countIncompleteRequestsByStore(String storeName);

    @Query("SELECT COUNT(v) FROM VoucherRequestEntity v WHERE v.status = 'DRAFT'")
    long countByStatusDraft();

    @Query("SELECT COUNT(v) FROM VoucherRequestEntity v WHERE v.status = 'CANCELLED'")
    long countByStatusCancelled();

    @Query("SELECT COUNT(v) FROM VoucherRequestEntity v WHERE v.status = 'PENDING_APPROVE'")
    long countByStatusPendingApprove();

    @Query("SELECT COUNT(v) FROM VoucherRequestEntity v WHERE v.status = 'FINISH'")
    long countByStatusFinish();

    @Query("SELECT COUNT(v) FROM VoucherRequestEntity v WHERE v.status = 'REJECTED'")
    long countByStatusRejected();

    @Query("SELECT COUNT(v) FROM VoucherRequestEntity v WHERE v.status = 'FAILED'")
    long countByStatusFailed();

    @Query("SELECT COUNT(v) FROM VoucherRequestEntity v WHERE v.status = 'DRAFT' AND v.storeName = :storeName")
    long countByStatusDraftAndStore(String storeName);

    @Query("SELECT COUNT(v) FROM VoucherRequestEntity v WHERE v.status = 'CANCELLED' AND v.storeName = :storeName")
    long countByStatusCancelledAndStore(String storeName);

    @Query("SELECT COUNT(v) FROM VoucherRequestEntity v WHERE v.status = 'PENDING_APPROVE' AND v.storeName = :storeName")
    long countByStatusPendingApproveAndStore(String storeName);

    @Query("SELECT COUNT(v) FROM VoucherRequestEntity v WHERE v.status = 'FINISH' AND v.storeName = :storeName")
    long countByStatusFinishAndStore(String storeName);

    @Query("SELECT COUNT(v) FROM VoucherRequestEntity v WHERE v.status = 'REJECTED' AND v.storeName = :storeName")
    long countByStatusRejectedAndStore(String storeName);

    @Query("SELECT COUNT(v) FROM VoucherRequestEntity v WHERE v.status = 'FAILED' AND v.storeName = :storeName")
    long countByStatusFailedAndStore(String storeName);

    List<VoucherRequestEntity> findByStatus(RequestStatus status);

    List<VoucherRequestEntity> findByStatusAndStoreName(RequestStatus status, String storeName);
}