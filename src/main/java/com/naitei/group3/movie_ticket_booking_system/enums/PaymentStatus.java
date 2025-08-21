package com.naitei.group3.movie_ticket_booking_system.enums;

public enum PaymentStatus {
    PENDING(0, "Pending"),
    SUCCESS(1, "Success"),
    FAILED(-1, "Failed");

    private final int code;
    private final String description;

    PaymentStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
