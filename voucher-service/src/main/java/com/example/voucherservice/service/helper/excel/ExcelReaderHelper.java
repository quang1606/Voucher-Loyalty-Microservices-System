package com.example.voucherservice.service.helper.excel;

import com.example.common.BaseException;
import com.example.voucherservice.constant.DiscountType;
import com.example.voucherservice.dto.request.CreateVoucherExcel;
import io.micrometer.common.util.StringUtils;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Slf4j
public class ExcelReaderHelper {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final String[] EXPECTED_HEADERS = {
        "Voucher Name", "Description", "Customer Tier",
        "Voucher Purpose", "Discount Type", "Discount Value", "Max Discount",
        "Min Order Value", "Total Stock", "Max Collect", "Start Date", "End Date"
    };

    public List<CreateVoucherExcel> readExcel(MultipartFile file, DiscountType discountType) {
        List<CreateVoucherExcel> result = new ArrayList<>();
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            ExcelUtils.validateHeaders(sheet, EXPECTED_HEADERS);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (ExcelUtils.isRowEmpty(row) || isAllRequiredFieldsBlank(row, discountType)) {
                    continue;
                }

                try {
                    CreateVoucherExcel dto = parseCommonFields(row, i);
                    if (discountType == DiscountType.FIXED) {
                        parseFixedFields(dto, row, i);
                    } else {
                        parsePercentFields(dto, row, i);
                    }
                    result.add(dto);
                } catch (BaseException e) {
                    throw e;
                } catch (Exception e) {
                    log.error("Error parsing row {}: {}", i + 1, e.getMessage());
                    throw BaseException.builder()
                            .httpStatus(HttpStatus.BAD_REQUEST)
                            .errorCode("EXCEL_ROW_PARSE_ERROR")
                            .description("Row " + (i + 1) + ": " + e.getMessage())
                            .build();
                }
            }
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to read excel file: {}", e.getMessage(), e);
            throw BaseException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .errorCode("EXCEL_READ_ERROR")
                    .description("Failed to read excel file: " + e.getMessage())
                    .build();
        }
        return result;
    }

    private boolean isAllRequiredFieldsBlank(Row row, DiscountType discountType) {
        boolean commonBlank = StringUtils.isBlank(ExcelUtils.getCellValueAsString(row.getCell(0)))
                && StringUtils.isBlank(ExcelUtils.getCellValueAsString(row.getCell(5)))
                && StringUtils.isBlank(ExcelUtils.getCellValueAsString(row.getCell(8)))
                && StringUtils.isBlank(ExcelUtils.getCellValueAsString(row.getCell(10)))
                && StringUtils.isBlank(ExcelUtils.getCellValueAsString(row.getCell(11)));
        if (!commonBlank) {
            return false;
        }
        if (discountType == DiscountType.FIXED) {
            return StringUtils.isBlank(ExcelUtils.getCellValueAsString(row.getCell(7)));
        }
        return StringUtils.isBlank(ExcelUtils.getCellValueAsString(row.getCell(6)))
                && StringUtils.isBlank(ExcelUtils.getCellValueAsString(row.getCell(7)));
    }

    private CreateVoucherExcel parseCommonFields(Row row, int rowIndex) {
        CreateVoucherExcel dto = new CreateVoucherExcel();
        dto.setVoucherName(ExcelUtils.getCellValueAsString(row.getCell(0)));
        dto.setDescription(ExcelUtils.getCellValueAsString(row.getCell(1)));
        dto.setCustomerTier(ExcelUtils.getCellValueAsString(row.getCell(2)));
        dto.setVoucherPurpose(ExcelUtils.getCellValueAsString(row.getCell(3)));
        dto.setDiscountType(ExcelUtils.getCellValueAsString(row.getCell(4)));
        dto.setDiscountValue(parseBigDecimal(ExcelUtils.getCellValueAsString(row.getCell(5)), rowIndex, 5));
        dto.setTotalStock(parseInteger(ExcelUtils.getCellValueAsString(row.getCell(8)), rowIndex, 8));
        dto.setMaxCollect(parseInteger(ExcelUtils.getCellValueAsString(row.getCell(9)), rowIndex, 9));
        dto.setStartDate(parseDateTime(ExcelUtils.getCellValueAsString(row.getCell(10)), rowIndex, 10));
        dto.setEndDate(parseDateTime(ExcelUtils.getCellValueAsString(row.getCell(11)), rowIndex, 11));
        return dto;
    }

    private void parseFixedFields(CreateVoucherExcel dto, Row row, int rowIndex) {
        dto.setMinOrderValue(parseBigDecimal(ExcelUtils.getCellValueAsString(row.getCell(7)), rowIndex, 7));
        dto.setDiscountType(DiscountType.FIXED.name());
    }

    private void parsePercentFields(CreateVoucherExcel dto, Row row, int rowIndex) {
        dto.setMaxDiscount(parseBigDecimal(ExcelUtils.getCellValueAsString(row.getCell(6)), rowIndex, 6));
        dto.setMinOrderValue(parseBigDecimal(ExcelUtils.getCellValueAsString(row.getCell(7)), rowIndex, 7));
        dto.setDiscountType(DiscountType.PERCENT.name());
    }


    private BigDecimal parseBigDecimal(String value, int row, int col) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            log.warn("Invalid number at row={}, col={}: {}", row, col, value);
            return null;
        }
    }

    private Integer parseInteger(String value, int row, int col) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            log.warn("Invalid integer at row={}, col={}: {}", row, col, value);
            return null;
        }
    }

    private LocalDateTime parseDateTime(String value, int row, int col) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        try {
            return LocalDateTime.parse(value, DATE_FMT);
        } catch (Exception e) {
            log.warn("Invalid datetime at row={}, col={}: {}", row, col, value);
            return null;
        }
    }
}
