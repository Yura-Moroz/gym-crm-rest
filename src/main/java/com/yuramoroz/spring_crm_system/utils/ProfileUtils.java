package com.yuramoroz.spring_crm_system.utils;

import com.yuramoroz.spring_crm_system.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.function.Function;

@Slf4j
public class ProfileUtils {

    public static String generateUsername(User user, Function<String, Boolean> userExistenceChecker) {
        String baseUsername = user.getFirstName() + "." + user.getLastName();
        String username = baseUsername;
        int serialNumber = 1;

        while (userExistenceChecker.apply(username)) {
            username = baseUsername + serialNumber;
            serialNumber++;
        }

        log.info("Generated unique username for: {} {}", user.getFirstName(), user.getLastName());
        return username;
    }

}
