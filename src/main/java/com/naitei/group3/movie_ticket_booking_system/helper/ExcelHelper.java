package com.naitei.group3.movie_ticket_booking_system.helper;

import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;
import com.naitei.group3.movie_ticket_booking_system.entity.Genre;
import com.naitei.group3.movie_ticket_booking_system.entity.Movie;
import com.naitei.group3.movie_ticket_booking_system.repository.GenreRepository;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ExcelHelper {

    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    public static boolean hasExcelFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType());
    }

    public static List<Movie> excelToMovies(InputStream is, GenreRepository genreRepository) {
        try {
            Workbook workbook = WorkbookFactory.create(is);

            Sheet sheet = workbook.getNumberOfSheets() > 0 ? workbook.getSheetAt(0) : null;
            if (sheet == null) {
                throw new RuntimeException("No sheet found in Excel file");
            }

            List<Movie> movies = new ArrayList<>();
            DataFormatter dataFormatter = new DataFormatter();
            int rowNumber = 0;

            for (Row currentRow : sheet) {
                if (rowNumber == 0) { // Skip header
                    rowNumber++;
                    continue;
                }

                Movie movie = new Movie();
                for (int cellIdx = 0; cellIdx <= 6; cellIdx++) {
                    Cell currentCell = currentRow.getCell(cellIdx, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    switch (cellIdx) {
                        case 0: // name
                            movie.setName(dataFormatter.formatCellValue(currentCell));
                            break;
                        case 1: // description (lưu String)
                            movie.setDescription(dataFormatter.formatCellValue(currentCell));
                            break;
                        case 2: // duration
                            if (currentCell.getCellType() == CellType.NUMERIC) {
                                movie.setDuration((int) currentCell.getNumericCellValue());
                            } else {
                                movie.setDuration(Integer.parseInt(dataFormatter.formatCellValue(currentCell)));
                            }
                            break;
                        case 3: // poster
                            movie.setPoster(dataFormatter.formatCellValue(currentCell));
                            break;
                        case 4: // release_date
                            String dateStr = dataFormatter.formatCellValue(currentCell);
                            if (!dateStr.isEmpty()) {
                                movie.setReleaseDate(LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                            }
                            break;
                        case 5: // is_active
                            String activeStr = dataFormatter.formatCellValue(currentCell);
                            movie.setIsActive(Boolean.parseBoolean(activeStr));
                            break;
                        case 6: // genres
                            String genresStr = dataFormatter.formatCellValue(currentCell);
                            Set<Genre> genres = new HashSet<>();
                            if (genresStr != null && !genresStr.trim().isEmpty()) {
                                for (String g : genresStr.split(",")) {
                                    String genreName = g.trim().toLowerCase();
                                    if (genreName.isEmpty() || genreName.length() > 50) continue;

                                    Genre genre;
                                    Optional<Genre> optionalGenre = genreRepository.findByNameIgnoreCase(genreName);
                                    if (optionalGenre.isPresent()) {
                                        genre = optionalGenre.get();
                                    } else {
                                        genre = new Genre();
                                        genre.setName(genreName);
                                        genre = genreRepository.save(genre);
                                    }
                                    genres.add(genre);
                                }
                            }
                            movie.setGenres(genres);
                            break;
                        default:
                            break;
                    }
                }
                movies.add(movie);
            }

            workbook.close();
            return movies;

        } catch (Exception e) {
            throw new RuntimeException("Fail to parse Excel file: " + e.getMessage(), e);
        }
    }
}
