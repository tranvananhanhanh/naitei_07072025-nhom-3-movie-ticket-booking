package com.naitei.group3.movie_ticket_booking_system.enums;

import lombok.Getter;

@Getter
public enum BookingStatus {

    CANCELLED(-1),
    PENDING(0),
    PAID(1);

    private final int value;

    BookingStatus(int value) {
        this.value = value;
    }
}
