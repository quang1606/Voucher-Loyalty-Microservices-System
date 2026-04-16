package com.example.voucherservice.service.helper.excel;

import com.example.common.BaseException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.http.HttpStatus;

@Slf4j
public final class ExcelUtils {

    public static boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }
        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String value = getCellValueAsString(cell);
                if (value != null && !value.trim().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    public static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toString();
                }
                double numVal = cell.getNumericCellValue();
                if (numVal == Math.floor(numVal) && !Double.isInfinite(numVal)) {
                    return String.valueOf((long) numVal);
                }
                return String.valueOf(numVal);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue().trim();
                } catch (Exception e) {
                    try {
                        return String.valueOf(cell.getNumericCellValue());
                    } catch (Exception ex) {
                        log.warn("Cannot evaluate formula at cell [{},{}]",
                                cell.getRowIndex(), cell.getColumnIndex());
                        return null;
                    }
                }
            case BLANK:
            default:
                return null;
        }
    }

    public static Long getCellValueAsLong(Cell cell) {
        if (cell == null) {
            return null;
        }
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return (long) cell.getNumericCellValue();
            }
            String strVal = getCellValueAsString(cell);
            return strVal != null ? Long.parseLong(strVal.trim()) : null;
        } catch (NumberFormatException e) {
            log.warn("Cannot parse Long at cell [{},{}]: {}",
                    cell.getRowIndex(), cell.getColumnIndex(), e.getMessage());
            return null;
        }
    }

  public static void validateHeaders(Sheet sheet, String[] expectedHeaders) {
    Row headerRow = sheet.getRow(0);

    if (headerRow == null) {
      throw BaseException.builder()
          .httpStatus(HttpStatus.BAD_REQUEST)
          .errorCode("INVALID_EXCEL_FILE")
          .description("Excel file is missing header row")
          .build();
    }

    int actualHeaderCount = 0;
    for (int i = 0; i < headerRow.getLastCellNum(); i++) {
      Cell cell = headerRow.getCell(i);
      String value = getCellValueAsString(cell);
      if (value != null && !value.trim().isEmpty()) {
        actualHeaderCount++;
      }
    }

    if (actualHeaderCount != expectedHeaders.length) {
      throw BaseException.builder()
          .httpStatus(HttpStatus.BAD_REQUEST)
          .errorCode("INVALID_EXCEL_FILE")
          .description("Invalid excel format")
          .build();
    }

    boolean isMatched = true;

    for (int i = 0; i < expectedHeaders.length; i++) {
      Cell cell = headerRow.getCell(i);
      String actualHeader = ExcelUtils.getCellValueAsString(cell);

      if (actualHeader == null) {
        isMatched = false;
      } else {
        String normalizedActual = actualHeader.trim().replaceAll("\\s+", " ");
        String normalizedExpected = expectedHeaders[i].trim().replaceAll("\\s+", " ");

        if (!normalizedActual.equalsIgnoreCase(normalizedExpected)) {
          isMatched = false;
        }
      }
    }

    if (!isMatched) {
      throw BaseException.builder()
          .httpStatus(HttpStatus.BAD_REQUEST)
          .errorCode("INVALID_EXCEL_FILE")
          .description("Invalid excel format")
          .build();
    }

    log.info("Excel header validation passed successfully");
  }
}
