package com.example.voucherservice.dto.response;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MissionResponse {
  private List<MissionResponseDetail> data;
  private long totalElements;
  private int totalPages;
  private int page;
  private int size;
  }
