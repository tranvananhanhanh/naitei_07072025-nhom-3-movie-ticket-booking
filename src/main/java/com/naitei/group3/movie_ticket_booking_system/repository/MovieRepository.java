package com.naitei.group3.movie_ticket_booking_system.repository;

import com.naitei.group3.movie_ticket_booking_system.enums.ShowtimeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.naitei.group3.movie_ticket_booking_system.entity.Movie;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long>, JpaSpecificationExecutor<Movie> {

    @Query("""
        SELECT DISTINCT m FROM Movie m
        LEFT JOIN m.genres g
        WHERE (:keyword IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(m.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
        AND (:year IS NULL OR FUNCTION('YEAR', m.releaseDate) = :year)
        AND (:genreName IS NULL OR LOWER(g.name) LIKE LOWER(CONCAT('%', :genreName, '%')))
        AND (:isActive IS NULL OR m.isActive = :isActive)
    """)
    Page<Movie> filterMovies(
            @Param("keyword") String keyword,
            @Param("year") Integer year,
            @Param("genreName") String genreName,
            @Param("isActive") Boolean isActive,
            Pageable pageable
    );

    @Query("""
        SELECT DISTINCT m
        FROM Movie m
        JOIN m.showtimes s
        WHERE s.status = :status
    """)
    Page<Movie> findMoviesByShowtimeStatus(@Param("status") ShowtimeStatus status, Pageable pageable);
}
