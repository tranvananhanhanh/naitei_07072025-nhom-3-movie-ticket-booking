package com.naitei.group3.movie_ticket_booking_system.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record SeatDTO(
        Long id,
        String seatRow,
        String seatColumn,
        String seatTypeName,
        BigDecimal priceMultiplier
) {
}
