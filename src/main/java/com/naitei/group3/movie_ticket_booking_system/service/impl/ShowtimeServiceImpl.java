package com.naitei.group3.movie_ticket_booking_system.service.impl;

import com.naitei.group3.movie_ticket_booking_system.converter.DtoConverter;
import com.naitei.group3.movie_ticket_booking_system.dto.request.ShowtimeFilterReq;
import com.naitei.group3.movie_ticket_booking_system.dto.response.ShowtimeDTO;
import com.naitei.group3.movie_ticket_booking_system.entity.Showtime;
import com.naitei.group3.movie_ticket_booking_system.enums.BookingStatus;
import com.naitei.group3.movie_ticket_booking_system.exception.ResourceNotFoundException;
import com.naitei.group3.movie_ticket_booking_system.repository.BookingSeatRepository;
import com.naitei.group3.movie_ticket_booking_system.repository.ShowtimeRepository;
import com.naitei.group3.movie_ticket_booking_system.service.ShowtimeService;
import com.naitei.group3.movie_ticket_booking_system.utils.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ShowtimeServiceImpl implements ShowtimeService {

    private final BookingSeatRepository bookingSeatRepository;
    private final ShowtimeRepository showtimeRepository;
    private final MessageUtil messageUtil;

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
                pageable).map(s -> {
                    Long paidSeats = this.getNumOfBookedSeats(s.getId());
                    return DtoConverter.convertShowtimeToDTO(s, paidSeats);
                });
    }

    @Override
    public ShowtimeDTO getShowtimeById(Long id) {
        Showtime showtime = showtimeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageUtil.getMessage("error.showtime.notfound", id)));
        return DtoConverter.convertShowtimeToDTO(showtime, this.getNumOfBookedSeats(showtime.getId()));
    }
}
