package com.naitei.group3.movie_ticket_booking_system.dto.response;

import lombok.Builder;
import lombok.Data;

// DTO cho createPayment
@Data
@Builder
public class PaymentResponseDTO {
    private String txnRef;
    private int amount;
    private String payUrl;
    private String message;
}
