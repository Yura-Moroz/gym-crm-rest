package com.yuramoroz.spring_crm_system.security;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BruteForceProtectionService {

    private final int MAX_ATTEMPT = 3;
    private final long BLOCK_TIME = 5 * 60 * 1000; // 5 minutes in milliseconds

    private Map<String, FailedLogin> attemptsCache = new ConcurrentHashMap<>();

    public void loginSucceeded(String key) {
        attemptsCache.remove(key);
    }

    public void loginFailed(String key) {
        FailedLogin failedLogin = attemptsCache.get(key);
        if (failedLogin == null) {
            failedLogin = new FailedLogin(1, System.currentTimeMillis());
        } else {
            failedLogin.increment();
        }
        attemptsCache.put(key, failedLogin);
    }

    public boolean isBlocked(String key) {
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

    private static class FailedLogin {
        private int attempts;
        private long lastAttempt;

        public FailedLogin(int attempts, long lastAttempt) {
            this.attempts = attempts;
            this.lastAttempt = lastAttempt;
        }

        public int getAttempts() {
            return attempts;
        }

        public long getLastAttempt() {
            return lastAttempt;
        }

        public void increment() {
            attempts++;
            lastAttempt = System.currentTimeMillis();
        }
    }
}
