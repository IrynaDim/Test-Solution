package com.dev.solution.utils.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DateValidatorTest {

    private final DateValidator dateValidator = new DateValidator();

    @BeforeEach
    public void init() {
        dateValidator.setAge(18);
    }

    @Test
    void IsValid_True_DateOfBirth19YearsAgo() {
        assertTrue(dateValidator.isValid(LocalDate.now().minusYears(18).minusDays(1), null));
    }

    @Test
    void IsValid_True_DateOfBirth18YearsAgo() {
        assertFalse(dateValidator.isValid(LocalDate.now().minusYears(18).plusDays(1), null));
    }

    @Test
    void IsValid_False_NullDateOfBirth() {
        assertFalse(dateValidator.isValid(null, null));
    }

    @Test
    void TestDateExactly18YearsAgo() {
        assertTrue(dateValidator.isValid(LocalDate.now().minusYears(18), null));
    }
}
