package com.naitei.group3.movie_ticket_booking_system.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ranks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "max_point")
    private Integer maxPoint;

    @Column(name = "min_point")
    private Integer minPoint;

    @OneToMany(mappedBy = "rank", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<User> users = new HashSet<>();
}
