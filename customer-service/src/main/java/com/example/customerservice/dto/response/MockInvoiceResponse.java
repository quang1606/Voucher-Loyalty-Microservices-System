package com.example.customerservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MockInvoiceResponse {
    private Long id;
    private String title;
    private String nameStore;
    private String amount;
    private Long createdAt;
    private Long updatedAt;
}
