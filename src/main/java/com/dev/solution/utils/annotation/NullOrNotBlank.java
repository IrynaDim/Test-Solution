package com.dev.solution.utils.annotation;

import com.dev.solution.utils.validation.EmailValidator;
import com.dev.solution.utils.validation.NullOrNotBlankValidation;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = NullOrNotBlankValidation.class)
public @interface NullOrNotBlank {
    String message() default "Field can not be blank.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
