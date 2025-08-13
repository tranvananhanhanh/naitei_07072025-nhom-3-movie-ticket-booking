package com.naitei.group3.movie_ticket_booking_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponseDTO {
  private Long id;
  private String name;
  private String roleName;
}
