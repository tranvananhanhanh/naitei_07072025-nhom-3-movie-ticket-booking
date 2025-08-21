package com.naitei.group3.movie_ticket_booking_system.service.impl;

import com.naitei.group3.movie_ticket_booking_system.entity.User;
import com.naitei.group3.movie_ticket_booking_system.exception.NotEnoughPointsException;
import com.naitei.group3.movie_ticket_booking_system.repository.UserRepository;
import com.naitei.group3.movie_ticket_booking_system.service.UserPointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserPointServiceImpl implements UserPointService {
    private final UserRepository userRepository;

    @Override
    public void deductPoints(User user, int pointsToUse) {
        if (pointsToUse > user.getPoint()) {
            throw new NotEnoughPointsException("Not enough points");
        }
        user.setPoint(user.getPoint() - pointsToUse);
        userRepository.save(user);
    }
}
