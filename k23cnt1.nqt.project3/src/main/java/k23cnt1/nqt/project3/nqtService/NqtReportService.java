package k23cnt1.nqt.project3.nqtService;

import k23cnt1.nqt.project3.nqtDto.NqtDatPhongResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class NqtReportService {

    public byte[] generateExcelReport(
            Double tongDoanhThu,
            Integer revenueGrowth,
            Integer newBookingsCount,
            Integer occupiedRooms,
            Integer vacantRooms,
            Double[] monthlyRevenue,
            List<NqtDatPhongResponse> recentBookings) {

        try (Workbook workbook = new XSSFWorkbook()) {
            // Create Summary Sheet
            createSummarySheet(workbook, tongDoanhThu, revenueGrowth, newBookingsCount, occupiedRooms, vacantRooms);

            // Create Monthly Revenue Sheet
            createMonthlyRevenueSheet(workbook, monthlyRevenue);

            // Create Recent Bookings Sheet
            createRecentBookingsSheet(workbook, recentBookings);

            // Write to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo báo cáo Excel: " + e.getMessage(), e);
        }
    }

    private void createSummarySheet(Workbook workbook, Double tongDoanhThu, Integer revenueGrowth,
            Integer newBookingsCount, Integer occupiedRooms, Integer vacantRooms) {
        Sheet sheet = workbook.createSheet("Tổng quan");

        // Create styles
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle labelStyle = createLabelStyle(workbook);
        CellStyle valueStyle = createValueStyle(workbook);

        int rowNum = 0;

        // Title
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("BÁO CÁO HOẠT ĐỘNG KHÁCH SẠN");
        titleCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 1));

        // Date
        Row dateRow = sheet.createRow(rowNum++);
        Cell dateCell = dateRow.createCell(0);
        dateCell.setCellValue(
                "Ngày xuất báo cáo: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(1, 1, 0, 1));

        rowNum++; // Empty row

        // Statistics
        addStatRow(sheet, rowNum++, "Tổng doanh thu", String.format("%,.0f VNĐ", tongDoanhThu), labelStyle, valueStyle);
        addStatRow(sheet, rowNum++, "Tăng trưởng doanh thu", revenueGrowth + "%", labelStyle, valueStyle);
        addStatRow(sheet, rowNum++, "Đặt phòng mới (tháng này)", newBookingsCount.toString(), labelStyle, valueStyle);
        addStatRow(sheet, rowNum++, "Phòng đã đặt", occupiedRooms.toString(), labelStyle, valueStyle);
        addStatRow(sheet, rowNum++, "Phòng trống", vacantRooms.toString(), labelStyle, valueStyle);
        addStatRow(sheet, rowNum++, "Tổng số phòng", (occupiedRooms + vacantRooms) + "", labelStyle, valueStyle);

        // Auto-size columns
        sheet.setColumnWidth(0, 8000);
        sheet.setColumnWidth(1, 6000);
    }

    private void createMonthlyRevenueSheet(Workbook workbook, Double[] monthlyRevenue) {
        Sheet sheet = workbook.createSheet("Doanh thu theo tháng");

        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle valueStyle = createValueStyle(workbook);

        String[] months = { "Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6",
                "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12" };

        // Header
        Row headerRow = sheet.createRow(0);
        Cell monthHeader = headerRow.createCell(0);
        monthHeader.setCellValue("Tháng");
        monthHeader.setCellStyle(headerStyle);

        Cell revenueHeader = headerRow.createCell(1);
        revenueHeader.setCellValue("Doanh thu (VNĐ)");
        revenueHeader.setCellStyle(headerStyle);

        // Data
        for (int i = 0; i < 12; i++) {
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(months[i]);
            Cell valueCell = row.createCell(1);
            valueCell.setCellValue(monthlyRevenue[i]);
            valueCell.setCellStyle(valueStyle);
        }

        // Total row
        Row totalRow = sheet.createRow(13);
        Cell totalLabel = totalRow.createCell(0);
        totalLabel.setCellValue("TỔNG CỘNG");
        totalLabel.setCellStyle(headerStyle);

        Cell totalValue = totalRow.createCell(1);
        double total = 0;
        for (Double revenue : monthlyRevenue) {
            total += revenue;
        }
        totalValue.setCellValue(total);
        totalValue.setCellStyle(headerStyle);

        sheet.setColumnWidth(0, 4000);
        sheet.setColumnWidth(1, 6000);
    }

    private void createRecentBookingsSheet(Workbook workbook, List<NqtDatPhongResponse> bookings) {
        Sheet sheet = workbook.createSheet("Đặt phòng gần đây");

        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle valueStyle = createValueStyle(workbook);

        // Header
        Row headerRow = sheet.createRow(0);
        String[] headers = { "Mã ĐP", "Khách hàng", "Phòng", "Ngày đến", "Ngày đi", "Trạng thái", "Tổng tiền (VNĐ)" };
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Data - limit to 20 recent bookings
        int maxRows = Math.min(bookings.size(), 20);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (int i = 0; i < maxRows; i++) {
            NqtDatPhongResponse booking = bookings.get(i);
            Row row = sheet.createRow(i + 1);

            row.createCell(0).setCellValue("#" + booking.getNqtId());
            row.createCell(1).setCellValue(booking.getNqtTenNguoiDung());
            row.createCell(2).setCellValue(booking.getNqtSoPhong());
            row.createCell(3)
                    .setCellValue(booking.getNqtNgayDen() != null ? booking.getNqtNgayDen().format(dateFormatter) : "");
            row.createCell(4)
                    .setCellValue(booking.getNqtNgayDi() != null ? booking.getNqtNgayDi().format(dateFormatter) : "");

            String status = "";
            if (booking.getNqtStatus() == 0)
                status = "Chờ thanh toán";
            else if (booking.getNqtStatus() == 1)
                status = "Đã thanh toán";
            else if (booking.getNqtStatus() == 2)
                status = "Hủy/Hoàn tiền";
            row.createCell(5).setCellValue(status);

            Cell totalCell = row.createCell(6);
            totalCell.setCellValue(booking.getNqtTongTien() != null ? booking.getNqtTongTien() : 0);
            totalCell.setCellStyle(valueStyle);
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.setColumnWidth(i, 4000);
        }
    }

    private void addStatRow(Sheet sheet, int rowNum, String label, String value, CellStyle labelStyle,
            CellStyle valueStyle) {
        Row row = sheet.createRow(rowNum);
        Cell labelCell = row.createCell(0);
        labelCell.setCellValue(label);
        labelCell.setCellStyle(labelStyle);

        Cell valueCell = row.createCell(1);
        valueCell.setCellValue(value);
        valueCell.setCellStyle(valueStyle);
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createLabelStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private CellStyle createValueStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));
        return style;
    }
}
