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
}
