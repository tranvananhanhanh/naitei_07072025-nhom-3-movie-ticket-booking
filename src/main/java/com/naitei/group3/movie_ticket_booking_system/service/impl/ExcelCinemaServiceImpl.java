package com.naitei.group3.movie_ticket_booking_system.service.impl;

import com.naitei.group3.movie_ticket_booking_system.entity.Cinema;
import com.naitei.group3.movie_ticket_booking_system.helper.ExcelCinemaHelper;
import com.naitei.group3.movie_ticket_booking_system.repository.CinemaRepository;
import com.naitei.group3.movie_ticket_booking_system.service.ExcelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Transactional
public class ExcelCinemaServiceImpl implements ExcelService {

    private static final Logger log = LoggerFactory.getLogger(ExcelCinemaServiceImpl.class);

    @Autowired
    private CinemaRepository cinemaRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(MultipartFile file) {
        try {
            List<Cinema> cinemas = ExcelCinemaHelper.excelToCinemas(file.getInputStream());
            cinemaRepository.saveAll(cinemas);
        } catch (IOException e) {
            log.error("Error reading Excel file: {}", e.getMessage(), e);
            throw new RuntimeException("Cannot read Excel file", e);
        } catch (Exception e) {
            log.error("Error saving cinemas: {}", e.getMessage(), e);
            throw e;
        }
    }
}
