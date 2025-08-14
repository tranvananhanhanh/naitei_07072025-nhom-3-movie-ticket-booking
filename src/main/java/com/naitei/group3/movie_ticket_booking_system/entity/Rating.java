package com.naitei.group3.movie_ticket_booking_system.entity;


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
    

    // status: 0 = pending, 1 = approved, 2 = rejected 
    @Column(nullable = false)
    private Integer status;
}
