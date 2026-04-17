package com.example.voucherservice.specification;

import com.example.voucherservice.constant.CreatorType;
import com.example.voucherservice.constant.RequestMode;
import com.example.voucherservice.constant.VoucherPurpose;
import com.example.voucherservice.entity.VoucherRequestEntity;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public final class VoucherRequestSpecification {

    private VoucherRequestSpecification() {
    }

    public static Specification<VoucherRequestEntity> withFilters(
        List<String> statuses,
        LocalDateTime fromDate,
        LocalDateTime toDate,
        String createdBy,
        RequestMode requestMode,
        CreatorType creatorType,
        VoucherPurpose voucherPurpose,
        String storeName) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (statuses != null && !statuses.isEmpty()) {
                predicates.add(root.get("status").as(String.class).in(statuses));
            }
            if (fromDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdTime"), fromDate));
            }
            if (toDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdTime"), toDate));
            }
            if (createdBy != null) {
                predicates.add(cb.equal(root.get("createdBy"), createdBy));
            }
            if (requestMode != null) {
                predicates.add(cb.equal(root.get("requestMode"), requestMode));
            }
            if (creatorType != null) {
                predicates.add(cb.equal(root.get("creatorType"), creatorType));
            }
            if (voucherPurpose != null) {
                predicates.add(cb.equal(root.get("voucherPurpose"), voucherPurpose));
            }
            if (storeName != null) {
                predicates.add(cb.equal(root.get("storeName"), storeName));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
