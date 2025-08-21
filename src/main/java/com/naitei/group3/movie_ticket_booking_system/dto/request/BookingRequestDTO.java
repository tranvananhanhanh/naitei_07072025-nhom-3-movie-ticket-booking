package com.naitei.group3.movie_ticket_booking_system.dto.request;

import java.util.List;

public record BookingRequestDTO(
        Long userId,
        Long showtimeId,
        List<Long> seatIds,
        int pointsToUse) {
}
