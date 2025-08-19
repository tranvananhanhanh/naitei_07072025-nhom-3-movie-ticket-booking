package com.naitei.group3.movie_ticket_booking_system.converter;

import com.naitei.group3.movie_ticket_booking_system.dto.response.MovieDTO;
import com.naitei.group3.movie_ticket_booking_system.dto.response.ShowtimeDTO;
import com.naitei.group3.movie_ticket_booking_system.entity.Movie;
import com.naitei.group3.movie_ticket_booking_system.entity.Showtime;

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

    public static ShowtimeDTO convertShowtimeToDTO(Showtime s, Long paidSeats) {
        if (s == null) return null;
        return ShowtimeDTO.builder()
                .id(s.getId())
                .movieId(s.getMovie().getId())
                .movieName(s.getMovie().getName())
                .date(s.getStartTime().toLocalDate())
                .startTime(s.getStartTime().toLocalTime())
                .endTime(s.getEndTime().toLocalTime())
                .price(s.getPrice())
                .hallName(s.getHall().getName())
                .cinemaName(s.getHall().getCinema().getName())
                .totalSeats(s.getHall().getTotalSeats())
                .paidSeats(paidSeats)
                .status(s.getStatus())
                .build();
    }
}
