package com.example.voucherservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class VoucherRequestResponsePage {
  private  List<VoucherRequestResponse> data;
  private long totalElements;
  private int totalPages;
  private int page;
  private int size;
}
