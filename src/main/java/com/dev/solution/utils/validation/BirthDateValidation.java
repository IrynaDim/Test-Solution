package com.dev.solution.utils.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = DateValidator.class)
public @interface BirthDateValidation {
    String message() default "Date value must be earlier than current date.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
