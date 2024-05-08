package com.dev.solution.utils.annotation;

import com.dev.solution.utils.validation.DateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = DateValidator.class)
public @interface BirthDateValidation {
    String message() default "Age must be over 18.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
