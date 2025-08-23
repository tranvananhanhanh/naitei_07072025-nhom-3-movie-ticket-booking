package com.naitei.group3.movie_ticket_booking_system.service;

import com.naitei.group3.movie_ticket_booking_system.dto.response.MovieDTO;
import com.naitei.group3.movie_ticket_booking_system.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MovieService {

    Page<MovieDTO> filterMovies(String keyword, Integer year, String genreName, Boolean isActive, Pageable pageable);

    MovieDTO getMovieById(Long id);
    Page<Movie> getNowShowingMovies(Pageable pageable);
    void delete(Long id);
}
