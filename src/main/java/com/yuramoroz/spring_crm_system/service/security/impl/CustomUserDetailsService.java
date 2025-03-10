package com.yuramoroz.spring_crm_system.service.security.impl;

import com.yuramoroz.spring_crm_system.entity.User;
import com.yuramoroz.spring_crm_system.service.TraineeService;
import com.yuramoroz.spring_crm_system.service.TrainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final TrainerService trainerService;
    private final TraineeService traineeService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<? extends User> optionalUser = trainerService.getByUsername(username);
        if (optionalUser.isEmpty()) {
            optionalUser = traineeService.getByUsername(username);
        }
        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        User user = optionalUser.get();

        return new org.springframework.security.core.userdetails.User(
                user.getUserName(),
                user.getPassword(),
                Collections.emptyList()
        );
    }
}
