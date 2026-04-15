package com.example.voucherservice.constant;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CustomerTier {
    ALL(0),
    SILVER(1),
    GOLD(2),
    PLATINUM(3),
    DIAMOND(4);

    private final int rank;

    public boolean canAccess(CustomerTier voucherTier) {
        return voucherTier == ALL || this.rank >= voucherTier.rank;
    }

    public List<CustomerTier> accessibleTiers() {
        return Arrays.stream(values())
                .filter(t -> t == ALL || t.rank <= this.rank)
                .collect(Collectors.toList());
    }
}
