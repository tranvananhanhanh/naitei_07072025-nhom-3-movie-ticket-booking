package com.naitei.group3.movie_ticket_booking_system.enums;

import lombok.Getter;

@Getter
public enum ShowtimeStatus {
    CANCELLED(-1),
    AVAILABLE(0),
    FINISHED(1);

    private final int value;

    ShowtimeStatus(int value) {
        this.value = value;
    }

    public static ShowtimeStatus fromValue(int value) {
        for (ShowtimeStatus status : values()) {
            if (status.value == value) return status;
        }
        throw new IllegalArgumentException("Invalid status: " + value);
    }
}
