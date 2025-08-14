package com.naitei.group3.movie_ticket_booking_system.exception;

public class AuthenticationFailedEx extends RuntimeException {
    public AuthenticationFailedEx(String message) {
        super(message);
    }
}
