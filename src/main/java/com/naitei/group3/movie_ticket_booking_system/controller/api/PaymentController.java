package com.naitei.group3.movie_ticket_booking_system.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.naitei.group3.movie_ticket_booking_system.service.PaymentGateway;
import com.naitei.group3.movie_ticket_booking_system.dto.response.PaymentResultDTO;
import com.naitei.group3.movie_ticket_booking_system.dto.response.PaymentResponseDTO;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/pay")
public class PaymentController {

    @Autowired
    private PaymentGateway paymentGateway;

    /**
     * API 1: Tạo URL thanh toán VNPAY
     */
    @PostMapping("/create")
    public PaymentResponseDTO createPayment(@RequestBody java.util.Map<String, Object> payload) {
        Long bookingId = Long.valueOf(payload.get("bookingId").toString());
        return paymentGateway.createPayment(bookingId);
    }

    /**
     * API 2: Xử lý khi redirect từ VNPAY về (Return URL)
     */
    @GetMapping("/return")
    public PaymentResultDTO handleReturn(HttpServletRequest request) {
        return paymentGateway.handleReturn(request);
    }

    /**
     * API 3: Callback từ VNPAY (IPN URL)
     */
    @PostMapping("/ipn")
    public PaymentResultDTO handleIpn(HttpServletRequest request) {
        return paymentGateway.handleIpn(request);
    }
}
