package com.naitei.group3.movie_ticket_booking_system.controller.admin;

import com.naitei.group3.movie_ticket_booking_system.dto.request.CinemaFilterReq;
import com.naitei.group3.movie_ticket_booking_system.dto.response.CinemaDTO;
import com.naitei.group3.movie_ticket_booking_system.entity.Hall;
import com.naitei.group3.movie_ticket_booking_system.service.CinemaService;
import com.naitei.group3.movie_ticket_booking_system.service.HallService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CinemaController extends BaseAdminController{

    private final CinemaService cinemaService;
    private final HallService hallService;
//    private final HallService hallService;

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

    @GetMapping("/admin/cinemas/{id}")
    public String getCinemaDetail(@PathVariable Long id, Model model) {
        CinemaDTO cinema = cinemaService.getCinemaById(id); // nếu không có sẽ throw
        List<Hall> halls = hallService.getHallsByCinemaId(id);

        model.addAttribute("cinema", cinema);
        model.addAttribute("halls", halls);
        return getAdminView("cinemas/detail");
    }
}
