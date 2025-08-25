package com.naitei.group3.movie_ticket_booking_system.dto.response;

import com.naitei.group3.movie_ticket_booking_system.enums.RatingStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RatingResponse {
    private Long movieId;
    private Long userId;
    private Integer stars;
    private String comment;
    private RatingStatus status;
}
