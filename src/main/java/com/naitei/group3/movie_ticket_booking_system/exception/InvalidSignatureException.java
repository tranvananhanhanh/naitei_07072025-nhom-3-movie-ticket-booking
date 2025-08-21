package com.naitei.group3.movie_ticket_booking_system.exception;

public class InvalidSignatureException extends PaymentException {
    public InvalidSignatureException(String message) {
        super(message);
    }

}
