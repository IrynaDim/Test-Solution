package com.dev.solution.utils.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;

public class DateValidator implements ConstraintValidator<BirthDateValidation, LocalDate> {
    @Value("${age}")
    private int age;

    @Override
    public void initialize(BirthDateValidation constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext constraintValidatorContext) {
        LocalDate eighteenYearsAgo = LocalDate.now().minusYears(age);
        return date != null && date.isBefore(eighteenYearsAgo);
    }
}
