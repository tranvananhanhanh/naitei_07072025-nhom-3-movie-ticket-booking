package com.naitei.group3.movie_ticket_booking_system.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "showtime_id")
    private Showtime showtime;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    private Integer status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private LocalDateTime expiresAt; // giữ ghế trong 10 phút

    @OneToMany(mappedBy = "booking", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<BookingSeat> bookingSeats = new HashSet<>();

    @OneToMany(mappedBy = "booking", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<PaymentTransaction> paymentTransactions = new HashSet<>();
}
