package com.naitei.group3.movie_ticket_booking_system.service.impl;

import com.naitei.group3.movie_ticket_booking_system.dto.request.RatingRequest;
import com.naitei.group3.movie_ticket_booking_system.dto.response.RatingResponse;
import com.naitei.group3.movie_ticket_booking_system.entity.*;
import com.naitei.group3.movie_ticket_booking_system.enums.BookingStatus;
import com.naitei.group3.movie_ticket_booking_system.enums.RatingStatus;
import com.naitei.group3.movie_ticket_booking_system.exception.ResourceNotFoundException;
import com.naitei.group3.movie_ticket_booking_system.repository.BookingRepository;
import com.naitei.group3.movie_ticket_booking_system.repository.MovieRepository;
import com.naitei.group3.movie_ticket_booking_system.repository.RatingRepository;
import com.naitei.group3.movie_ticket_booking_system.repository.UserRepository;
import com.naitei.group3.movie_ticket_booking_system.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    public RatingResponse createOrUpdateRating(RatingRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));

        // Kiểm tra user đã mua vé và suất chiếu đã kết thúc
        boolean hasWatched = bookingRepository
                .existsByUser_IdAndShowtime_Movie_IdAndStatusAndShowtime_EndTimeBefore(
                        user.getId(),
                        movie.getId(),
                        BookingStatus.PAID.getValue(),
                        LocalDateTime.now());

        if (!hasWatched) {
            throw new RuntimeException("Bạn chỉ có thể đánh giá phim sau khi đã xem xong suất chiếu.");
        }

        RatingId ratingId = new RatingId(request.getUserId(), request.getMovieId());
        Rating rating = ratingRepository.findById(ratingId)
                .orElse(Rating.builder()
                        .id(ratingId)
                        .user(user)
                        .movie(movie)
                        .build());

        rating.setStars(request.getStars());
        rating.setComment(request.getComment());
        rating.setStatus(RatingStatus.PENDING); // dùng trực tiếp enum

        ratingRepository.save(rating);

        return RatingResponse.builder()
                .movieId(movie.getId())
                .userId(user.getId())
                .stars(rating.getStars())
                .comment(rating.getComment())
                .status(rating.getStatus()) 
                .build();
    }

    @Override
    public List<RatingResponse> getRatingsByMovie(Long movieId) {
        return ratingRepository.findByMovie_Id(movieId).stream()
                .map(r -> RatingResponse.builder()
                        .movieId(r.getMovie().getId())
                        .userId(r.getUser().getId())
                        .stars(r.getStars())
                        .comment(r.getComment())
                        .status(r.getStatus()) 
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<RatingResponse> getRatingsByUser(Long userId) {
        return ratingRepository.findByUser_Id(userId).stream()
                .map(r -> RatingResponse.builder()
                        .movieId(r.getMovie().getId())
                        .userId(r.getUser().getId())
                        .stars(r.getStars())
                        .comment(r.getComment())
                        .status(r.getStatus())
                        .build())
                .collect(Collectors.toList());
    }
}
