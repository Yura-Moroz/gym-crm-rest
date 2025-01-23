package com.yuramoroz.spring_crm_system.utils;

import com.yuramoroz.spring_crm_system.entity.User;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProfileUtilsTest {
    @Test
    void testGenerateUsername_NoConflicts() {
        User mockUser = mock(User.class);
        when(mockUser.getFirstName()).thenReturn("John");
        when(mockUser.getLastName()).thenReturn("Doe");

        Function<String, Boolean> userExistenceChecker = username -> false;

        String generatedUsername = ProfileUtils.generateUsername(mockUser, userExistenceChecker);

        assertEquals("John.Doe", generatedUsername, "Generated username should be 'John.Doe'");
    }

    @Test
    void testGenerateUsername_WithConflicts() {
        User mockUser = mock(User.class);
        when(mockUser.getFirstName()).thenReturn("Jane");
        when(mockUser.getLastName()).thenReturn("Smith");

        // Mock user existence checker: returns true for "Jane.Smith" and "Jane.Smith1", then false
        Function<String, Boolean> userExistenceChecker = mock(Function.class);
        when(userExistenceChecker.apply("Jane.Smith")).thenReturn(true);
        when(userExistenceChecker.apply("Jane.Smith1")).thenReturn(true);
        when(userExistenceChecker.apply("Jane.Smith2")).thenReturn(false);

        String generatedUsername = ProfileUtils.generateUsername(mockUser, userExistenceChecker);

        assertEquals("Jane.Smith2", generatedUsername, "Generated username should be 'Jane.Smith2'");
    }

    @Test
    void testGenerateUsername_EmptyNames() {
        User mockUser = mock(User.class);
        when(mockUser.getFirstName()).thenReturn("");
        when(mockUser.getLastName()).thenReturn("");

        // Mock user existence checker: always returns false (no conflicts)
        Function<String, Boolean> userExistenceChecker = username -> false;

        String generatedUsername = ProfileUtils.generateUsername(mockUser, userExistenceChecker);

        // Verify the username
        assertEquals(".", generatedUsername, "Generated username should be '.' for empty first and last names");
    }
}
