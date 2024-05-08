package com.dev.solution.utils.validation;

import com.dev.solution.exception.NotValidFieldsException;
import com.dev.solution.model.dto.UserRequestDto;
import jakarta.validation.ConstraintViolation;
import org.springframework.stereotype.Component;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class FieldsValidation {
    private final LocalValidatorFactoryBean validator;
    private static final String SEPARATOR = ": ";

    public FieldsValidation(LocalValidatorFactoryBean validator) {
        this.validator = validator;
    }

    public void validateFields(UserRequestDto updatedFields) {
        Set<ConstraintViolation<Object>> violations = validator.validate(updatedFields);
        if (!violations.isEmpty()) {
            List<String> errorMessages = new ArrayList<>();
            for (ConstraintViolation<Object> violation : violations) {
                errorMessages.add(violation.getPropertyPath() + SEPARATOR + violation.getMessage());
            }
            throw new NotValidFieldsException(errorMessages);
        }
    }
}
