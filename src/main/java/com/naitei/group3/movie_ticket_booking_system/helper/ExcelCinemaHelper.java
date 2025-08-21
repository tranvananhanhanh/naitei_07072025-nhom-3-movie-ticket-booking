package com.naitei.group3.movie_ticket_booking_system.helper;

import com.naitei.group3.movie_ticket_booking_system.entity.Cinema;
import com.naitei.group3.movie_ticket_booking_system.dto.response.ExcelErrorDTO;
import com.naitei.group3.movie_ticket_booking_system.exception.ExcelValidationException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

public class ExcelCinemaHelper {

    private static final int MIN_COLUMNS = 5; // Name, Address, City, URL, ...

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
            if (rows.hasNext())
                rows.next(); // Skip header

            while (rows.hasNext()) {
                Row row = rows.next();
                int rowNumber = row.getRowNum() + 1;

                if (ExcelHelper.isRowEmpty(row)) {
                    continue;
                }

                List<String> errorMessages = new ArrayList<>();
                Cinema cinema = parseCinemaRow(row, errorMessages);

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

    private static Cinema parseCinemaRow(Row row, List<String> errorMessages) {
        if (row.getLastCellNum() < MIN_COLUMNS) {
            errorMessages.add("Missing columns! At least " + MIN_COLUMNS + " columns are required.");
            return new Cinema();
        }

        Cinema cinema = new Cinema();

        // Name
        String name = ExcelHelper.getStringCell(row, 1);
        if (name.isEmpty())
            errorMessages.add("Name is required");
        cinema.setName(name);

        // Address
        String address = ExcelHelper.getStringCell(row, 2);
        if (address.isEmpty())
            errorMessages.add("Address is required");
        cinema.setAddress(address);

        // City
        String city = ExcelHelper.getStringCell(row, 3);
        if (city.isEmpty())
            errorMessages.add("City is required");
        cinema.setCity(city);

        // URL
        String url = ExcelHelper.getStringCell(row, 4);
        if (url.isEmpty())
            errorMessages.add("URL is required");
        cinema.setMapUrl(url);

        return cinema;
    }
}
