package com.naitei.group3.movie_ticket_booking_system.security;

import com.naitei.group3.movie_ticket_booking_system.utils.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.naitei.group3.movie_ticket_booking_system.entity.User;
import com.naitei.group3.movie_ticket_booking_system.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyUserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    private final MessageUtil messageUtil;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmailWithRolesAndPermissions(email)
                .orElseThrow(() -> new UsernameNotFoundException(messageUtil.getMessage("error.user.email.notfound")));

        if (!user.getIsVerified()) {
            throw new DisabledException(messageUtil.getMessage("error.not.verified"));
        }

        return new CustomUserPrincipal(user);
    }
}
