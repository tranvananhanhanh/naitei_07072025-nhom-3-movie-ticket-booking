package com.naitei.group3.movie_ticket_booking_system.controller.api;

import com.naitei.group3.movie_ticket_booking_system.dto.request.RatingRequest;
import com.naitei.group3.movie_ticket_booking_system.dto.response.RatingResponse;
import com.naitei.group3.movie_ticket_booking_system.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    // Tạo hoặc cập nhật rating
    @PostMapping
    public RatingResponse createOrUpdateRating(@RequestBody RatingRequest request) {
        return ratingService.createOrUpdateRating(request);
    }

    // Lấy rating theo movie
    @GetMapping("/movie/{movieId}")
    public List<RatingResponse> getRatingsByMovie(@PathVariable Long movieId) {
        return ratingService.getRatingsByMovie(movieId);
    }

    // Lấy rating theo user
    @GetMapping("/user/{userId}")
    public List<RatingResponse> getRatingsByUser(@PathVariable Long userId) {
        return ratingService.getRatingsByUser(userId);
    }
}
