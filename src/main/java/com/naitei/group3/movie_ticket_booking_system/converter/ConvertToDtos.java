package com.naitei.group3.movie_ticket_booking_system.converter;

import com.naitei.group3.movie_ticket_booking_system.dto.response.MovieDTO;
import com.naitei.group3.movie_ticket_booking_system.entity.Movie;

import java.util.stream.Collectors;

public class ConvertToDtos {

    public static MovieDTO convertMovieToDTO(Movie movie) {
        if (movie == null) return null;

        return MovieDTO.builder()
                .id(movie.getId())
                .name(movie.getName())
                .description(movie.getDescription())
                .duration(movie.getDuration())
                .poster(movie.getPoster())
                .releaseDate(movie.getReleaseDate())
                .isActive(movie.getIsActive())
                .genres(movie.getGenres().stream()
                        .map(g -> g.getName())
                        .collect(Collectors.toSet()))
                .build();
    }
}
