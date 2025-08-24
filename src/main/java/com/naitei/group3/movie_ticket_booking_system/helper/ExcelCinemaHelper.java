package com.naitei.group3.movie_ticket_booking_system.helper;

import com.naitei.group3.movie_ticket_booking_system.entity.Cinema;
import com.naitei.group3.movie_ticket_booking_system.dto.response.ExcelErrorDTO;
import com.naitei.group3.movie_ticket_booking_system.exception.ExcelValidationException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

public class ExcelCinemaHelper {

    private static final int MIN_COLUMNS = 4; // name, address, city , url

    public static boolean hasExcelFormat(MultipartFile file) {
        return ExcelHelper.hasExcelFormat(file.getContentType());
    }

    public static List<Cinema> excelToCinemas(InputStream is) throws ExcelValidationException {
        List<Cinema> cinemas = new ArrayList<>();
        List<ExcelErrorDTO> errors = new ArrayList<>();
        Set<String> uniqueKeys = new HashSet<>();
        try (Workbook workbook = WorkbookFactory.create(is)) {
            Sheet sheet = workbook.getNumberOfSheets() > 0 ? workbook.getSheetAt(0) : null;
            if (sheet == null) {
                throw new RuntimeException("No sheet found in Excel file");
            }
            Iterator<Row> rows = sheet.iterator();
            if (!rows.hasNext()) {
                throw new RuntimeException("No header row found in Excel file");
            }
            Row headerRow = rows.next();
            Map<String, Integer> colIndexMap = new HashMap<>();
            for (Cell cell : headerRow) {
                colIndexMap.put(cell.getStringCellValue().trim().toLowerCase(), cell.getColumnIndex());
            }

            List<String> requiredColumns = List.of("name", "address", "city", "map_url");
            for (String col : requiredColumns) {
                if (!colIndexMap.containsKey(col)) {
                    errors.add(new ExcelErrorDTO(headerRow.getRowNum() + 1, "Missing required column: " + col));
                }
            }
            if (!errors.isEmpty()) {
                throw new ExcelValidationException(errors);
            }

            while (rows.hasNext()) {
                Row row = rows.next();
                int rowNumber = row.getRowNum() + 1;

                if (ExcelHelper.isRowEmpty(row)) {
                    continue;
                }

                List<String> errorMessages = new ArrayList<>();
                Cinema cinema = parseCinemaRow(row, errorMessages, colIndexMap);

                String key = (cinema.getName() + "|" + cinema.getAddress() + "|" + cinema.getCity()).toLowerCase();
                if (!uniqueKeys.add(key)) {
                    errorMessages.add("Duplicate cinema in Excel file (name, address, city): " + cinema.getName());
                }

                if (!errorMessages.isEmpty()) {
                    errors.add(new ExcelErrorDTO(rowNumber, String.join("; ", errorMessages)));
                } else {
                    cinemas.add(cinema);
                }
            }
        } catch (ExcelValidationException e) {
            throw e;
        } catch (Exception e) {
            errors.add(new ExcelErrorDTO(0, "Fail to parse Excel file: " + e.getMessage()));
            throw new ExcelValidationException(errors);
        }

        if (!errors.isEmpty()) {
            throw new ExcelValidationException(errors);
        }
        return cinemas;
    }

    private static Cinema parseCinemaRow(Row row, List<String> errorMessages, Map<String, Integer> colIndexMap) {
        Cinema cinema = new Cinema();

        // Name
        String name = ExcelHelper.getStringCell(row, colIndexMap.get("name"));
        if (name.isEmpty())
            errorMessages.add("Name is required");
        cinema.setName(name);

        // Address
        String address = ExcelHelper.getStringCell(row, colIndexMap.get("address"));
        if (address.isEmpty())
            errorMessages.add("Address is required");
        cinema.setAddress(address);

        // City
        String city = ExcelHelper.getStringCell(row, colIndexMap.get("city"));
        if (city.isEmpty())
            errorMessages.add("City is required");
        cinema.setCity(city);

        // Map URL
        String url = ExcelHelper.getStringCell(row, colIndexMap.get("map_url"));
        if (url.isEmpty())
            errorMessages.add("URL is required");
        cinema.setMapUrl(url);

        return cinema;
    }
}
