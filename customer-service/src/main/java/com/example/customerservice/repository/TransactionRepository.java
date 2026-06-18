package com.example.customerservice.repository;

import com.example.customerservice.dto.response.VoucherRequestStatsResponse;
import com.example.customerservice.dto.response.VoucherUsageStatsProjection;
import com.example.customerservice.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findByCustomerId(Long customerId, Pageable pageable);

    Optional<Transaction> findByIdAndCustomerId(Long id, Long customerId);

    @Query("SELECT new com.example.customerservice.dto.response.VoucherRequestStatsResponse(" +
           "COUNT(t), COALESCE(SUM(t.discountAmount), 0)) " +
           "FROM Transaction t " +
           "WHERE t.requestId = :requestId AND t.status = :status")
    VoucherRequestStatsResponse getStatsByRequestIdAndStatus(
        @Param("requestId") String requestId,
        @Param("status") Transaction.TransactionStatus status);

    @Query("SELECT new com.example.customerservice.dto.response.VoucherRequestStatsResponse(" +
           "COUNT(t), COALESCE(SUM(t.discountAmount), 0)) " +
           "FROM Transaction t " +
           "WHERE t.requestId = :requestId AND t.status = :status " +
           "AND MONTH(t.createdAt) = :month AND YEAR(t.createdAt) = :year")
    VoucherRequestStatsResponse getStatsByRequestIdAndStatusAndMonthAndYear(
        @Param("requestId") String requestId,
        @Param("status") Transaction.TransactionStatus status,
        @Param("month") int month,
        @Param("year") int year);

    @Query("SELECT new com.example.customerservice.dto.response.VoucherRequestStatsResponse(" +
           "COUNT(t), COALESCE(SUM(t.discountAmount), 0)) " +
           "FROM Transaction t " +
           "WHERE t.requestId = :requestId AND t.status = :status " +
           "AND YEAR(t.createdAt) = :year")
    VoucherRequestStatsResponse getStatsByRequestIdAndStatusAndYear(
        @Param("requestId") String requestId,
        @Param("status") Transaction.TransactionStatus status,
        @Param("year") int year);

    @Query("SELECT t.requestId as requestId, t.voucherCode as voucherCode, COUNT(t) as usedCount, COALESCE(SUM(t.discountAmount), 0) as totalDiscount " +
           "FROM Transaction t " +
           "WHERE t.requestId IN :requestIds AND t.status = 'SUCCESS' " +
           "AND MONTH(t.createdAt) = :month AND YEAR(t.createdAt) = :year " +
           "GROUP BY t.requestId, t.voucherCode")
    List<VoucherUsageStatsProjection> getUsageStatsByRequestIds(
        @Param("requestIds") List<String> requestIds,
        @Param("month") int month,
        @Param("year") int year);
}