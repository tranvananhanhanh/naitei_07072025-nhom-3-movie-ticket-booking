package com.naitei.group3.movie_ticket_booking_system.repository;

import com.naitei.group3.movie_ticket_booking_system.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByHallId(Long id);

    @Query("""
            SELECT s FROM Seat s
            WHERE s.hall.id = :hallId
            """)
    List<Seat> findAllByHallId(Long hallId);
}
