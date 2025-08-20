package com.naitei.group3.movie_ticket_booking_system.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/admin")
public class DashboardController extends BaseAdminController {

    @GetMapping
    public String dashboard(Model model) {

        model.addAttribute("pageTitle", "Dashboard");
        model.addAttribute("ticketsToday", 0);
        return getAdminView("dashboard");
    }

}
