package com.naitei.group3.movie_ticket_booking_system.helper;

import com.naitei.group3.movie_ticket_booking_system.dto.response.ExcelErrorDTO;
import com.naitei.group3.movie_ticket_booking_system.dto.response.ShowtimeImportDTO;
import com.naitei.group3.movie_ticket_booking_system.entity.Showtime;
import com.naitei.group3.movie_ticket_booking_system.enums.ShowtimeStatus;
import com.naitei.group3.movie_ticket_booking_system.exception.ExcelValidationException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class ExcelShowtimeHelper {

    public static boolean hasExcelFormat(MultipartFile file) {
        return ExcelHelper.hasExcelFormat(file.getContentType());
    }

    public static List<ShowtimeImportDTO> excelToShowtimes(InputStream is, Set<Long> validMovieIds,
            Set<Long> validHallIds) throws ExcelValidationException {
        List<ShowtimeImportDTO> showtimes = new ArrayList<>();
        List<ExcelErrorDTO> errors = new ArrayList<>();
        Set<String> uniqueShowtimes = new HashSet<>();
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

            List<String> requiredColumns = List.of("movie_id", "hall_id", "start_time", "end_time", "price", "status");
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

                if (row.getLastCellNum() < requiredColumns.size()) {
                    errors.add(new ExcelErrorDTO(rowNumber, "Missing columns at row " + rowNumber));
                    continue;
                }

                if (ExcelHelper.isRowEmpty(row))
                    continue;

                List<String> errorMessages = new ArrayList<>();
                ShowtimeImportDTO dto = parseShowtimeRow(row, errorMessages, validMovieIds, validHallIds, colIndexMap);

                if (!errorMessages.isEmpty()) {
                    errors.add(new ExcelErrorDTO(rowNumber, String.join("; ", errorMessages)));
                } else if (dto != null) {
                    String key = dto.movieId() + "-" + dto.hallId() + "-" + dto.showtime().getStartTime();
                    if (!uniqueShowtimes.add(key)) {
                        errors.add(new ExcelErrorDTO(rowNumber,
                                "Duplicate showtime in import file: movie, hall, and start time already exist in file"));
                        continue;
                    }
                    showtimes.add(dto);
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
        return showtimes;
    }

    private static ShowtimeImportDTO parseShowtimeRow(Row row, List<String> errorMessages, Set<Long> validMovieIds,
            Set<Long> validHallIds, Map<String, Integer> colIndexMap) {

        Long movieId = ExcelHelper.getLongCell(row, colIndexMap.get("movie_id"));
        if (movieId == null) {
            errorMessages.add("Movie ID is required");
        } else if (!validMovieIds.contains(movieId)) {
            errorMessages.add("Movie ID " + movieId + " does not exist");
        }

        Long hallId = ExcelHelper.getLongCell(row, colIndexMap.get("hall_id"));
        if (hallId == null) {
            errorMessages.add("Hall ID is required");
        } else if (!validHallIds.contains(hallId)) {
            errorMessages.add("Hall ID " + hallId + " does not exist");
        }

        LocalDateTime startTime = ExcelHelper.getLocalDateTimeCell(row, colIndexMap.get("start_time"));
        if (startTime == null) {
            errorMessages.add("Start time is required");
        }

        LocalDateTime endTime = ExcelHelper.getLocalDateTimeCell(row, colIndexMap.get("end_time"));
        if (endTime == null) {
            errorMessages.add("End time is required");
        }

        if (startTime != null && endTime != null && !startTime.isBefore(endTime)) {
            errorMessages.add("Start time must be before end time");
        }

        if (startTime != null) {
            int startHour = startTime.getHour();
            int startMinute = startTime.getMinute();
            if (startHour < 9 || (startHour == 22 && startMinute > 30) || startHour > 22) {
                errorMessages.add("Start time must be between 09:00 and 22:30");
            }
        }
        if (endTime != null) {
            int endHour = endTime.getHour();
            int endMinute = endTime.getMinute();
            if (endHour < 9 || (endHour == 22 && endMinute > 30) || endHour > 22) {
                errorMessages.add("End time must be between 09:00 and 22:30");
            }
        }

        BigDecimal price = ExcelHelper.getBigDecimalCell(row, colIndexMap.get("price"));
        if (price == null) {
            errorMessages.add("Price is required");
        } else if (price.compareTo(BigDecimal.ZERO) <= 0) {
            errorMessages.add("Price must be greater than 0");
        }

        Integer status = ExcelHelper.getIntegerCell(row, colIndexMap.get("status"));

        if (!errorMessages.isEmpty()) {
            return null;
        }

        Showtime showtime = new Showtime();
        showtime.setStartTime(startTime);
        showtime.setEndTime(endTime);
        showtime.setPrice(price);
        ShowtimeStatus showtimeStatus = ShowtimeStatus.AVAILABLE;
        if (status != null) {
            showtimeStatus = ShowtimeStatus.fromValue(status);
        }
        showtime.setStatus(showtimeStatus);

        return new ShowtimeImportDTO(showtime, movieId, hallId);
    }
}
