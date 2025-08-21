package com.naitei.group3.movie_ticket_booking_system.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.naitei.group3.movie_ticket_booking_system.service.impl.BookingServiceImpl;

@Component
@RequiredArgsConstructor
public class BookingCleanupTask {

    private final BookingServiceImpl bookingService;

    @Scheduled(fixedRate = 600000) // chạy mỗi 10 phút (600000 ms)
    public void cleanup() {
        bookingService.releaseExpiredBookings();
    }
}
