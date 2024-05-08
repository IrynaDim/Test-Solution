package com.dev.solution.utils.validation;

import com.dev.solution.utils.annotation.NullOrNotBlank;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NullOrNotBlankValidation implements ConstraintValidator<NullOrNotBlank, String> {

    @Override
    public void initialize(NullOrNotBlank constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        return value == null || value.trim().length() > 0;
    }
}