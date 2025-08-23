package com.naitei.group3.movie_ticket_booking_system.service;

import com.naitei.group3.movie_ticket_booking_system.dto.response.SeatDTO;

import java.util.List;
import java.util.Map;

public interface SeatService {

//    List<SeatDTO> getSeatsByHallId(Long id);
    Map<String, List<SeatDTO>> getSeatsByHallId(Long id);
}
