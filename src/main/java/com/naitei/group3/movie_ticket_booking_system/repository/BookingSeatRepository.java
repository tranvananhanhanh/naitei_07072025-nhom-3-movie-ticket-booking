package com.naitei.group3.movie_ticket_booking_system.repository;

import com.naitei.group3.movie_ticket_booking_system.entity.BookingSeat;
import com.naitei.group3.movie_ticket_booking_system.entity.BookingSeatId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingSeatRepository extends JpaRepository<BookingSeat, BookingSeatId> {

        // Đếm số ghế đã thanh toán của một suất chiếu
        @Query("""
                        SELECT COUNT(bs)
                        FROM BookingSeat bs
                        JOIN bs.booking b
                        WHERE b.showtime.id = :showtimeId
                          AND b.status = :paidStatus
                        """)
        long countPaidSeatsByShowtimeId(
                        @Param("showtimeId") Long showtimeId,
                        @Param("paidStatus") int paidStatus);

        // Kiểm tra xem ghế có bị giữ hoặc đã đặt không
        @Query("""
                        SELECT CASE WHEN COUNT(bs) > 0 THEN true ELSE false END
                        FROM BookingSeat bs
                        JOIN bs.booking b
                        WHERE b.showtime.id = :showtimeId
                          AND bs.seat.id IN :seatIds
                          AND b.status IN :statuses
                        """)
        boolean existsBookedOrHeldSeats(
                        @Param("showtimeId") Long showtimeId,
                        @Param("seatIds") List<Long> seatIds,
                        @Param("statuses") List<Integer> statuses);

        
}
