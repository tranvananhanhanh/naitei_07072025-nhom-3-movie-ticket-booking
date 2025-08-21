package com.naitei.group3.movie_ticket_booking_system.repository;


import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.naitei.group3.movie_ticket_booking_system.entity.User;
import org.springframework.stereotype.Repository;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);
  boolean existsByEmail(String email);
}
