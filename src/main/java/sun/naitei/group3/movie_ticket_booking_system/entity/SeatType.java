package sun.naitei.group3.movie_ticket_booking_system.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "seattypes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "price_multiplier")
    private BigDecimal priceMultiplier;

    @OneToMany(mappedBy = "seatType", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Seat> seats = new HashSet<>();
}
