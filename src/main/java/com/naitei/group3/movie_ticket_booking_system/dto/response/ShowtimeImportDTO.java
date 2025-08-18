package com.naitei.group3.movie_ticket_booking_system.dto.response;

import com.naitei.group3.movie_ticket_booking_system.entity.Showtime;

public record ShowtimeImportDTO(Showtime showtime, Long movieId, Long hallId) {
}
