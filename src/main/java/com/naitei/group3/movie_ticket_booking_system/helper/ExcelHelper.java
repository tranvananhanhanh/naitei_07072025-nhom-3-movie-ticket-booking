package com.naitei.group3.movie_ticket_booking_system.helper;

import org.apache.poi.ss.usermodel.*;

public class ExcelHelper {

    public static final String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    public static boolean hasExcelFormat(String contentType) {
        return TYPE.equals(contentType);
    }

    public static String getStringCell(Row row, int idx) {
        Cell cell = row.getCell(idx, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell).trim();
    }

    public static boolean isRowEmpty(Row row) {
        if (row == null) return true;
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (cell != null && new DataFormatter().formatCellValue(cell).trim().length() > 0) {
                return false;
            }
        }
        return true;
    }
}
