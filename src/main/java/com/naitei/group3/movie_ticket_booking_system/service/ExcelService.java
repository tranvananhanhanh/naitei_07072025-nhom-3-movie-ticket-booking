package com.naitei.group3.movie_ticket_booking_system.service;

import org.springframework.web.multipart.MultipartFile;

public interface ExcelService {
    void save(MultipartFile file);
}
