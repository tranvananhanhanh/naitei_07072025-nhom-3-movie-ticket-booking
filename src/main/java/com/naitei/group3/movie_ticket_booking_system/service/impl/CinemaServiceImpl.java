package com.naitei.group3.movie_ticket_booking_system.service.impl;

import com.naitei.group3.movie_ticket_booking_system.converter.DtoConverter;
import com.naitei.group3.movie_ticket_booking_system.dto.response.CinemaDTO;
import com.naitei.group3.movie_ticket_booking_system.repository.CinemaRepository;
import com.naitei.group3.movie_ticket_booking_system.service.CinemaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CinemaServiceImpl implements CinemaService {

    private final CinemaRepository cinemaRepository;

    @Override
    public Page<CinemaDTO> searchCinema(String keyword, String city, Pageable pageable) {
        return cinemaRepository.searchCinema(keyword, city, pageable)
                .map(DtoConverter::convertCinemaToDTO);
    }

    public List<String> getAllCities() {
        return cinemaRepository.findDistinctCities();
    }
}
