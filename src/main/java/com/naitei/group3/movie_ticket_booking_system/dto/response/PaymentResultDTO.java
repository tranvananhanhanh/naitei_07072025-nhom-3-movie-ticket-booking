package com.naitei.group3.movie_ticket_booking_system.dto.response;

import lombok.Builder;
import lombok.Data;

// DTO cho handleReturn / handleIpn
@Data
@Builder
public class PaymentResultDTO {
    private String txnRef;
    private String amount;
    private String responseCode;
    private boolean valid;
    private String message;
    private String status; // chỉ dùng cho IPN nếu cần
}
