package com.naitei.group3.movie_ticket_booking_system.helper;

import org.apache.poi.ss.usermodel.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class ExcelHelper {

    public static final String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    public static boolean hasExcelFormat(String contentType) {
        return TYPE.equals(contentType);
    }

    public static String getStringCell(Row row, int idx) {
        if (row == null)
            return "";
        Cell cell = row.getCell(idx, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell).trim();
    }

    public static boolean isRowEmpty(Row row) {
        if (row == null)
            return true;
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (cell != null && new DataFormatter().formatCellValue(cell).trim().length() > 0) {
                return false;
            }
        }
        return true;
    }

    public static Long getLongCell(Row row, int idx) {
        String value = getStringCell(row, idx);
        try {
            return value.isEmpty() ? null : Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Double getDoubleCell(Row row, int idx) {
        String value = getStringCell(row, idx);
        try {
            return value.isEmpty() ? null : Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Integer getIntegerCell(Row row, int idx) {
        String value = getStringCell(row, idx);
        try {
            return value.isEmpty() ? null : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static BigDecimal getBigDecimalCell(Row row, int idx) {
        String value = getStringCell(row, idx);
        try {
            return value.isEmpty() ? null : new BigDecimal(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Timestamp getTimestampCell(Row row, int idx) {
        Cell cell = row.getCell(idx, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        if (cell == null)
            return null;
        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                return new Timestamp(cell.getDateCellValue().getTime());
            }
            String value = getStringCell(row, idx);
            if (value.isEmpty())
                return null;
            // ISO 8601 or yyyy-MM-dd HH:mm:ss
            return Timestamp.valueOf(value.replace('T', ' '));
        } catch (Exception e) {
            return null;
        }
    }

    public static LocalDateTime getLocalDateTimeCell(Row row, int idx) {
        Timestamp ts = getTimestampCell(row, idx);
        return ts != null ? ts.toLocalDateTime() : null;
    }
}
