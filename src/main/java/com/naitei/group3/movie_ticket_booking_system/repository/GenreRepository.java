package com.naitei.group3.movie_ticket_booking_system.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.naitei.group3.movie_ticket_booking_system.entity.Genre;
import java.util.Optional;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    Optional<Genre> findByNameIgnoreCase(String name);
}
