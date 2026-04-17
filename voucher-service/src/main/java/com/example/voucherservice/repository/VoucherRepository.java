package com.example.voucherservice.repository;

import com.example.voucherservice.constant.CustomerTier;
import com.example.voucherservice.constant.DiscountType;
import com.example.voucherservice.constant.RequestStatus;
import com.example.voucherservice.constant.VoucherStatus;
import com.example.voucherservice.dto.projection.ProjectionStatus;
import com.example.voucherservice.dto.projection.ProjectionTotalVoucher;
import com.example.voucherservice.entity.VoucherDetailEntity;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoucherRepository extends JpaRepository<VoucherDetailEntity, Long>,
    JpaSpecificationExecutor<VoucherDetailEntity> {

    List<VoucherDetailEntity> findByRequestIdAndRequestStatusAndIdGreaterThanOrderByIdAsc(
            String requestId, RequestStatus requestStatus, Long id, Pageable pageable);

    boolean existsByRequestIdAndRequestStatus(String requestId, RequestStatus requestStatus);

    boolean existsByRequestIdAndDiscountTypeIsNull(String requestId);

    boolean existsByRequestIdAndRequestStatusNot(String requestId, RequestStatus requestStatus);

    @Query("SELECT DISTINCT v.requestId FROM VoucherDetailEntity v WHERE "
            + "(:name IS NULL OR LOWER(v.voucherName) LIKE LOWER(CONCAT('%', :name, '%'))) "
            + "AND (:customerTier IS NULL OR v.customerTier = :customerTier) "
            + "AND (:voucherStatus IS NULL OR v.status = :voucherStatus) "
            + "AND (:discountType IS NULL OR v.discountType = :discountType)")
    List<String> findDistinctRequestIdsByFilters(
            @Param("name") String name,
            @Param("customerTier") CustomerTier customerTier,
            @Param("voucherStatus") VoucherStatus voucherStatus,
            @Param("discountType") DiscountType discountType);

    @Query("SELECT v.requestId AS requestId, COUNT(v) AS totalVoucher FROM VoucherDetailEntity v WHERE v.requestId IN :requestIds GROUP BY v.requestId")
    List<ProjectionTotalVoucher> countTotalVoucherByRequestIds(List<String> requestIds);

    @Query("SELECT v.requestId AS requestId, v.requestStatus AS requestStatus, COUNT(v) AS count FROM VoucherDetailEntity v WHERE v.requestId IN :requestIds GROUP BY v.requestId, v.requestStatus")
    List<ProjectionStatus> countStatusByRequestIds(List<String> requestIds);
}
