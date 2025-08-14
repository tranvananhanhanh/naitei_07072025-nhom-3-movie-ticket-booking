package com.naitei.group3.movie_ticket_booking_system.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.http.HttpStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaseApiResponse<T> {
    private int code;
    private String message;
    private T data;

    public BaseApiResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public BaseApiResponse(int code, T data) {
        this.code = code;
        this.data = data;
    }
}
