package com.naitei.group3.movie_ticket_booking_system.repository;

import com.naitei.group3.movie_ticket_booking_system.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    // custom query methods if needed
}
