package com.naitei.group3.movie_ticket_booking_system.controller.admin;

import com.naitei.group3.movie_ticket_booking_system.dto.request.MovieFilterReq;
import com.naitei.group3.movie_ticket_booking_system.dto.response.MovieDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.naitei.group3.movie_ticket_booking_system.service.MovieService;

@Controller
@RequiredArgsConstructor
public class MovieController extends BaseAdminController{

    private final MovieService movieService;

//    @GetMapping("/admin/movies")
//    public String listMovies(
//            @RequestParam(required = false) String keyword,
//            @RequestParam(required = false) Integer year,
//            @RequestParam(required = false) String genreName,
//            @RequestParam(required = false) Boolean isActive,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            Model model
//    ) {
//        // tạo pageable
//        PageRequest pageable = PageRequest.of(page, size);
//
//        Page<MovieDTO> movies = movieService.filterMovies(keyword, year, genreName, isActive, pageable);
//
//        model.addAttribute("movies", movies);
//        model.addAttribute("currentPage", page);
//        model.addAttribute("totalPages", movies.getTotalPages());
//        model.addAttribute("keyword", keyword);
//        model.addAttribute("year", year);
//        model.addAttribute("genreName", genreName);
//        model.addAttribute("isActive", isActive);
//
//        return getAdminView("movies/index");
//    }

    @GetMapping("/admin/movies")
    public String listMovies(
            @ModelAttribute MovieFilterReq filter,
            Model model
    ) {
        PageRequest pageable = PageRequest.of(filter.getPage(), filter.getSize());
        Page<MovieDTO> movies = movieService.filterMovies(
                filter.getKeyword(), filter.getYear(), filter.getGenreName(), filter.getIsActive(), pageable
        );
        model.addAttribute("movies", movies);
        model.addAttribute("currentPage", filter.getPage());
        model.addAttribute("totalPages", movies.getTotalPages());
        model.addAttribute("keyword", filter.getKeyword());
        model.addAttribute("year", filter.getYear());
        model.addAttribute("genreName", filter.getGenreName());
        model.addAttribute("isActive", filter.getIsActive());
        return getAdminView("movies/index");
    }
}
