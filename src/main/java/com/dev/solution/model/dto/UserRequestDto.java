package com.dev.solution.model.dto;

import com.dev.solution.utils.validation.BirthDateValidation;
import com.dev.solution.utils.validation.EmailValidation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserRequestDto {
    @NotNull(message = "First name can not be null.")
    @NotBlank(message = "First name can not be empty.")
    private String firstName;

    @NotNull(message = "Last name can not be null.")
    @NotBlank(message = "Last name can not be empty.")
    private String lastName;

    @NotNull(message = "Email can not be null.")
    @NotBlank(message = "Email can not be empty.")
    @EmailValidation
    private String email;

    @NotNull(message = "Birth Date can not be null.")
    @BirthDateValidation
    private LocalDate birthDate;

    @NotBlank(message = "Address can not be empty.")
    private String address;

    @NotBlank(message = "Phone number can not be empty.")
    private String phoneNumber;
}
