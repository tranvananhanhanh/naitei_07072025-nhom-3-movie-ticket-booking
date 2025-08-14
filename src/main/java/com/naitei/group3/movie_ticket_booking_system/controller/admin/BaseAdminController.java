package com.naitei.group3.movie_ticket_booking_system.controller.admin;

public abstract class BaseAdminController {

    // view path
    protected String getAdminView(String viewName) {
        return "admin/" + viewName;
    }
}
