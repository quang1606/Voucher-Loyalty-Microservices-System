package com.example.voucherservice.dto.request;

import com.example.voucherservice.constant.ConfirmAction;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ConfirmVoucherRequest {

    @NotNull(message = "Action is required")
    private ConfirmAction action;

    private String reason;
}
