package com.naitei.group3.movie_ticket_booking_system.dto.response;

import java.math.BigDecimal;

// Tạo BookingDTO chỉ chứa các trường cần thiết
public record BookingDTO(Long id, Long userId, Long showtimeId, BigDecimal totalPrice, int status) {
}
