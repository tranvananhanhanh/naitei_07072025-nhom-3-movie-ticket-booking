package com.naitei.group3.movie_ticket_booking_system.service.impl;

import com.naitei.group3.movie_ticket_booking_system.entity.*;
import com.naitei.group3.movie_ticket_booking_system.enums.BookingStatus;
import com.naitei.group3.movie_ticket_booking_system.repository.*;
import com.naitei.group3.movie_ticket_booking_system.service.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.naitei.group3.movie_ticket_booking_system.exception.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ShowtimeRepository showtimeRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final UserPointService userPointService;

    /**
     * Đặt ghế
     */
    @Transactional
    public Booking bookSeats(Long userId, Long showtimeId, List<Long> seatIds, int pointsToUse) {
        // Lấy thông tin suất chiếu
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found"));

        List<Seat> seats = seatRepository.findAllById(seatIds);

        // Kiểm tra ghế có đang bị giữ/đặt không
        boolean isTaken = bookingSeatRepository.existsBookedOrHeldSeats(
                showtimeId,
                seatIds,
                List.of(BookingStatus.PENDING.getValue(), BookingStatus.PAID.getValue()));
        if (isTaken) {
            throw new SeatAlreadyBookedException("One or more seats are already booked or held.");
        }

        // Tính tổng tiền
        BigDecimal totalPrice = seats.stream()
                .map(seat -> showtime.getPrice().multiply(seat.getSeatType().getPriceMultiplier()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Trừ điểm
        BigDecimal pointsToUseBD = BigDecimal.valueOf(pointsToUse);
        totalPrice = totalPrice.subtract(pointsToUseBD);
        if (totalPrice.compareTo(BigDecimal.ZERO) < 0) {
            totalPrice = BigDecimal.ZERO;
        }

        // Lấy user và trừ điểm
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (pointsToUse > user.getPoint()) {
            throw new NotEnoughPointsException("Not enough points");
        }
        userPointService.deductPoints(user, pointsToUse);

        // Tạo booking trước để có ID
        Booking booking = Booking.builder()
                .user(user)
                .showtime(showtime)
                .totalPrice(totalPrice)
                .status(BookingStatus.PENDING.getValue())
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .build();
        Booking savedBooking = bookingRepository.save(booking); // persist booking, có ID

        // Tạo các BookingSeat
        Set<BookingSeat> bookingSeats = new HashSet<>();


        for (Seat seat : seats) {
		    BookingSeat bookingSeat = new BookingSeat();
		
		    // Tạo composite key
		    BookingSeatId id = new BookingSeatId(savedBooking.getId(), seat.getId());
		    bookingSeat.setId(id);
		
		    bookingSeat.setBooking(savedBooking);
		    bookingSeat.setSeat(seat);
		    bookingSeats.add(bookingSeat);
		}
		savedBooking.setBookingSeats(bookingSeats);
		return bookingRepository.save(savedBooking);
        
    	}

    /**
     * Dọn dẹp các booking hết hạn
     */
    @Transactional
    public void releaseExpiredBookings() {
        List<Booking> expiredBookings = bookingRepository.findByStatusAndExpiresAtBefore(
                BookingStatus.PENDING.getValue(), LocalDateTime.now());
        for (Booking booking : expiredBookings) {
            booking.setStatus(BookingStatus.CANCELLED.getValue());
            // KHÔNG hoàn điểm cho user nữa
        }
        bookingRepository.saveAll(expiredBookings);
    }

    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }
}
