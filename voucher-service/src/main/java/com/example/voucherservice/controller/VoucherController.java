package com.example.voucherservice.controller;

import com.example.common.BaseErrorCode;
import com.example.common.BaseResponse;
import com.example.voucherservice.constant.CreatorType;
import com.example.voucherservice.constant.CustomerTier;
import com.example.voucherservice.constant.DiscountType;
import com.example.voucherservice.constant.RequestMode;
import com.example.voucherservice.constant.RequestStatus;
import com.example.voucherservice.constant.VoucherPurpose;
import com.example.voucherservice.constant.VoucherStatus;
import com.example.voucherservice.dto.request.ConfirmVoucherRequest;
import com.example.voucherservice.dto.request.CreateVoucherExcelRequest;
import com.example.voucherservice.dto.request.CreateVoucherRequest;
import com.example.voucherservice.dto.response.VoucherDetailResponsePage;
import com.example.voucherservice.dto.response.VoucherRequestResponse;
import com.example.voucherservice.dto.response.VoucherRequestResponsePage;
import com.example.voucherservice.service.VoucherService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    voucherService.createVoucherByExcel(request);
    return ResponseEntity.ok(BaseResponse.<Void>builder()
        .status(BaseErrorCode.SUCCESS.getErrorNumCode())
        .code(BaseErrorCode.SUCCESS.getErrorCode())
        .message(BaseErrorCode.SUCCESS.getErrorDescription()).build());

  }

  @GetMapping
  @PreAuthorize("hasAnyRole('MAKER', 'CHECKER', 'PARTNER')")
  public ResponseEntity<BaseResponse<VoucherRequestResponsePage>>getVoucher(
      @RequestParam(name = "status", required = false) RequestStatus status,
      @RequestParam(name = "requestMode", required = false) RequestMode requestMode,
      @RequestParam(name = "creatorType", required = false) CreatorType creatorType,
      @RequestParam(name = "voucherPurpose", required = false) VoucherPurpose voucherPurpose,
      @RequestParam(name = "storeName", required = false) String storeName,
      @RequestParam(name = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
      @RequestParam(name = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
      @PageableDefault(size = 20) Pageable pageable) {
    VoucherRequestResponsePage data = voucherService.getVouchers(
        status, requestMode, creatorType, voucherPurpose, storeName,
        fromDate, toDate, pageable);
    return ResponseEntity.ok(BaseResponse.<VoucherRequestResponsePage>builder()
        .status(BaseErrorCode.SUCCESS.getErrorNumCode())
        .code(BaseErrorCode.SUCCESS.getErrorCode())
        .message(BaseErrorCode.SUCCESS.getErrorDescription())
        .data(data).build());
  }

  @GetMapping("/details")
  @PreAuthorize("hasAnyRole('MAKER', 'CHECKER', 'PARTNER')")
  public ResponseEntity<BaseResponse<VoucherDetailResponsePage>> getAllVoucherDetails(
      @RequestParam(name = "creatorType", required = false) CreatorType creatorType,
      @RequestParam(name = "customerTier", required = false) CustomerTier customerTier,
      @RequestParam(name = "discountType", required = false) DiscountType discountType,
      @RequestParam(name = "voucherPurpose", required = false) VoucherPurpose voucherPurpose,
      @RequestParam(name = "voucherStatus", required = false) VoucherStatus voucherStatus,
      @RequestParam(name = "storeName", required = false) String storeName,
      @RequestParam(name = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
      @RequestParam(name = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
      @PageableDefault(size = 20) Pageable pageable) {
    VoucherDetailResponsePage data = voucherService.getAllVoucherDetails(
        creatorType, customerTier, discountType, voucherPurpose, voucherStatus,
        storeName, fromDate, toDate, pageable);
    return ResponseEntity.ok(BaseResponse.<VoucherDetailResponsePage>builder()
        .status(BaseErrorCode.SUCCESS.getErrorNumCode())
        .code(BaseErrorCode.SUCCESS.getErrorCode())
        .message(BaseErrorCode.SUCCESS.getErrorDescription())
        .data(data).build());
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('MAKER', 'CHECKER', 'PARTNER')")
  public ResponseEntity<BaseResponse<VoucherRequestResponse>> getVoucherById(
      @PathVariable Long id,
      @RequestParam(name = "voucherName", required = false) String voucherName,
      @RequestParam(name = "status", required = false) RequestStatus status,
      @PageableDefault(size = 20) Pageable pageable) {
    VoucherRequestResponse data = voucherService.getVoucherById(id, voucherName, status, pageable);
    return ResponseEntity.ok(BaseResponse.<VoucherRequestResponse>builder()
        .status(BaseErrorCode.SUCCESS.getErrorNumCode())
        .code(BaseErrorCode.SUCCESS.getErrorCode())
        .message(BaseErrorCode.SUCCESS.getErrorDescription())
        .data(data).build());
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
      @Valid @RequestBody ConfirmVoucherRequest request) {
    voucherService.confirmVoucher(id, request);
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
