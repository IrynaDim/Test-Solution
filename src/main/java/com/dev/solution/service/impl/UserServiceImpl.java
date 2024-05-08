package com.dev.solution.service.impl;

import com.dev.solution.exception.AlreadyExistsException;
import com.dev.solution.exception.ErrorMessage;
import com.dev.solution.exception.NotFoundException;
import com.dev.solution.exception.NotValidFieldsException;
import com.dev.solution.model.User;
import com.dev.solution.model.dto.UserRequestDto;
import com.dev.solution.model.dto.UserResponseDto;
import com.dev.solution.repository.UserRepository;
import com.dev.solution.service.UserService;
import com.dev.solution.utils.validation.FieldsValidation;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final FieldsValidation fieldsValidation;

    public UserServiceImpl(UserRepository userRepository,
                           ModelMapper modelMapper,
                           FieldsValidation fieldsValidation) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.fieldsValidation = fieldsValidation;
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
            throw new NotValidFieldsException("Start date must be before end date.");
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
     * @throws NotValidFieldsException  if any of the updated fields fail validation.
     * @throws IllegalArgumentException if the updated email or phone number already exists for another user.
     */
    @Override
    @Transactional
    public UserResponseDto updatePartUser(Long id, Map<String, Object> fieldsToUpdate) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_BY_ID_NOT_FOUND + id));

        UserRequestDto updatedFields = modelMapper.map(user, UserRequestDto.class);
        modelMapper.map(fieldsToUpdate, updatedFields);

        fieldsValidation.validateFields(updatedFields);

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
            throw new AlreadyExistsException(ErrorMessage.USER_BY_EMAIL_EXIST + email);
        });
    }

    private void checkPhoneUnique(String phone) {
        userRepository.findByPhoneNumber(phone).ifPresent(u -> {
            throw new AlreadyExistsException(ErrorMessage.USER_BY_PHONE_NUMBER_EXIST + phone);
        });
    }
}
