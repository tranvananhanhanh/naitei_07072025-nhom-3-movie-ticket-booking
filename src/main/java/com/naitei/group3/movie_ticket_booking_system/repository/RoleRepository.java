package com.naitei.group3.movie_ticket_booking_system.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.naitei.group3.movie_ticket_booking_system.entity.Role;
import org.springframework.stereotype.Repository;

public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(String name);
}
