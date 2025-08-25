package com.naitei.group3.movie_ticket_booking_system.dto.request;

import lombok.Data;

@Data
public class RatingRequest {
    private Long userId;     // user gửi rating
    private Long movieId;    // phim nào
    private Integer stars;   // số sao
    private String comment;  // nội dung bình luận
}
