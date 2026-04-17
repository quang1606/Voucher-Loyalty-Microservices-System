package com.example.voucherservice.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class VoucherDetailResponsePage {
    private List<VoucherDetailResponse> data;
    private long totalElements;
    private int totalPages;
    private int page;
    private int size;
}
