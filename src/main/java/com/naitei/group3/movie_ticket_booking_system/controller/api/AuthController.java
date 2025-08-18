package com.naitei.group3.movie_ticket_booking_system.controller.api;

import com.naitei.group3.movie_ticket_booking_system.dto.request.RegisterRequestDTO;
import com.naitei.group3.movie_ticket_booking_system.dto.response.UserResponseDTO;
import com.naitei.group3.movie_ticket_booking_system.enums.RoleType;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.naitei.group3.movie_ticket_booking_system.entity.User;
import com.naitei.group3.movie_ticket_booking_system.service.impl.UserService;

@RestController("apiAuthController")
@RequestMapping("/api/v1/auth")
public class AuthController {
  private final UserService userService;

  public AuthController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/register")
  public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
    User savedUser = userService.registerUser(request, RoleType.USER);

    UserResponseDTO responseDTO = new UserResponseDTO(
        savedUser.getId(),
        savedUser.getName(),
        savedUser.getRole().getName()
    );

    return ResponseEntity.ok(responseDTO);
  }
}
