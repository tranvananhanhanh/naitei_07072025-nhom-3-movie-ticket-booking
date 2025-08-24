package com.naitei.group3.movie_ticket_booking_system.service.impl;

import com.naitei.group3.movie_ticket_booking_system.converter.DtoConverter;
import com.naitei.group3.movie_ticket_booking_system.dto.response.MovieDTO;
import com.naitei.group3.movie_ticket_booking_system.exception.ResourceNotFoundException;
import com.naitei.group3.movie_ticket_booking_system.enums.ShowtimeStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.naitei.group3.movie_ticket_booking_system.repository.MovieRepository;
import com.naitei.group3.movie_ticket_booking_system.service.MovieService;
import com.naitei.group3.movie_ticket_booking_system.entity.Movie;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
//import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final MessageSource messageSource;

    @Override
    public Page<MovieDTO> filterMovies(String keyword, Integer year, String genreName, Boolean isActive,
            Pageable pageable) {
        return movieRepository.filterMovies(keyword, year, genreName, isActive, pageable)
                .map(DtoConverter::convertMovieToDTO);
    }

    @Override
    public MovieDTO getMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id " + id));
        return DtoConverter.convertMovieToDTO(movie);
    }

    @Override
    public Page<Movie> getNowShowingMovies(Pageable pageable) {
        return movieRepository.findMoviesByShowtimeStatus(ShowtimeStatus.AVAILABLE.getValue(), pageable);
    }
}
