package com.dev.solution.service.impl;

import com.dev.solution.exception.AlreadyExists;
import com.dev.solution.exception.ErrorMessage;
import com.dev.solution.exception.NotFoundException;
import com.dev.solution.exception.NotValidFields;
import com.dev.solution.model.User;
import com.dev.solution.model.dto.UserRequestDto;
import com.dev.solution.model.dto.UserResponseDto;
import com.dev.solution.repository.UserRepository;
import com.dev.solution.service.UserService;
import jakarta.validation.ConstraintViolation;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final LocalValidatorFactoryBean validator;
    private final ModelMapper modelMapper;

    public UserServiceImpl(UserRepository userRepository,
                           LocalValidatorFactoryBean validator,
                           ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.validator = validator;
        this.modelMapper = modelMapper;
    }

    @Override
    public UserResponseDto findById(Long id) {
        return modelMapper.map(
                userRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrorMessage.USER_BY_ID_NOT_FOUND + id)),
                UserResponseDto.class);
    }

    @Override
    public UserResponseDto save(UserRequestDto userDto) {
        User user = modelMapper.map(userDto, User.class);
        checkEmailUnique(userDto.getEmail());
        checkPhoneUnique(userDto.getPhoneNumber());
        return modelMapper.map(userRepository.save(user), UserResponseDto.class);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public Page<UserResponseDto> findByDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        if (startDate.isAfter(endDate)) {
            throw new NotValidFields("Start date must be before end date.");
        }

        Page<User> usersPage = userRepository.findByBirthDateBetween(startDate, endDate, pageable);

        List<UserResponseDto> userResponseDtoList = usersPage.getContent()
                .stream()
                .map(u -> modelMapper.map(u, UserResponseDto.class))
                .collect(Collectors.toList());

        return new PageImpl<>(userResponseDtoList, pageable, usersPage.getTotalElements());
    }

    @Override
    @Transactional
    public UserResponseDto updateFullUser(Long id, UserRequestDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_BY_ID_NOT_FOUND + id));
        if (!user.getEmail().equals(userDto.getEmail())) {
            checkEmailUnique(userDto.getEmail());
        }
        if (!user.getPhoneNumber().equals(userDto.getPhoneNumber())) {
            checkPhoneUnique(userDto.getPhoneNumber());
        }
        modelMapper.map(userDto, user);
        return modelMapper.map(userRepository.save(user), UserResponseDto.class);
    }

    /**
     * Updates specific fields of a user identified by the provided ID.
     * Retrieves the user from the repository based on the ID, then updates the specified fields with the values provided in the fieldsToUpdate map.
     * Checks for uniqueness constraints on email and phone number fields. If the updated email or phone number already exists for another user, throws an IllegalArgumentException.
     * Validates the updated fields using the Bean Validation framework. If any field fails validation, a NotValidFields exception is thrown with details of the validation errors.
     * Saves the updated user entity to the repository and returns the updated user information as a UserFullDto object.
     *
     * @param id             The ID of the user to be updated.
     * @param fieldsToUpdate A Map containing the fields to update along with their new values.
     * @return The updated UserFullDto object containing the modified user information.
     * @throws NotFoundException        if no user with the provided ID is found in the repository.
     * @throws NotValidFields           if any of the updated fields fail validation.
     * @throws IllegalArgumentException if the updated email or phone number already exists for another user.
     */
    @Override
    @Transactional
    public UserResponseDto updatePartUser(Long id, Map<String, Object> fieldsToUpdate) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_BY_ID_NOT_FOUND + id));

        UserRequestDto updatedFields = modelMapper.map(user, UserRequestDto.class);
        modelMapper.map(fieldsToUpdate, updatedFields);
        validateFields(updatedFields);
        User userToUpdate = modelMapper.map(updatedFields, User.class);
        userToUpdate.setId(id);

        if (!user.getEmail().equals(updatedFields.getEmail())) {
            checkEmailUnique(updatedFields.getEmail());
        }
        if (!user.getPhoneNumber().equals(updatedFields.getPhoneNumber())) {
            checkPhoneUnique(updatedFields.getPhoneNumber());
        }

        return modelMapper.map(userRepository.save(userToUpdate), UserResponseDto.class);
    }

    private void checkEmailUnique(String email) {
        userRepository.findByEmail(email).ifPresent(u -> {
            throw new AlreadyExists(ErrorMessage.USER_BY_EMAIL_EXIST + email);
        });
    }

    private void checkPhoneUnique(String phone) {
        userRepository.findByPhoneNumber(phone).ifPresent(u -> {
            throw new AlreadyExists(ErrorMessage.USER_BY_PHONE_NUMBER_EXIST + phone);
        });
    }

    private void validateFields(UserRequestDto updatedFields) {
        Set<ConstraintViolation<Object>> violations = validator.validate(updatedFields);
        if (!violations.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder(ErrorMessage.NOT_CORRECT_FIELD_NAME);
            for (ConstraintViolation<Object> violation : violations) {
                errorMessage.append(violation.getPropertyPath()).append(" ").append(violation.getMessage()).append("; ");
            }
            throw new NotValidFields(errorMessage.toString());
        }
    }

}
