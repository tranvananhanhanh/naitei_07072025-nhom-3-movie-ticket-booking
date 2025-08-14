package com.naitei.group3.movie_ticket_booking_system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.naitei.group3.movie_ticket_booking_system.repository.GenreRepository;
import com.naitei.group3.movie_ticket_booking_system.repository.MovieRepository;
import com.naitei.group3.movie_ticket_booking_system.helper.ExcelHelper;
import com.naitei.group3.movie_ticket_booking_system.entity.Movie;

import java.util.List;

@Service
public class ExcelService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private GenreRepository genreRepository;

    public void save(MultipartFile file) {
        try {
            List<Movie> movies = ExcelHelper.excelToMovies(file.getInputStream(), genreRepository);
            movieRepository.saveAll(movies);
        } catch (Exception e) {
            throw new RuntimeException("Không thể lưu dữ liệu từ file Excel: " + e.getMessage());
        }
    }
}
