package com.naitei.group3.movie_ticket_booking_system.service;

import com.naitei.group3.movie_ticket_booking_system.entity.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingService {
    Booking bookSeats(Long userId, Long showtimeId, List<Long> seatIds, int pointsToUse);

    void releaseExpiredBookings();

    Optional<Booking> getBookingById(Long id);
}
