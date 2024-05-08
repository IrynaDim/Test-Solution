package com.dev.solution.utils.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NullOrNotBlankValidationTest {

    private final NullOrNotBlankValidation nullOrNotBlankValidation = new NullOrNotBlankValidation();

    @ParameterizedTest
    @ValueSource(strings = {"", "  "})
    void isValid_False_NullOrEmptyInput(String input) {
        assertFalse(nullOrNotBlankValidation.isValid(input, null));
    }

    @Test
    void isValid_True_CorrectInput() {
        assertTrue(nullOrNotBlankValidation.isValid("correct", null));
    }

    @Test
    void isValid_True_CorrectNullInput() {
        assertTrue(nullOrNotBlankValidation.isValid(null, null));
    }
}
