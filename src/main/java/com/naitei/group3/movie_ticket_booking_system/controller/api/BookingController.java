package com.naitei.group3.movie_ticket_booking_system.controller.api;

import com.naitei.group3.movie_ticket_booking_system.dto.request.BookingRequestDTO;
import com.naitei.group3.movie_ticket_booking_system.dto.response.BaseApiResponse;
import com.naitei.group3.movie_ticket_booking_system.dto.response.BookingDTO;
import com.naitei.group3.movie_ticket_booking_system.entity.Booking;
import com.naitei.group3.movie_ticket_booking_system.service.BookingService;
import com.naitei.group3.movie_ticket_booking_system.security.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BaseApiResponse<BookingDTO>> createBooking(
            @RequestBody BookingRequestDTO request,
            Authentication authentication) {

        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
        Long userId = principal.getId();

        Booking booking = bookingService.bookSeats(
                userId,
                request.showtimeId(),
                request.seatIds(),
                request.pointsToUse());

        BookingDTO dto = new BookingDTO(
                booking.getId(),
                booking.getUser().getId(),
                booking.getShowtime().getId(),
                booking.getTotalPrice(),
                booking.getStatus());

        BaseApiResponse<BookingDTO> response = new BaseApiResponse<>(
                200,
                "Booking created successfully",
                dto);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseApiResponse<BookingDTO>> getBookingById(@PathVariable Long id) {
        return bookingService.getBookingById(id)
                .map(booking -> new BookingDTO(
                        booking.getId(),
                        booking.getUser().getId(),
                        booking.getShowtime().getId(),
                        booking.getTotalPrice(),
                        booking.getStatus()))
                .map(dto -> new BaseApiResponse<>(200, "Success", dto))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404)
                        .body(new BaseApiResponse<>(404, "Booking not found")));
    }
}
