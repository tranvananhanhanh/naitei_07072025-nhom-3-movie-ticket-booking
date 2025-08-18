package com.naitei.group3.movie_ticket_booking_system.repository;

import com.naitei.group3.movie_ticket_booking_system.entity.Hall;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HallRepository extends JpaRepository<Hall, Long> {

    List<Hall> findByCinemaIdOrderByNameAsc(Long id);
}
