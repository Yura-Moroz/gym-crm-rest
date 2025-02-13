package com.yuramoroz.spring_crm_system.security;

import com.yuramoroz.spring_crm_system.entity.Trainee;
import com.yuramoroz.spring_crm_system.entity.Trainer;
import com.yuramoroz.spring_crm_system.entity.User;
import com.yuramoroz.spring_crm_system.service.TraineeService;
import com.yuramoroz.spring_crm_system.service.TrainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final TrainerService trainerService;
    private final TraineeService traineeService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // First, try to load as Trainer
        Optional<? extends User> optionalUser = trainerService.getByUsername(username);
        if (!optionalUser.isPresent()) {
            // Try as Trainee if not found as Trainer
            optionalUser = traineeService.getByUsername(username);
        }
        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        User appUser = optionalUser.get();

        // Assign role(s) based on user type
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (appUser instanceof Trainer) {
            authorities.add(new SimpleGrantedAuthority("ROLE_TRAINER"));
        } else if (appUser instanceof Trainee) {
            authorities.add(new SimpleGrantedAuthority("ROLE_TRAINEE"));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        // Note: appUser.isActive() controls if the user is enabled.
        return new org.springframework.security.core.userdetails.User(
                appUser.getUserName(),
                appUser.getPassword(),
                appUser.isActive(), // enabled
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                authorities);
    }
}
