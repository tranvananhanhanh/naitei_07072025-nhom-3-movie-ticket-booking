package com.naitei.group3.movie_ticket_booking_system.service;

import com.naitei.group3.movie_ticket_booking_system.entity.User;
import com.naitei.group3.movie_ticket_booking_system.exception.NotEnoughPointsException;

public interface UserPointService {
    void deductPoints(User user, int pointsToUse) throws NotEnoughPointsException;
}
