package com.naitei.group3.movie_ticket_booking_system.service.impl;

import com.naitei.group3.movie_ticket_booking_system.converter.DtoConverter;
import com.naitei.group3.movie_ticket_booking_system.dto.response.SeatDTO;
import com.naitei.group3.movie_ticket_booking_system.entity.Seat;
import com.naitei.group3.movie_ticket_booking_system.repository.SeatRepository;
import com.naitei.group3.movie_ticket_booking_system.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;

    @Override
    public Map<String, List<SeatDTO>> getSeatsByHallId(Long id) {
        List<Seat> seats = seatRepository.findByHallId(id);

        // Convert -> DTO
        List<SeatDTO> seatDTOs = seats.stream()
                .map(DtoConverter::convertSeatToDTO)
                .toList();

        // Group + sort
        return seatDTOs.stream()
                .collect(Collectors.groupingBy(         // group by row
                        SeatDTO::seatRow,
                        TreeMap::new,                   // sort row alphabet
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.stream()
                                        .sorted(
                                                Comparator.comparing(
                                                        SeatDTO::seatTypeName,              // sort by type
                                                        Comparator.comparingInt(type ->
                                                                List.of("Standard", "VIP", "Double").indexOf(type)
                                                        )
                                                ).thenComparingInt(s -> {           // sort by type
                                                    // Extract number from column string
                                                    String col = s.seatColumn(); // ví dụ: "A1"
                                                    return Integer.parseInt(col.replaceAll("\\D+", ""));        // remove non-digit
                                                })
                                        )
                                        .toList()
                        )
                ));
    }
}
