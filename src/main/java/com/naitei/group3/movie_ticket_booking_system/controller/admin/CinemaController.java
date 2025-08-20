package com.naitei.group3.movie_ticket_booking_system.controller.admin;

import com.naitei.group3.movie_ticket_booking_system.dto.request.CinemaFilterReq;
import com.naitei.group3.movie_ticket_booking_system.dto.response.CinemaDTO;
import com.naitei.group3.movie_ticket_booking_system.service.CinemaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
@RequiredArgsConstructor
public class CinemaController extends BaseAdminController{

    private final CinemaService cinemaService;

    @GetMapping("/admin/cinemas")
    public String listCinemas(@ModelAttribute CinemaFilterReq filter, Model model) {

        var pageable = PageRequest.of(filter.getPage(), filter.getSize());
        Page<CinemaDTO> cinemas = cinemaService.searchCinema(filter.getKeyword(), filter.getCity(), pageable);

        model.addAttribute("cinemas", cinemas);
        model.addAttribute("totalPages", cinemas.getTotalPages());
        model.addAttribute("filter", filter);

        model.addAttribute("cities", cinemaService.getAllCities());

        return getAdminView("cinemas/index");
    }
}
