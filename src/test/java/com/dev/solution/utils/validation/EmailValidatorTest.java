package com.dev.solution.utils.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class EmailValidatorTest {

    private final EmailValidator emailValidator = new EmailValidator();

    @ParameterizedTest
    @ValueSource(strings = {"test@example.com", "test+123@e.com"})
    void isValid_True_ValidEmail(String email) {
        assertTrue(emailValidator.isValid(email, null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"test@example", "testexample.com", "test@examplecom"})
    void isValid_False_InvalidEmail(String email) {
        assertFalse(emailValidator.isValid(email, null));
    }

    @Test
    void isValid_False_NullEmail() {
        assertFalse(emailValidator.isValid(null, null));
    }

    @Test
    void isValid_False_EmptyEmail() {
        assertFalse(emailValidator.isValid("", null));
    }
}