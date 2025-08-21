package com.naitei.group3.movie_ticket_booking_system.converter;

import com.naitei.group3.movie_ticket_booking_system.enums.ShowtimeStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ShowtimeStatusConverter implements AttributeConverter<ShowtimeStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(ShowtimeStatus status) {
        return status != null ? status.getValue() : null;
    }

    @Override
    public ShowtimeStatus convertToEntityAttribute(Integer dbData) {
        return dbData != null ? ShowtimeStatus.fromValue(dbData) : null;
    }
}

