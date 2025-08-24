package com.naitei.group3.movie_ticket_booking_system.helper;

import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;
import com.naitei.group3.movie_ticket_booking_system.entity.Genre;
import com.naitei.group3.movie_ticket_booking_system.entity.Movie;
import com.naitei.group3.movie_ticket_booking_system.repository.GenreRepository;
import com.naitei.group3.movie_ticket_booking_system.dto.response.ExcelErrorDTO;
import com.naitei.group3.movie_ticket_booking_system.exception.ExcelValidationException;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class ExcelMovieHelper {

    public static final int MIN_COLUMNS = 7; // name, description, duration, poster, release_date, is_active, genres

    public static boolean hasExcelFormat(MultipartFile file) {
        return ExcelHelper.hasExcelFormat(file.getContentType());
    }

    public static List<Movie> excelToMovies(InputStream is, GenreRepository genreRepository)
            throws ExcelValidationException {
        List<Movie> movies = new ArrayList<>();
        List<ExcelErrorDTO> errors = new ArrayList<>();
        Set<String> uniqueMovies = new HashSet<>(); // Thêm dòng này
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

            // Kiểm tra số cột tối thiểu ở header
            if (headerRow.getLastCellNum() < MIN_COLUMNS) {
                errors.add(new ExcelErrorDTO(headerRow.getRowNum() + 1,
                        "Missing columns in header row, required at least " + MIN_COLUMNS + " columns"));
                throw new ExcelValidationException(errors);
            }

            // Danh sách các cột bắt buộc
            List<String> requiredColumns = List.of("name", "description", "duration", "poster", "release_date",
                    "is_active", "genres");
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

                // Kiểm tra số cột tối thiểu ở từng dòng dữ liệu
                if (row.getLastCellNum() < MIN_COLUMNS) {
                    errors.add(new ExcelErrorDTO(rowNumber, "Missing columns at row " + rowNumber));
                    continue;
                }

                if (ExcelHelper.isRowEmpty(row)) {
                    continue;
                }

                List<String> errorMessages = new ArrayList<>();
                Movie movie = parseMovieRow(row, genreRepository, errorMessages, colIndexMap);

                // Kiểm tra trùng lặp phim theo name + release_date
                String name = ExcelHelper.getStringCell(row, colIndexMap.get("name")).trim().toLowerCase();
                String releaseDate = ExcelHelper.getStringCell(row, colIndexMap.get("release_date")).trim();
                String key = name + "-" + releaseDate;
                if (!name.isEmpty() && !releaseDate.isEmpty()) {
                    if (!uniqueMovies.add(key)) {
                        errorMessages.add("Duplicate movie in import file: name '" + name + "' and release date '"
                                + releaseDate + "' already exist in file");
                    }
                }

                if (!errorMessages.isEmpty()) {
                    errors.add(new ExcelErrorDTO(rowNumber, String.join("; ", errorMessages)));
                } else {
                    movies.add(movie);
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
        return movies;
    }

    private static Movie parseMovieRow(Row row, GenreRepository genreRepository, List<String> errorMessages,
            Map<String, Integer> colIndexMap) {
        Movie movie = new Movie();

        // Name
        String name = ExcelHelper.getStringCell(row, colIndexMap.get("name"));
        if (name.isEmpty())
            errorMessages.add("Name is required");
        movie.setName(name);

        // Description
        String desc = ExcelHelper.getStringCell(row, colIndexMap.get("description"));
        if (desc.isEmpty())
            errorMessages.add("Description is required");
        movie.setDescription(desc);

        // Duration
        String durationStr = ExcelHelper.getStringCell(row, colIndexMap.get("duration"));
        if (durationStr.isEmpty()) {
            errorMessages.add("Duration is required");
        } else {
            try {
                int duration = Integer.parseInt(durationStr);
                if (duration <= 0)
                    errorMessages.add("Duration must be positive");
                movie.setDuration(duration);
            } catch (NumberFormatException e) {
                errorMessages.add("Duration must be a valid number");
            }
        }

        // Poster
        String poster = ExcelHelper.getStringCell(row, colIndexMap.get("poster"));
        if (poster.isEmpty())
            errorMessages.add("Poster is required");
        movie.setPoster(poster);

        // Release date
        String releaseDateStr = ExcelHelper.getStringCell(row, colIndexMap.get("release_date"));
        if (releaseDateStr.isEmpty()) {
            errorMessages.add("Release date is required");
        } else {
            try {
                movie.setReleaseDate(LocalDate.parse(releaseDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            } catch (DateTimeParseException e) {
                errorMessages.add("Invalid release date format (yyyy-MM-dd)");
            }
        }

        // is_active
        String isActiveStr = ExcelHelper.getStringCell(row, colIndexMap.get("is_active"));
        if (isActiveStr.isEmpty()) {
            errorMessages.add("IsActive is required");
        } else {
            String lower = isActiveStr.trim().toLowerCase();
            if (!lower.equals("true") && !lower.equals("false")) {
                errorMessages.add("IsActive chỉ được nhập 'true' hoặc 'false' (không phân biệt hoa thường)");
            } else {
                movie.setIsActive(Boolean.parseBoolean(lower));
            }
        }

        // Genres
        String genresStr = ExcelHelper.getStringCell(row, colIndexMap.get("genres"));
        Set<Genre> genres = new HashSet<>();
        if (genresStr.isEmpty()) {
            errorMessages.add("Genres are required");
        } else {
            for (String g : genresStr.split(",")) {
                String genreName = g.trim().toLowerCase();
                if (genreName.isEmpty() || genreName.length() > 50) {
                    errorMessages.add("Invalid genre name: " + g);
                    continue;
                }
                Genre genre = genreRepository.findByNameIgnoreCase(genreName)
                        .orElseGet(() -> genreRepository.save(new Genre(genreName)));
                genres.add(genre);
            }
        }
        movie.setGenres(genres);

        return movie;
    }
}
