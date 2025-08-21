package com.naitei.group3.movie_ticket_booking_system.dto.response;

import lombok.Builder;

@Builder
public record HallDTO(
        Long id,
        String name,
        Integer totalSeats
) {
}
