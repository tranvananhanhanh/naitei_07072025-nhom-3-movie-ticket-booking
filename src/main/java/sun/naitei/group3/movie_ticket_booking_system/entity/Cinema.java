package sun.naitei.group3.movie_ticket_booking_system.entity;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cinemas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cinema {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    private String city;

    @Column(name = "map_url")
    private String mapUrl;

    @OneToMany(mappedBy = "cinema", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Hall> halls = new HashSet<>();
}