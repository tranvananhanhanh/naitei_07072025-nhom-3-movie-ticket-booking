package com.naitei.group3.movie_ticket_booking_system.exception;

import java.util.List;

import com.naitei.group3.movie_ticket_booking_system.dto.response.*;

public class ExcelValidationException extends RuntimeException {
    private final List<ExcelErrorDTO> errors;

    public ExcelValidationException(List<ExcelErrorDTO> errors) {
        super("Excel validation failed");
        this.errors = errors;
    }

    public List<ExcelErrorDTO> getErrors() {
        return errors;
    }
}
