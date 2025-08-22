package com.naitei.group3.movie_ticket_booking_system.service.impl;

import com.naitei.group3.movie_ticket_booking_system.dto.response.ExcelErrorDTO;
import com.naitei.group3.movie_ticket_booking_system.dto.response.ShowtimeImportDTO;
import com.naitei.group3.movie_ticket_booking_system.entity.Hall;
import com.naitei.group3.movie_ticket_booking_system.entity.Movie;
import com.naitei.group3.movie_ticket_booking_system.entity.Showtime;
import com.naitei.group3.movie_ticket_booking_system.exception.ExcelValidationException;
import com.naitei.group3.movie_ticket_booking_system.helper.ExcelShowtimeHelper;
import com.naitei.group3.movie_ticket_booking_system.repository.HallRepository;
import com.naitei.group3.movie_ticket_booking_system.repository.MovieRepository;
import com.naitei.group3.movie_ticket_booking_system.repository.ShowtimeRepository;
import com.naitei.group3.movie_ticket_booking_system.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExcelShowtimeServiceImpl implements ExcelService {

    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;
    private final HallRepository hallRepository;

    @Override
    public void save(MultipartFile file) throws ExcelValidationException {
        try {
            Set<Long> validMovieIds = movieRepository.findAll().stream()
                    .map(Movie::getId)
                    .collect(Collectors.toSet());
            Set<Long> validHallIds = hallRepository.findAll().stream()
                    .map(Hall::getId)
                    .collect(Collectors.toSet());

            InputStream is = file.getInputStream();
            List<ShowtimeImportDTO> dtos = ExcelShowtimeHelper.excelToShowtimes(is, validMovieIds, validHallIds);

            List<Showtime> showtimes = new ArrayList<>();
            for (ShowtimeImportDTO dto : dtos) {
                Movie movie = movieRepository.findById(dto.movieId())
                        .orElseThrow(() -> new ExcelValidationException(
                                List.of(new ExcelErrorDTO(0, "Movie not found with id: " + dto.movieId()))));
                Hall hall = hallRepository.findById(dto.hallId())
                        .orElseThrow(() -> new ExcelValidationException(
                                List.of(new ExcelErrorDTO(0, "Hall not found with id: " + dto.hallId()))));
                Showtime showtime = dto.showtime();
                showtime.setMovie(movie);
                showtime.setHall(hall);
                showtimes.add(showtime);
            }

            showtimeRepository.saveAll(showtimes);
        } catch (ExcelValidationException e) {
            throw e;
        } catch (Exception e) {
            List<ExcelErrorDTO> errors = new ArrayList<>();
            errors.add(new ExcelErrorDTO(0, "Fail to store excel data: " + e.getMessage()));
            throw new ExcelValidationException(errors);
        }
    }
}
