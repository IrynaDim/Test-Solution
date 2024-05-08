package com.dev.solution.utils.validation;

import com.dev.solution.exception.NotValidFieldsException;
import com.dev.solution.model.dto.UserRequestDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FieldsValidationTest {

    @Mock
    private LocalValidatorFactoryBean validator;

    @InjectMocks
    private FieldsValidation fieldsValidation;

    @Test
    void validateFields_ThrowsNotValidFieldsException_IfViolationsNotEmpty() {
        UserRequestDto updatedFields = new UserRequestDto();
        Set<ConstraintViolation<UserRequestDto>> violations = createViolations();
        when(validator.validate(updatedFields)).thenReturn(violations);

        NotValidFieldsException exception = assertThrows(NotValidFieldsException.class, () -> fieldsValidation.validateFields(updatedFields));
        List<String> messages = (List<String>) exception.getMessages();
        assertEquals(messages.size(), 2);

        assertTrue(messages.stream().anyMatch(message -> message.contains(": Address can not be empty.")));
        assertTrue(messages.stream().anyMatch(message -> message.contains(": Phone number can not be empty.")));
    }

    private Set<ConstraintViolation<UserRequestDto>> createViolations() {
        Set<ConstraintViolation<UserRequestDto>> violations = new HashSet<>();
        ConstraintViolation<UserRequestDto> violation1 = mock(ConstraintViolation.class);
        ConstraintViolation<UserRequestDto> violation2 = mock(ConstraintViolation.class);

        when(violation1.getPropertyPath()).thenReturn(mock(Path.class));
        when(violation1.getMessage()).thenReturn("Address can not be empty.");
        when(violation2.getPropertyPath()).thenReturn(mock(Path.class));
        when(violation2.getMessage()).thenReturn("Phone number can not be empty.");

        violations.add(violation1);
        violations.add(violation2);

        return violations;
    }
}
