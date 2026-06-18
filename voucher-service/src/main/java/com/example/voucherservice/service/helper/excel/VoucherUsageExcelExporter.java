package com.example.voucherservice.service.helper.excel;

import com.example.voucherservice.dto.response.VoucherUsageStatsPageResponse;
import com.example.voucherservice.dto.response.VoucherUsageStatsResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class VoucherUsageExcelExporter {

    private static final String[] HEADERS = {
        "STT", "Mã yêu cầu", "Mã voucher", "Tên voucher", "Cửa hàng", "Lượt sử dụng", "Tổng tiền giảm giá"
    };

    public static byte[] export(VoucherUsageStatsPageResponse data) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Thống kê sử dụng voucher");

            // Header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            // Data style
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setAlignment(HorizontalAlignment.LEFT);
            dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            // Number style (right-aligned)
            CellStyle numberStyle = workbook.createCellStyle();
            numberStyle.setAlignment(HorizontalAlignment.RIGHT);
            numberStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            numberStyle.setBorderBottom(BorderStyle.THIN);
            numberStyle.setBorderTop(BorderStyle.THIN);
            numberStyle.setBorderLeft(BorderStyle.THIN);
            numberStyle.setBorderRight(BorderStyle.THIN);
            numberStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));

            // Currency style
            CellStyle currencyStyle = workbook.createCellStyle();
            currencyStyle.setAlignment(HorizontalAlignment.RIGHT);
            currencyStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            currencyStyle.setBorderBottom(BorderStyle.THIN);
            currencyStyle.setBorderTop(BorderStyle.THIN);
            currencyStyle.setBorderLeft(BorderStyle.THIN);
            currencyStyle.setBorderRight(BorderStyle.THIN);
            currencyStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));

            // Summary style
            CellStyle summaryStyle = workbook.createCellStyle();
            Font summaryFont = workbook.createFont();
            summaryFont.setBold(true);
            summaryStyle.setFont(summaryFont);
            summaryStyle.setAlignment(HorizontalAlignment.LEFT);

            // Write summary row
            Row summaryRow1 = sheet.createRow(0);
            Cell summaryCell1 = summaryRow1.createCell(0);
            summaryCell1.setCellValue("Tổng số yêu cầu: " + data.getTotalRequestCount());
            summaryCell1.setCellStyle(summaryStyle);

            Row summaryRow2 = sheet.createRow(1);
            Cell summaryCell2 = summaryRow2.createCell(0);
            summaryCell2.setCellValue("Tổng voucher đã sử dụng: " + data.getTotalVoucherUsed());
            summaryCell2.setCellStyle(summaryStyle);

            Row summaryRow3 = sheet.createRow(2);
            Cell summaryCell3 = summaryRow3.createCell(0);
            summaryCell3.setCellValue("Tổng tiền giảm giá: " + data.getTotalDiscountAmount().toPlainString() + " VND");
            summaryCell3.setCellStyle(summaryStyle);

            // Write header row
            int headerRowIdx = 4;
            Row headerRow = sheet.createRow(headerRowIdx);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }

            // Write data rows
            List<VoucherUsageStatsResponse> details = data.getDetails();
            for (int i = 0; i < details.size(); i++) {
                VoucherUsageStatsResponse item = details.get(i);
                Row row = sheet.createRow(headerRowIdx + 1 + i);

                Cell sttCell = row.createCell(0);
                sttCell.setCellValue(i + 1);
                sttCell.setCellStyle(numberStyle);

                Cell reqIdCell = row.createCell(1);
                reqIdCell.setCellValue(item.getRequestId() != null ? item.getRequestId() : "");
                reqIdCell.setCellStyle(dataStyle);

                Cell codeCell = row.createCell(2);
                codeCell.setCellValue(item.getVoucherCode() != null ? item.getVoucherCode() : "");
                codeCell.setCellStyle(dataStyle);

                Cell nameCell = row.createCell(3);
                nameCell.setCellValue(item.getVoucherName() != null ? item.getVoucherName() : "");
                nameCell.setCellStyle(dataStyle);

                Cell storeCell = row.createCell(4);
                storeCell.setCellValue(item.getStoreName() != null ? item.getStoreName() : "");
                storeCell.setCellStyle(dataStyle);

                Cell usedCell = row.createCell(5);
                usedCell.setCellValue(item.getUsedCount());
                usedCell.setCellStyle(numberStyle);

                Cell amountCell = row.createCell(6);
                amountCell.setCellValue(item.getTotalDiscountAmount() != null ? item.getTotalDiscountAmount().doubleValue() : 0);
                amountCell.setCellStyle(currencyStyle);
            }

            // Auto-size columns
            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
                // Add a bit of padding
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 512);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }
}
