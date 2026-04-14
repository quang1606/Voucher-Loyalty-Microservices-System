package com.example.voucherservice.controller;

import com.example.common.BaseErrorCode;
import com.example.common.BaseResponse;
import com.example.voucherservice.constant.DiscountType;
import com.example.voucherservice.constant.RequestMode;
import com.example.voucherservice.constant.RequestStatus;
import com.example.voucherservice.dto.request.CreateUpdateInfoExcelRequest;
import com.example.voucherservice.dto.request.CreateVoucherExcel;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/vouchers")
@RequiredArgsConstructor
public class VoucherController {

  @PostMapping
  @PreAuthorize("hasAnyRole('MAKER', 'PARTNER')")
  public ResponseEntity<BaseResponse<Void>> createVoucher(@RequestBody List<CreateVoucherExcel> request) {
    // TODO: call service
    return ResponseEntity.ok(BaseResponse.<Void>builder()
            .status(BaseErrorCode.ERROR.getErrorNumCode())
            .code(BaseErrorCode.ERROR.getErrorCode())
            .message(BaseErrorCode.ERROR.getErrorDescription()).build());
  }

  @PostMapping(value = "/excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasAnyRole('MAKER', 'PARTNER')")
  public ResponseEntity<BaseResponse<Void>> createVoucherByExcel(@Valid @ModelAttribute CreateUpdateInfoExcelRequest request) {
    // TODO: call service
    return ResponseEntity.ok(BaseResponse.<Void>builder()
            .status(BaseErrorCode.ERROR.getErrorNumCode())
            .code(BaseErrorCode.ERROR.getErrorCode())
            .message(BaseErrorCode.ERROR.getErrorDescription())
            .build());
    
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('MAKER', 'CHECKER', 'PARTNER')")
  public ResponseEntity<?> getVoucher(@RequestParam(name = "status", required = false) RequestStatus status,
                                      @RequestParam(name = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
                                      @RequestParam(name = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
                                      @RequestParam(name = "partnerId", required = false) String partnerId,
                                      @RequestParam(name = "requestType", required = false)DiscountType discountType,
                                      @PageableDefault(size = 20) Pageable pageable)   {
    // TODO: call service
    return ResponseEntity.ok().build();
  }

  @GetMapping("/{requestId}")
  @PreAuthorize("hasAnyRole('MAKER', 'CHECKER', 'PARTNER')")
  public ResponseEntity<?> getVoucherById(@PathVariable Long requestId) {
    // TODO: call service
    return ResponseEntity.ok().build();
  }

  @PutMapping("/{requestId}/submit")
  @PreAuthorize("hasAnyRole('MAKER', 'PARTNER')")
  public ResponseEntity<BaseResponse<Void>> submitVoucher(@PathVariable String requestId) {
    // TODO: call service
    return ResponseEntity.ok(BaseResponse.<Void>builder()
            .status(BaseErrorCode.ERROR.getErrorNumCode())
            .code(BaseErrorCode.ERROR.getErrorCode())
            .message(BaseErrorCode.ERROR.getErrorDescription()).build());
  }

  @PutMapping("/{requestId}/confirm")
  @PreAuthorize("hasRole('CHECKER')")
  public ResponseEntity<BaseResponse<Void>> confirmVoucher(@PathVariable String requestId,
      @RequestParam String action) {
    // TODO: call service - action: APPROVED / REJECTED
    return ResponseEntity.ok(BaseResponse.<Void>builder()
            .status(BaseErrorCode.ERROR.getErrorNumCode())
            .code(BaseErrorCode.ERROR.getErrorCode())
            .message(BaseErrorCode.ERROR.getErrorDescription()).build());
  }

  @PutMapping("/{requestId}/cancel")
  @PreAuthorize("hasAnyRole('MAKER', 'PARTNER')")
  public ResponseEntity<BaseResponse<Void>> cancelVoucher(@PathVariable String requestId) {
    // TODO: call service
    return ResponseEntity.ok(BaseResponse.<Void>builder()
            .status(BaseErrorCode.ERROR.getErrorNumCode())
            .code(BaseErrorCode.ERROR.getErrorCode())
            .message(BaseErrorCode.ERROR.getErrorDescription()).build());
  }
}
