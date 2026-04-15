package com.example.voucherservice.controller;

import com.example.common.BaseErrorCode;
import com.example.common.BaseResponse;
import com.example.voucherservice.constant.DiscountType;
import com.example.voucherservice.constant.RequestMode;
import com.example.voucherservice.constant.RequestStatus;
import com.example.voucherservice.dto.request.CreateVoucherExcelRequest;
import com.example.voucherservice.dto.request.CreateVoucherExcel;

import com.example.voucherservice.dto.request.CreateVoucherRequest;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.Valid;
import com.example.voucherservice.service.VoucherService;
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

  private final VoucherService voucherService;

  @PostMapping
  @PreAuthorize("hasAnyRole('MAKER', 'PARTNER')")
  public ResponseEntity<BaseResponse<Void>> createVoucher( @Valid @RequestBody CreateVoucherRequest request) {
    voucherService.createVoucher(request);
    return ResponseEntity.ok(BaseResponse.<Void>builder()
            .status(BaseErrorCode.SUCCESS.getErrorNumCode())
            .code(BaseErrorCode.SUCCESS.getErrorCode())
            .message(BaseErrorCode.SUCCESS.getErrorDescription()).build());
  }

  @PostMapping(value = "/excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasAnyRole('MAKER', 'PARTNER')")
  public ResponseEntity<BaseResponse<Void>> createVoucherByExcel(@Valid @ModelAttribute CreateVoucherExcelRequest request) {
    // TODO: call service
    return ResponseEntity.ok(BaseResponse.<Void>builder()
        .status(BaseErrorCode.SUCCESS.getErrorNumCode())
        .code(BaseErrorCode.SUCCESS.getErrorCode())
        .message(BaseErrorCode.SUCCESS.getErrorDescription()).build());

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

  @PutMapping("/{id}/submit")
  @PreAuthorize("hasAnyRole('MAKER', 'PARTNER')")
  public ResponseEntity<BaseResponse<Void>> submitVoucher(@PathVariable Long id) {
    voucherService.submitVoucher(id);
    return ResponseEntity.ok(BaseResponse.<Void>builder()
        .status(BaseErrorCode.SUCCESS.getErrorNumCode())
        .code(BaseErrorCode.SUCCESS.getErrorCode())
        .message(BaseErrorCode.SUCCESS.getErrorDescription()).build());
  }

  @PutMapping("/{id}/confirm")
  @PreAuthorize("hasRole('CHECKER')")
  public ResponseEntity<BaseResponse<Void>> confirmVoucher(@PathVariable Long id,
      @RequestParam String action) {
    voucherService.confirmVoucher(id, action);
    return ResponseEntity.ok(BaseResponse.<Void>builder()
        .status(BaseErrorCode.SUCCESS.getErrorNumCode())
        .code(BaseErrorCode.SUCCESS.getErrorCode())
        .message(BaseErrorCode.SUCCESS.getErrorDescription()).build());
  }

  @PutMapping("/{id}/cancel")
  @PreAuthorize("hasAnyRole('MAKER', 'PARTNER')")
  public ResponseEntity<BaseResponse<Void>> cancelVoucher(@PathVariable Long id) {
    voucherService.cancelVoucher(id);
    return ResponseEntity.ok(BaseResponse.<Void>builder()
        .status(BaseErrorCode.SUCCESS.getErrorNumCode())
        .code(BaseErrorCode.SUCCESS.getErrorCode())
        .message(BaseErrorCode.SUCCESS.getErrorDescription()).build());
  }
}
