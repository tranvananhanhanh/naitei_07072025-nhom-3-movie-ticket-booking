package com.naitei.group3.movie_ticket_booking_system.controller.admin;

import com.naitei.group3.movie_ticket_booking_system.dto.request.RegisterRequestDTO;
import com.naitei.group3.movie_ticket_booking_system.dto.response.UserResponseDTO;
import com.naitei.group3.movie_ticket_booking_system.enums.RoleType;
import com.naitei.group3.movie_ticket_booking_system.utils.MessageUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.naitei.group3.movie_ticket_booking_system.entity.User;
import com.naitei.group3.movie_ticket_booking_system.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("adminAuthController")
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
  private final UserService userService;
  private final MessageUtil messageUtil;

  @GetMapping("/register")
  public String showRegister(Model model) {
    model.addAttribute("pageTitle", "Đăng ký");
    return "admin/register";
  }

  @PostMapping("/register")
  public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
    User savedUser = userService.registerUser(request, RoleType.ADMIN);

    UserResponseDTO responseDTO = new UserResponseDTO(
        savedUser.getId(),
        savedUser.getName(),
        savedUser.getRole().getName());

    return ResponseEntity.ok(responseDTO);
  }

  @GetMapping("/access-denied")
  public String accessDenied(Model model) {
    model.addAttribute("statusCode", 403);
    model.addAttribute("title", messageUtil.getMessage("error.accessdenied.title"));
    model.addAttribute("errorMessage", messageUtil.getMessage("error.accessdenied.mess"));
    model.addAttribute("redirectUrl", "/login");
    model.addAttribute("buttonLabel", messageUtil.getMessage("button.back.login"));
    return "errors/error_without-layout";
  }
}
