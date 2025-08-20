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

    public static boolean hasExcelFormat(MultipartFile file) {
        return ExcelHelper.hasExcelFormat(file.getContentType());
    }

    public static List<Movie> excelToMovies(InputStream is, GenreRepository genreRepository)
            throws ExcelValidationException {
        List<Movie> movies = new ArrayList<>();
        List<ExcelErrorDTO> errors = new ArrayList<>();
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
                Movie movie = parseMovieRow(row, genreRepository, errorMessages);

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

    private static Movie parseMovieRow(Row row, GenreRepository genreRepository, List<String> errorMessages) {
        // check mininum columns
        int minColumns = 7;
        if (row.getLastCellNum() < minColumns) {
            errorMessages.add("Missing columns! At least " + minColumns + " columns are required.");
            return new Movie();
        }

        Movie movie = new Movie();

        // Name
        String name = ExcelHelper.getStringCell(row, 0);
        if (name.isEmpty())
            errorMessages.add("Name is required");
        movie.setName(name);

        // Description
        String desc = ExcelHelper.getStringCell(row, 1);
        if (desc.isEmpty())
            errorMessages.add("Description is required");
        movie.setDescription(desc);

        // Duration
        String durationStr = ExcelHelper.getStringCell(row, 2);
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
        String poster = ExcelHelper.getStringCell(row, 3);
        if (poster.isEmpty())
            errorMessages.add("Poster is required");
        movie.setPoster(poster);

        // Release date
        String releaseDateStr = ExcelHelper.getStringCell(row, 4);
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
        String isActiveStr = ExcelHelper.getStringCell(row, 5);
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
        String genresStr = ExcelHelper.getStringCell(row, 6);
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
