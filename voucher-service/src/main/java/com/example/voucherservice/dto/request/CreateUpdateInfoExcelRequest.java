package com.example.voucherservice.dto.request;

import com.example.voucherservice.constant.DiscountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUpdateInfoExcelRequest {

    @NotNull(message = "file is required")
    private MultipartFile file;

    @NotNull(message = "requestType is required")
    private DiscountType discountType;

    @NotBlank(message = "requestId is required")
    private String requestId;
}