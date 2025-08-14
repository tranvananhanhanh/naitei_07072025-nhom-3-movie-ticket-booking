package com.naitei.group3.movie_ticket_booking_system.service.impl;

import com.naitei.group3.movie_ticket_booking_system.entity.Hall;
import com.naitei.group3.movie_ticket_booking_system.repository.HallRepository;
import com.naitei.group3.movie_ticket_booking_system.service.HallService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HallServiceImpl implements HallService {

    private final HallRepository hallRepository;

    @Override
    public List<Hall> getHallsByCinemaId(Long id) {
        return hallRepository.findByCinemaIdOrderByNameAsc(id);
    }

}
