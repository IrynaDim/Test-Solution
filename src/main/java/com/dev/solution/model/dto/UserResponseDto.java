package com.dev.solution.model.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate birthDate;
    private String address;
    private String phoneNumber;
}
