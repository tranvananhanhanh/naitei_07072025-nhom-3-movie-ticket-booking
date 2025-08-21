package com.naitei.group3.movie_ticket_booking_system.repository;

import com.naitei.group3.movie_ticket_booking_system.entity.PaymentTransaction;
import com.naitei.group3.movie_ticket_booking_system.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    // Tìm giao dịch thanh toán theo booking
    Optional<PaymentTransaction> findByBooking(Booking booking);
}
