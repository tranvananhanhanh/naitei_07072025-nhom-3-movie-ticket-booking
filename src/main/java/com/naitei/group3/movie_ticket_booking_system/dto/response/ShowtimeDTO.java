package com.naitei.group3.movie_ticket_booking_system.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public record ShowtimeDTO(
        Long id,
        Long movieId,
        String movieName,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        BigDecimal price,
        String hallName,
        String cinemaName,
        Integer totalSeats,
        Long paidSeats, // số ghế đã đặt
        Integer status
) {}
