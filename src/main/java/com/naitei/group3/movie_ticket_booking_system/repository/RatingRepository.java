package com.naitei.group3.movie_ticket_booking_system.repository;

import com.naitei.group3.movie_ticket_booking_system.entity.Rating;
import com.naitei.group3.movie_ticket_booking_system.entity.RatingId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, RatingId> {
    List<Rating> findByMovie_Id(Long movieId);
    List<Rating> findByUser_Id(Long userId);
}
