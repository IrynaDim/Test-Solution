package com.dev.solution.controller;

import com.dev.solution.model.dto.DataResponse;
import com.dev.solution.model.dto.DataResponsePage;
import com.dev.solution.model.dto.UserRequestDto;
import com.dev.solution.model.dto.UserResponseDto;
import com.dev.solution.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Tag(name = "User", description = "API for users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by id.", responses = {
            @ApiResponse(description = "Not found", responseCode = "404")
    })
    public DataResponse<UserResponseDto> findById(@PathVariable Long id) {
        return new DataResponse<>(userService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new user.", responses = {
            @ApiResponse(description = "Bad request", responseCode = "400"),
            @ApiResponse(description = "Conflict", responseCode = "409")
    })
    public DataResponse<UserResponseDto> save(@RequestBody @Valid UserRequestDto user) {
        return new DataResponse<>(userService.save(user));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user by id.")
    public void deleteById(@PathVariable Long id) {
        userService.deleteById(id);
    }

    @GetMapping
    @Operation(summary = "Find users by birth date between two dates. " +
            "The result will be a paginated list of users.")
    public DataResponsePage<UserResponseDto> findByDateBetween(@RequestParam LocalDate from,
                                                               @RequestParam LocalDate to,
                                                               @RequestParam(defaultValue = "0") Integer page,
                                                               @RequestParam(defaultValue = "10") Integer size) {
        Page<UserResponseDto> usersPage = userService.findByDateBetween(from, to, PageRequest.of(page, size));
        return new DataResponsePage<>(usersPage.getContent(), usersPage.getTotalPages(), usersPage.getTotalElements());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update full user. If a field is passed as null, " +
            "the corresponding value for the user will be removed.",
            responses = {
                    @ApiResponse(description = "Bad request", responseCode = "400"),
                    @ApiResponse(description = "Not found", responseCode = "404"),
                    @ApiResponse(description = "Conflict", responseCode = "409")
            })
    public DataResponse<UserResponseDto> updateFullUser(@PathVariable Long id,
                                                        @RequestBody @Valid UserRequestDto userDto) {

        return new DataResponse<>(userService.updateFullUser(id, userDto));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update part of a user's information. Only specified fields can be updated." +
            "Possible fields to update: firstName, lastName, email, birthDate (in format YYYY-MM-DD), address, phone. " +
            "Empty or null values for certain fields are not allowed. Address and phone can be null.",
            responses = {
                    @ApiResponse(description = "Bad request", responseCode = "400"),
                    @ApiResponse(description = "Not found", responseCode = "404"),
                    @ApiResponse(description = "Conflict", responseCode = "409")
            })
    public DataResponse<UserResponseDto> updatePartUser(@PathVariable Long id,
                                                        @RequestBody Map<String, Object> fieldsToUpdate) {
        return new DataResponse<>(userService.updatePartUser(id, fieldsToUpdate));
    }
}
