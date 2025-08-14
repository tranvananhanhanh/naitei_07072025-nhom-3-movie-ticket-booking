package com.naitei.group3.movie_ticket_booking_system.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "halls")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hall {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_id")
    private Cinema cinema;

    private String name;

    @Column(name = "total_seats")
    private Integer totalSeats;

    @OneToMany(mappedBy = "hall", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Seat> seats = new HashSet<>();

    @OneToMany(mappedBy = "hall", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Showtime> showtimes = new HashSet<>();
}
