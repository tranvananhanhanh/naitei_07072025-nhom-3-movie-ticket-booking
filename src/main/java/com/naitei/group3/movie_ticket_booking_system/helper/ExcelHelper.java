package com.naitei.group3.movie_ticket_booking_system.helper;

import com.naitei.group3.movie_ticket_booking_system.dto.ExcelErrorDTO;
import com.naitei.group3.movie_ticket_booking_system.entity.Genre;
import com.naitei.group3.movie_ticket_booking_system.entity.Movie;
import com.naitei.group3.movie_ticket_booking_system.exception.ExcelValidationException;
import com.naitei.group3.movie_ticket_booking_system.repository.GenreRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class ExcelHelper {

    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    public static boolean hasExcelFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType());
    }

    public static List<Movie> excelToMovies(InputStream is, GenreRepository genreRepository) throws ExcelValidationException {
        List<ExcelErrorDTO> errors = new ArrayList<>();
        List<Movie> movies = new ArrayList<>();
        try {
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getNumberOfSheets() > 0 ? workbook.getSheetAt(0) : null;
            if (sheet == null) {
                throw new RuntimeException("No sheet found in Excel file");
            }

            DataFormatter dataFormatter = new DataFormatter();

            for (Row currentRow : sheet) {
                if (currentRow.getRowNum() == 0) continue; // Bỏ qua header

                // Kiểm tra dòng có dữ liệu hay không
                boolean isEmptyRow = true;
                for (int c = 0; c <= 6; c++) {
                    Cell cell = currentRow.getCell(c, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    if (!dataFormatter.formatCellValue(cell).trim().isEmpty()) {
                        isEmptyRow = false;
                        break;
                    }
                }
                if (isEmptyRow) continue; // Bỏ qua dòng trống

                int rowNumber = currentRow.getRowNum() + 1; // Lấy số dòng thực tế
                List<String> errorMessages = new ArrayList<>();
                Movie movie = new Movie();

                for (int cellIdx = 0; cellIdx <= 6; cellIdx++) {
                    Cell currentCell = currentRow.getCell(cellIdx, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    String cellValue = dataFormatter.formatCellValue(currentCell).trim();

                    switch (cellIdx) {
                        case 0: // name
                            if (cellValue.isEmpty()) errorMessages.add("Name is required");
                            else movie.setName(cellValue);
                            break;
                        case 1: // description
                            if (cellValue.isEmpty()) errorMessages.add("Description is required");
                            else movie.setDescription(cellValue);
                            break;
                        case 2: // duration
                            if (cellValue.isEmpty()) errorMessages.add("Duration is required");
                            else {
                                try {
                                    int duration = Integer.parseInt(cellValue);
                                    if (duration <= 0) errorMessages.add("Duration must be positive");
                                    else movie.setDuration(duration);
                                } catch (NumberFormatException e) {
                                    errorMessages.add("Duration must be a valid number");
                                }
                            }
                            break;
                        case 3: // poster
                            if (cellValue.isEmpty()) errorMessages.add("Poster is required");
                            else movie.setPoster(cellValue);
                            break;
                        case 4: // release_date
                            if (cellValue.isEmpty()) errorMessages.add("Release date is required");
                            else {
                                try {
                                    movie.setReleaseDate(LocalDate.parse(cellValue, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                                } catch (DateTimeParseException e) {
                                    errorMessages.add("Invalid release date format (yyyy-MM-dd)");
                                }
                            }
                            break;
                        case 5: // is_active
                            if (cellValue.isEmpty()) errorMessages.add("IsActive is required");
                            else movie.setIsActive(Boolean.parseBoolean(cellValue));
                            break;
                        case 6: // genres
                            Set<Genre> genres = new HashSet<>();
                            if (cellValue.isEmpty()) errorMessages.add("Genres are required");
                            else {
                                for (String g : cellValue.split(",")) {
                                    String genreName = g.trim().toLowerCase();
                                    if (genreName.isEmpty() || genreName.length() > 50) {
                                        errorMessages.add("Invalid genre name: " + g);
                                        continue;
                                    }
                                    Genre genre = genreRepository.findByNameIgnoreCase(genreName)
                                            .orElseGet(() -> {
                                                Genre newGenre = new Genre();
                                                newGenre.setName(genreName);
                                                return genreRepository.save(newGenre);
                                            });
                                    genres.add(genre);
                                }
                            }
                            movie.setGenres(genres);
                            break;
                        default:
                            break;
                    }
                }

                if (!errorMessages.isEmpty()) {
                    errors.add(new ExcelErrorDTO(rowNumber, String.join("; ", errorMessages)));
                } else {
                    movies.add(movie);
                }
            }

            workbook.close();

            if (!errors.isEmpty()) throw new ExcelValidationException(errors);

            return movies;

        } catch (ExcelValidationException e) {
            throw e;
        } catch (Exception e) {
            errors.add(new ExcelErrorDTO(0, "Fail to parse Excel file: " + e.getMessage()));
            throw new ExcelValidationException(errors);
        }
    }
}
