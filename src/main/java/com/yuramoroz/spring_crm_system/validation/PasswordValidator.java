package com.yuramoroz.spring_crm_system.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Slf4j
public class PasswordValidator {
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static String hashPassword(String password) {
        log.info("Hashing provided password");
        return passwordEncoder.encode(password);
    }

    public static boolean ifPasswordMatches(String password, String encodedPassword) {
        log.info("Checking if provided password matches to encoded DB version");
        return passwordEncoder.matches(password, encodedPassword);
    }

    public static boolean verify(String password) {
        if (password.length() >= 4 && password.length() <= 10) {
            log.info("Password meets all requirements");
            return true;
        }
        log.info("Something wrong with password...\n It should be 4-10 character length");
        return false;
    }
}
