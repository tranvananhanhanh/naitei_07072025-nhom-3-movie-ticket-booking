package sun.naitei.group3.movie_ticket_booking_system.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // keep JSON text as String; DB column may use JSON type
    @Column(columnDefinition = "JSON")
    private String description;

    private Integer duration; // in minutes
    private String poster;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "is_active")
    private Boolean isActive;

    @OneToMany(mappedBy = "movie", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Showtime> showtimes = new HashSet<>();

    @OneToMany(mappedBy = "movie", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Rating> ratings = new HashSet<>();

    // Many-to-Many with Genre
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "movie_genre",
        joinColumns = @JoinColumn(name = "movie_id"),
        inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();
}
