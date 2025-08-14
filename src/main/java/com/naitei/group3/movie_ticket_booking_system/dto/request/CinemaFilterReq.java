package com.naitei.group3.movie_ticket_booking_system.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CinemaFilterReq {
    private String keyword;
    private String city;

    @PositiveOrZero
    private int page = 0;

    @Min(1) @Max(100)
    private int size = 6;

    public void setKeyword(String keyword) {
        this.keyword = emptyToNull(keyword);
    }

    public void setCity(String city) {
        this.city = emptyToNull(city);
    }

    public void setPage(int page) {
        this.page = Math.max(0, page);
    }

    public void setSize(int size) {
        if (size < 1) size = 6;
        if (size > 100) size = 100;
        this.size = size;
    }

    private String emptyToNull(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }
}
