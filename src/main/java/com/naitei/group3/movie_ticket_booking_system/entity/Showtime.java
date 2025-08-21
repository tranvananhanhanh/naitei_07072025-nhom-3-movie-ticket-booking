package com.naitei.group3.movie_ticket_booking_system.entity;

import com.naitei.group3.movie_ticket_booking_system.converter.ShowtimeStatusConverter;
import com.naitei.group3.movie_ticket_booking_system.enums.ShowtimeStatus;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "showtimes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Showtime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id")
    private Hall hall;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Convert(converter = ShowtimeStatusConverter.class)
    private ShowtimeStatus status;

    private BigDecimal price;

    @OneToMany(mappedBy = "showtime", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Booking> bookings = new HashSet<>();
}
