package com.naitei.group3.movie_ticket_booking_system.converter;

import com.naitei.group3.movie_ticket_booking_system.dto.response.CinemaDTO;
import com.naitei.group3.movie_ticket_booking_system.dto.response.MovieDTO;
import com.naitei.group3.movie_ticket_booking_system.dto.response.SeatDTO;
import com.naitei.group3.movie_ticket_booking_system.dto.response.ShowtimeDTO;
import com.naitei.group3.movie_ticket_booking_system.entity.Cinema;
import com.naitei.group3.movie_ticket_booking_system.entity.Movie;
import com.naitei.group3.movie_ticket_booking_system.entity.Seat;
import com.naitei.group3.movie_ticket_booking_system.entity.Showtime;

import java.util.stream.Collectors;

public class DtoConverter {

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

    public static CinemaDTO convertCinemaToDTO(Cinema cinema) {
        if (cinema == null) return null;

        return CinemaDTO.builder()
                .id(cinema.getId())
                .name(cinema.getName())
                .address(cinema.getAddress())
                .city(cinema.getCity())
                .mapUrl(cinema.getMapUrl())
                .build();
    }

    public static ShowtimeDTO convertShowtimeToDTO(Showtime s, Long paidSeats) {
        if (s == null) return null;
        return ShowtimeDTO.builder()
                .id(s.getId())
                .date(s.getStartTime().toLocalDate())
                .startTime(s.getStartTime().toLocalTime())
                .endTime(s.getEndTime().toLocalTime())
                .price(s.getPrice())
                .paidSeats(paidSeats)
                .status(s.getStatus())
                .movie(s.getMovie())
                .hall(s.getHall())
                .build();
    }

    public static SeatDTO convertSeatToDTO(Seat seat) {
        if (seat == null) return null;

        return SeatDTO.builder()
                .id(seat.getId())
                .seatRow(seat.getSeatRow())
                .seatColumn(seat.getSeatColumn())
                .seatTypeName(seat.getSeatType().getName())
                .priceMultiplier(seat.getSeatType().getPriceMultiplier())
                .build();
    }
}
