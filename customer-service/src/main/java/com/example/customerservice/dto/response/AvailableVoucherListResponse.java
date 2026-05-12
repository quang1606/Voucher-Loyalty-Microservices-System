package com.example.customerservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableVoucherListResponse {
    private List<AvailableVoucherResponse> data;
    private int totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
}
