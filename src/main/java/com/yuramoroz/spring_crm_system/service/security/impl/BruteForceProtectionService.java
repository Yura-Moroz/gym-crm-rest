package com.yuramoroz.spring_crm_system.service.security.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class BruteForceProtectionService {

    private final int MAX_ATTEMPT = 3;
    private final long BLOCK_TIME = 5 * 60 * 1000; // 5 minutes in milliseconds

    private final Map<String, FailedLogin> attemptsCache = new ConcurrentHashMap<>();
    private final Set<String> blacklist = ConcurrentHashMap.newKeySet();

    public void loginSucceeded(String key) {
        log.info("Remove user's username from attempts if authenticated");
        attemptsCache.remove(key);
    }

    public void loginFailed(String key) {
        log.info("Checking the number of attempts left before the user is blocked");
        FailedLogin failedLogin = attemptsCache.get(key);
        if (failedLogin == null) {
            failedLogin = new FailedLogin(1, System.currentTimeMillis());
        } else {
            failedLogin.increment();
        }
        attemptsCache.put(key, failedLogin);
    }

    public boolean isBlocked(String key) {
        log.info("Setting user status to 'blocked' for " + (BLOCK_TIME/60/1000) + " minutes");
        FailedLogin failedLogin = attemptsCache.get(key);
        if (failedLogin == null) {
            return false;
        }
        if (failedLogin.getAttempts() >= MAX_ATTEMPT) {
            long timeSinceLastAttempt = System.currentTimeMillis() - failedLogin.getLastAttempt();
            if (timeSinceLastAttempt < BLOCK_TIME) {
                return true;
            } else {
                // Block time expired—reset counter.
                attemptsCache.remove(key);
                return false;
            }
        }
        return false;
    }

    public void blacklistToken(String token) {
        log.info("Putting token to blacklist");
        blacklist.add(token);
    }

    public boolean isTokenBlacklisted(String token) {
        log.info("Checking if token is blacklisted");
        return blacklist.contains(token);
    }

    @Getter
    @AllArgsConstructor
    private static class FailedLogin {
        private int attempts;
        private long lastAttempt;

        public void increment() {
            log.info("Setting new number for attempts and a time for the last one");
            attempts++;
            lastAttempt = System.currentTimeMillis();
        }
    }
}
