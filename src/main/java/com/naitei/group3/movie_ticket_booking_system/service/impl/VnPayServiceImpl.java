package com.naitei.group3.movie_ticket_booking_system.service.impl;

import com.naitei.group3.movie_ticket_booking_system.dto.response.PaymentResponseDTO;
import com.naitei.group3.movie_ticket_booking_system.dto.response.PaymentResultDTO;
import com.naitei.group3.movie_ticket_booking_system.entity.Booking;
import com.naitei.group3.movie_ticket_booking_system.entity.PaymentTransaction;
import com.naitei.group3.movie_ticket_booking_system.enums.PaymentStatus;
import com.naitei.group3.movie_ticket_booking_system.exception.InvalidSignatureException;
import com.naitei.group3.movie_ticket_booking_system.exception.PaymentFailedException;
import com.naitei.group3.movie_ticket_booking_system.exception.ResourceNotFoundException;
import com.naitei.group3.movie_ticket_booking_system.repository.BookingRepository;
import com.naitei.group3.movie_ticket_booking_system.repository.PaymentTransactionRepository;
import com.naitei.group3.movie_ticket_booking_system.service.PaymentGateway;
import com.naitei.group3.movie_ticket_booking_system.utils.VnPayUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class VnPayServiceImpl implements PaymentGateway {

    @Value("${vnpay.tmnCode}")
    private String vnpTmnCode;

    @Value("${vnpay.hashSecret}")
    private String vnpHashSecret;

    @Value("${vnpay.payUrl}")
    private String vnpPayUrl;

    @Value("${vnpay.returnUrl}")
    private String vnpReturnUrl;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;

    @Override
    public PaymentResponseDTO createPayment(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));

        int amount = booking.getTotalPrice().intValue();
        Map<String, String> vnpParams = buildVnpParams(booking);
        String payUrl = VnPayUtils.getPaymentUrl(vnpParams, vnpHashSecret, vnpPayUrl);

        return PaymentResponseDTO.builder()
                .txnRef(String.valueOf(bookingId))
                .amount(amount)
                .payUrl(payUrl)
                .message("Create payment URL successfully")
                .build();
    }

    @Override
    public PaymentResultDTO handleReturn(HttpServletRequest request) {
        Map<String, String> fields = extractFields(request);
        String vnpSecureHash = request.getParameter("vnp_SecureHash");
        boolean valid = VnPayUtils.verifyPayment(fields, vnpSecureHash, vnpHashSecret);

        if (!valid)
            throw new InvalidSignatureException("Invalid VNPAY signature!");

        return PaymentResultDTO.builder()
                .txnRef(fields.get("vnp_TxnRef"))
                .amount(fields.get("vnp_Amount"))
                .responseCode(fields.get("vnp_ResponseCode"))
                .valid(true)
                .message("Payment successful!")
                .build();
    }

    @Override
    public PaymentResultDTO handleIpn(HttpServletRequest request) {
        Map<String, String> fields = extractFields(request);
        String vnpSecureHash = request.getParameter("vnp_SecureHash");
        boolean valid = VnPayUtils.verifyPayment(fields, vnpSecureHash, vnpHashSecret);

        if (!valid)
            throw new InvalidSignatureException("Invalid VNPAY signature!");

        PaymentResultDTO.PaymentResultDTOBuilder builder = PaymentResultDTO.builder()
                .txnRef(fields.get("vnp_TxnRef"))
                .amount(fields.get("vnp_Amount"))
                .responseCode(fields.get("vnp_ResponseCode"))
                .valid(valid);

        if ("00".equals(fields.get("vnp_ResponseCode"))) {
            Long bookingId = Long.valueOf(fields.get("vnp_TxnRef"));
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));

            PaymentTransaction transaction = new PaymentTransaction();
            transaction.setBooking(booking);
            transaction.setAmount(booking.getTotalPrice());
            transaction.setStatus(PaymentStatus.SUCCESS);

            // Cập nhật status booking
            booking.setStatus(1); // 1 = Paid
            paymentTransactionRepository.save(transaction);
            bookingRepository.save(booking);

            builder.message("Payment success (IPN)").status("OK");
        } else {
            throw new PaymentFailedException("Payment failed (IPN)");
        }

        return builder.build();
    }

    // ================= Helper methods =================
    private Map<String, String> buildVnpParams(Booking booking) {
        Map<String, String> params = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", vnpTmnCode);
        params.put("vnp_Amount", String.valueOf(booking.getTotalPrice().intValue() * 100));
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", String.valueOf(booking.getId()));
        params.put("vnp_OrderInfo", "Thanh toán vé xem phim cho booking #" + booking.getId());
        params.put("vnp_OrderType", "other");
        params.put("vnp_Locale", "vn");
        params.put("vnp_ReturnUrl", vnpReturnUrl);
        params.put("vnp_IpAddr", "127.0.0.1");
        params.put("vnp_CreateDate", LocalDateTime.now().format(formatter));

        return params;
    }

    private Map<String, String> extractFields(HttpServletRequest request) {
        Map<String, String> fields = new HashMap<>();
        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String name = params.nextElement();
            if (!name.equals("vnp_SecureHash") && !name.equals("vnp_SecureHashType")) {
                fields.put(name, request.getParameter(name));
            }
        }
        return fields;
    }
}
