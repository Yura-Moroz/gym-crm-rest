package com.yuramoroz.spring_crm_system.service.security;

import com.yuramoroz.spring_crm_system.service.security.impl.BruteForceProtectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BruteForceProtectionServiceTest {

    private BruteForceProtectionService service;

    @BeforeEach
    void setUp() {
        service = new BruteForceProtectionService();
    }

    @Test
    void testLoginSucceededRemovesAttempts() {
        String key = "user1";
        // Simulate some failed login attempts.
        service.loginFailed(key);
        service.loginFailed(key);
        // At this point, the user may be nearing the threshold.
        // Now simulate a successful login.
        service.loginSucceeded(key);
        // After 3-rd but successful login, the user should not be blocked.
        assertFalse(service.isBlocked(key), "User should not be blocked after a successful login");
    }

    @Test
    void testLoginFailedIncrementsAttemptsAndBlocksUser() {
        String key = "user2";

        assertFalse(service.isBlocked(key), "User should not be blocked initially");
        // 1st failure
        service.loginFailed(key);
        assertFalse(service.isBlocked(key), "User should not be blocked after 1 failed attempt");
        // 2nd failure
        service.loginFailed(key);
        assertFalse(service.isBlocked(key), "User should not be blocked after 2 failed attempts");
        // 3rd failure
        service.loginFailed(key);
        // Now, the user should be blocked (assuming the block time has not passed).
        assertTrue(service.isBlocked(key), "User should be blocked after 3 failed attempts");
    }

    @Test
    void testBlockTimeExpirationResetsBlock() {
        String key = "user3";
        // Simulate 3 failed attempts to block the user.
        service.loginFailed(key);
        service.loginFailed(key);
        service.loginFailed(key);
        assertTrue(service.isBlocked(key), "User should be blocked after 3 failed attempts");

        // Now, simulate that the block time has passed.
        // Retrieve the internal attemptsCache map using ReflectionTestUtils.
        Map<String, ?> attemptsCache = (Map<String, ?>) ReflectionTestUtils.getField(service, "attemptsCache");
        Object failedLogin = attemptsCache.get(key);
        assertNotNull(failedLogin, "FailedLogin should be present for the user");

        // The BLOCK_TIME is defined as 5 minutes (300000 ms).
        long blockTime = 5 * 60 * 1000;
        // Set the lastAttempt to a time that is older than BLOCK_TIME.
        long newLastAttempt = System.currentTimeMillis() - blockTime;
        ReflectionTestUtils.setField(failedLogin, "lastAttempt", newLastAttempt);

        // Now the block should be expired.
        assertFalse(service.isBlocked(key), "User should not be blocked after block time expires");
    }

    @Test
    void testBlacklistToken() {
        String token = "some-token";
        // Initially, token should not be blacklisted.
        assertFalse(service.isTokenBlacklisted(token), "Token should not be blacklisted initially");
        // Blacklist the token.
        service.blacklistToken(token);
        // Now it should be blacklisted.
        assertTrue(service.isTokenBlacklisted(token), "Token should be blacklisted after calling blacklistToken");
    }
}