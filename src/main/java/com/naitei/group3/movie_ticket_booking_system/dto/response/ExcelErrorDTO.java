package com.naitei.group3.movie_ticket_booking_system.dto.response;

public class ExcelErrorDTO {
    private final int row;
    private final String message;

    public ExcelErrorDTO(int row, String message) {
        this.row = row;
        this.message = message;
    }

    public int getRow() {
        return row;
    }

    public String getMessage() {
        return message;
    }
}
