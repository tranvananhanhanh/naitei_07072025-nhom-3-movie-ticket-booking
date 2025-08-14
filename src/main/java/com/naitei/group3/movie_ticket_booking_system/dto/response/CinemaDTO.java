package com.naitei.group3.movie_ticket_booking_system.dto.response;

import lombok.Builder;

@Builder
public record CinemaDTO(
        Long id,

        String name,
        String address,
        String city,
        String mapUrl
) {
}
