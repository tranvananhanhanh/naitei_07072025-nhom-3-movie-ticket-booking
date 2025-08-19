package com.naitei.group3.movie_ticket_booking_system.service.impl;

import com.naitei.group3.movie_ticket_booking_system.converter.ConvertToDtos;
import com.naitei.group3.movie_ticket_booking_system.dto.request.ShowtimeFilterReq;
import com.naitei.group3.movie_ticket_booking_system.dto.response.ShowtimeDTO;
import com.naitei.group3.movie_ticket_booking_system.enums.BookingStatus;
import com.naitei.group3.movie_ticket_booking_system.repository.BookingSeatRepository;
import com.naitei.group3.movie_ticket_booking_system.repository.MovieRepository;
import com.naitei.group3.movie_ticket_booking_system.repository.ShowtimeRepository;
import com.naitei.group3.movie_ticket_booking_system.service.ShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ShowtimeServiceImpl implements ShowtimeService {

    private final BookingSeatRepository bookingSeatRepository;
    private final ShowtimeRepository showtimeRepository;

    @Override
    public Long getNumOfBookedSeats(Long showTimeId) {
        return bookingSeatRepository.countPaidSeatsByShowtimeId(showTimeId, BookingStatus.PAID.getValue());
    }

    @Override
    public Page<ShowtimeDTO> filterShowtime(ShowtimeFilterReq filter, Pageable pageable) {
        return showtimeRepository.filterShowtimes(
                filter.getKeyword(),
                filter.getCinemaName(),
                filter.getStatus(),
                filter.getShowDate(),
                pageable
        ).map(s -> {
            Long paidSeats = this.getNumOfBookedSeats(s.getId());
            return ConvertToDtos.convertShowtimeToDTO(s, paidSeats);
        });
    }
}
