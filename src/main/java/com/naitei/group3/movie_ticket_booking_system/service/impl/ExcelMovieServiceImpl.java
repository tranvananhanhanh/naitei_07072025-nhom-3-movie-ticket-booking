package com.naitei.group3.movie_ticket_booking_system.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.naitei.group3.movie_ticket_booking_system.repository.GenreRepository;
import com.naitei.group3.movie_ticket_booking_system.repository.MovieRepository;
import com.naitei.group3.movie_ticket_booking_system.service.ExcelService;
import com.naitei.group3.movie_ticket_booking_system.helper.ExcelHelper;
import com.naitei.group3.movie_ticket_booking_system.entity.Movie;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

@Service
@Transactional
public class ExcelMovieServiceImpl implements ExcelService  {

    private static final Logger log = LoggerFactory.getLogger(ExcelMovieServiceImpl.class);

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Override
    @Transactional(rollbackFor = Exception.class) // rollback cho cả checked + unchecked
    public void save(MultipartFile file) {
        try {
            List<Movie> movies = ExcelHelper.excelToMovies(file.getInputStream(), genreRepository);
            movieRepository.saveAll(movies);
        } catch (IOException e) {
            log.error("Lỗi khi đọc file Excel: {}", e.getMessage(), e);
            throw new RuntimeException("Không thể đọc file Excel", e);
        } catch (Exception e) {
            log.error("Lỗi khi lưu movies: {}", e.getMessage(), e);
            throw e; // ném lại để transaction rollback
        }
    }
}
