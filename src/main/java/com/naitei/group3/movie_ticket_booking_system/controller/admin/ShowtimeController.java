package com.naitei.group3.movie_ticket_booking_system.controller.admin;

import com.naitei.group3.movie_ticket_booking_system.dto.request.ShowtimeFilterReq;
import com.naitei.group3.movie_ticket_booking_system.dto.response.ShowtimeDTO;
import com.naitei.group3.movie_ticket_booking_system.service.ShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.swing.text.html.Option;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/showtimes")
public class ShowtimeController extends BaseAdminController {

    private final ShowtimeService service;

    @GetMapping
    public String showTimes(
            @ModelAttribute ShowtimeFilterReq filter,
            Model model) {

        PageRequest pageable = PageRequest.of(filter.getPage(), filter.getSize());

        Page<ShowtimeDTO> showtimes = service.filterShowtime(filter, pageable);

        model.addAttribute("showtimes", showtimes);
        model.addAttribute("filter", filter);

        return getAdminView("showtimes/index");
    }

    @GetMapping("/{id}")
    public String getShowtimeDetail(@PathVariable Long id, Model model) {
        ShowtimeDTO showtime = service.getShowtimeById(id);
        model.addAttribute("showtime", showtime);
        return getAdminView("showtimes/show");
    }
}
