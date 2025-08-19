package com.naitei.group3.movie_ticket_booking_system.repository;

import com.naitei.group3.movie_ticket_booking_system.entity.BookingSeat;
import com.naitei.group3.movie_ticket_booking_system.entity.BookingSeatId;
import com.naitei.group3.movie_ticket_booking_system.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingSeatRepository extends JpaRepository<BookingSeat, BookingSeatId> {
    @Query("""
            SELECT COUNT(bs)
            FROM BookingSeat bs
            JOIN bs.booking b
            WHERE b.showtime.id = :showtimeId
              AND b.status = :paidStatus
            """)
    long countPaidSeatsByShowtimeId(
            @Param("showtimeId") Long showtimeId,
            @Param("paidStatus") int paidStatus
    );
}
