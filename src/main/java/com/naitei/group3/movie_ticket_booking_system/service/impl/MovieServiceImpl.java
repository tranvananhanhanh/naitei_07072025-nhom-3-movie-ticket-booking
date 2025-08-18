package com.naitei.group3.movie_ticket_booking_system.service.impl;

import com.naitei.group3.movie_ticket_booking_system.converter.ConvertToDtos;
import com.naitei.group3.movie_ticket_booking_system.dto.response.MovieDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.naitei.group3.movie_ticket_booking_system.repository.MovieRepository;
import com.naitei.group3.movie_ticket_booking_system.service.MovieService;
import com.naitei.group3.movie_ticket_booking_system.entity.Movie;
//import java.util.function.Predicate;


@Service
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    public MovieServiceImpl(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    public Page<MovieDTO> filterMovies(String keyword, Integer year, String genreName, Boolean isActive, Pageable pageable) {
        return movieRepository.filterMovies(keyword, year, genreName, isActive, pageable)
                .map(ConvertToDtos::convertMovieToDTO);
    }

    @Override
    public MovieDTO getMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found"));
        return ConvertToDtos.convertMovieToDTO(movie);
    }
}
