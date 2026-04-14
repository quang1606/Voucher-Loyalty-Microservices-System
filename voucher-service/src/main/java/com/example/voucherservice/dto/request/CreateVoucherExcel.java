package com.example.voucherservice.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateVoucherExcel {

  private String voucherName;
  private String description;
  private BigDecimal discountValue;
  private BigDecimal maxDiscount;
  private BigDecimal minOrderValue;
  private Integer totalStock;
  private Integer maxCollect;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
}
