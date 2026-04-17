package com.example.voucherservice.specification;

import com.example.voucherservice.constant.CustomerTier;
import com.example.voucherservice.constant.DiscountType;
import com.example.voucherservice.constant.RequestStatus;
import com.example.voucherservice.constant.VoucherStatus;
import com.example.voucherservice.entity.VoucherDetailEntity;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public final class VoucherDetailSpecification {

    private VoucherDetailSpecification() {
    }

    public static Specification<VoucherDetailEntity> withFilters(
        String requestId,
        String voucherName,
        RequestStatus requestStatus) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("requestId"), requestId));

            if (voucherName != null && !voucherName.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("voucherName")),
                    "%" + voucherName.trim().toLowerCase() + "%"));
            }
            if (requestStatus != null) {
                predicates.add(cb.equal(root.get("requestStatus"), requestStatus));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<VoucherDetailEntity> withAllFilters(
        List<String> requestIds,
        CustomerTier customerTier,
        DiscountType discountType,
        VoucherStatus voucherStatus,
        LocalDateTime fromDate,
        LocalDateTime toDate) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (requestIds != null && !requestIds.isEmpty()) {
                predicates.add(root.get("requestId").in(requestIds));
            }
            if (customerTier != null) {
                predicates.add(cb.equal(root.get("customerTier"), customerTier));
            }
            if (discountType != null) {
                predicates.add(cb.equal(root.get("discountType"), discountType));
            }
            if (voucherStatus != null) {
                predicates.add(cb.equal(root.get("status"), voucherStatus));
            }
            if (fromDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), fromDate));
            }
            if (toDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), toDate));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
