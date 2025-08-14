package com.naitei.group3.movie_ticket_booking_system.dto.response;

import lombok.Builder;

import java.time.LocalDate;
import java.util.Set;

@Builder
public record MovieDTO(
        Long id,
        String name,
        String description,
        Integer duration,
        String poster,
        LocalDate releaseDate,
        Boolean isActive,
        Set<String>genres
) {
}
