package com.yuramoroz.spring_crm_system.profileHandlers;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHandler {
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static String hashPassword(String password) {
        if(!verify(password)) throw new IllegalArgumentException("Password should be 4-10 chars in length");
        return passwordEncoder.encode(password);
    }

    public static boolean ifPasswordMatches(String password, String encodedPassword) {
        return passwordEncoder.matches(password, encodedPassword);
    }

    public static boolean verify(String password) {
        return password.length() >= 4 && password.length() <= 10;
    }
}
