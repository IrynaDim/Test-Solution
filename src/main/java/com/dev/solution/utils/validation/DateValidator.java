package com.dev.solution.utils.validation;

import com.dev.solution.utils.annotation.BirthDateValidation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;

@Setter
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
        return date != null && (date.isBefore(eighteenYearsAgo) || date.isEqual(eighteenYearsAgo));
    }
}
