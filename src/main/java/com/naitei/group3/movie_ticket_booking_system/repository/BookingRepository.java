package com.naitei.group3.movie_ticket_booking_system.repository;

import com.naitei.group3.movie_ticket_booking_system.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    boolean existsByUser_IdAndShowtime_Movie_IdAndStatusAndShowtime_EndTimeBefore(
            Long userId,
            Long movieId,
            Integer status,
            LocalDateTime now);
}
