package com.naitei.group3.movie_ticket_booking_system.service;

import com.naitei.group3.movie_ticket_booking_system.entity.Hall;

import java.util.List;

public interface HallService {

    List<Hall> getHallsByCinemaId(Long id);
}
