package sun.naitei.group3.movie_ticket_booking_system.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "seats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id")
    private Hall hall;

    @Column(name = "seat_row")
    private String seatRow;

    @Column(name = "seat_column")
    private String seatColumn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_type_id")
    private SeatType seatType;

    // Quan hệ One-to-Many tới BookingSeat
    @OneToMany(mappedBy = "seat", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<BookingSeat> bookingSeats = new HashSet<>();
}
