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
import java.util.stream.Collectors;

public class ExcelHelper {

    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    public static boolean hasExcelFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType());
    }

    public static List<Movie> excelToMovies(InputStream is, GenreRepository genreRepository) {
        List<Movie> movies = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(is)) {
            Sheet sheet = workbook.getNumberOfSheets() > 0 ? workbook.getSheetAt(0) : null;
            if (sheet == null) {
                throw new RuntimeException("No sheet found in Excel file");
            }
            Iterator<Row> rows = sheet.iterator();
            if (rows.hasNext()) rows.next(); // Skip header

            while (rows.hasNext()) {
                Row row = rows.next();
                Movie movie = parseMovieRow(row, genreRepository);
                movies.add(movie);
            }
        } catch (Exception e) {
            throw new RuntimeException("Fail to parse Excel file: " + e.getMessage(), e);
        }
        return movies;
    }

    private static Movie parseMovieRow(Row row, GenreRepository genreRepository) {
        Movie movie = new Movie();
        movie.setName(getStringCell(row, 0));
        movie.setDescription(getStringCell(row, 1));
        movie.setDuration(getIntCell(row, 2));
        movie.setPoster(getStringCell(row, 3));
        movie.setReleaseDate(getDateCell(row, 4));
        movie.setIsActive(getBooleanCell(row, 5));
        movie.setGenres(getGenresCell(row, 6, genreRepository));
        return movie;
    }

    private static String getStringCell(Row row, int idx) {
        Cell cell = row.getCell(idx, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell).trim();
    }

    private static int getIntCell(Row row, int idx) {
    	DataFormatter formatter = new DataFormatter();
    	String value = formatter.formatCellValue(row.getCell(idx, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)).trim();
    	return value.matches("-?\\d+") ? Integer.parseInt(value) : 0;
    }

    private static LocalDate getDateCell(Row row, int idx) {
    	Cell cell = row.getCell(idx, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
    	DataFormatter formatter = new DataFormatter();
    	String value = formatter.formatCellValue(cell).trim();
    	if (value.isEmpty()) {
    		return null; // Trả về sớm
    	}
    	try {
    		return LocalDate.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    	} catch (Exception ignored) {
    		return null;
    	}
    }


    private static Boolean getBooleanCell(Row row, int idx) {
        Cell cell = row.getCell(idx, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        DataFormatter formatter = new DataFormatter();
        String value = formatter.formatCellValue(cell).trim();
        return Boolean.parseBoolean(value);
    }

    private static Set<Genre> getGenresCell(Row row, int idx, GenreRepository genreRepository) {
        String genresStr = getStringCell(row, idx);
        if (genresStr.isEmpty()) return Collections.emptySet();
        return Arrays.stream(genresStr.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(genreName -> !genreName.isEmpty() && genreName.length() <= 50)
                .map(genreName -> genreRepository.findByNameIgnoreCase(genreName)
                        .orElseGet(() -> genreRepository.save(new Genre(genreName))))
                .collect(Collectors.toSet());
    }
}
