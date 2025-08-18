package com.naitei.group3.movie_ticket_booking_system.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Data;

@Data
public class RegisterRequestDTO {
  @NotBlank(message = "Tên không được để trống")
  private String name;

  @Email(message = "Email không hợp lệ")
  @NotBlank(message = "Email không được để trống")
  private String email;

  @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
  private String password;

  private String address;
  private String phone;
  private Integer gender;
  private LocalDate dateOfBirth;
}
