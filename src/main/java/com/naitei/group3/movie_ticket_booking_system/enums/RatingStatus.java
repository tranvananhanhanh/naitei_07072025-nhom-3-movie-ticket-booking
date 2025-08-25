package com.naitei.group3.movie_ticket_booking_system.enums;

import lombok.Getter;

@Getter
public enum RatingStatus {

    REJECTED(-1),
    PENDING(0),
    APPROVED(1);

    private final int value;

    RatingStatus(int value) {
        this.value = value;
    }

    
}
