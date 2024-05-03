package com.dev.solution.service;

import com.dev.solution.model.dto.UserRequestDto;
import com.dev.solution.model.dto.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Map;

public interface UserService {
    UserResponseDto findById(Long id);

    UserResponseDto save(UserRequestDto user);

    void deleteById(Long id);

    Page<UserResponseDto> findByDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    UserResponseDto updateFullUser(Long id, UserRequestDto userDto);

    UserResponseDto updatePartUser(Long id, Map<String, Object> fieldsToUpdate);
}
