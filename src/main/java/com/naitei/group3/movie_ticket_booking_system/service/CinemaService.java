package com.naitei.group3.movie_ticket_booking_system.service;

import com.naitei.group3.movie_ticket_booking_system.dto.response.CinemaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CinemaService {

    Page<CinemaDTO> searchCinema(String keyword, String city, Pageable pageable);
    List<String> getAllCities();
    CinemaDTO getCinemaById(Long id);
}
