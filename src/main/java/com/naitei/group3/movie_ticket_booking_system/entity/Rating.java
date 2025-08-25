package com.naitei.group3.movie_ticket_booking_system.entity;

import com.naitei.group3.movie_ticket_booking_system.enums.RatingStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ratings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rating {

    @EmbeddedId
    private RatingId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @MapsId("movieId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movie movie;

    private Integer stars;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Enumerated(EnumType.STRING)  // Lưu enum dạng chuỗi: PENDING, APPROVED, REJECTED
    @Column(nullable = false)
    private RatingStatus status;
}
