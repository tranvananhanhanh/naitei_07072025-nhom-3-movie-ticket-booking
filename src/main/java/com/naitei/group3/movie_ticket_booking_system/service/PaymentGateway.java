package com.naitei.group3.movie_ticket_booking_system.service;

import com.naitei.group3.movie_ticket_booking_system.dto.response.PaymentResponseDTO;
import com.naitei.group3.movie_ticket_booking_system.dto.response.PaymentResultDTO;
import jakarta.servlet.http.HttpServletRequest;

public interface PaymentGateway {

    // Trả về DTO cho việc tạo payment
    PaymentResponseDTO createPayment(Long bookingId);

    // Trả về DTO cho handleReturn
    PaymentResultDTO handleReturn(HttpServletRequest request);

    // Trả về DTO cho handleIpn
    PaymentResultDTO handleIpn(HttpServletRequest request);
}
