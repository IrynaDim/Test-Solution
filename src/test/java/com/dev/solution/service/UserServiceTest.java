package com.dev.solution.service;

import com.dev.solution.exception.AlreadyExistsException;
import com.dev.solution.exception.NotFoundException;
import com.dev.solution.exception.NotValidFieldsException;
import com.dev.solution.model.User;
import com.dev.solution.model.dto.UserRequestDto;
import com.dev.solution.model.dto.UserResponseDto;
import com.dev.solution.repository.UserRepository;
import com.dev.solution.service.impl.UserServiceImpl;
import com.dev.solution.utils.validation.FieldsValidation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

//To avoid extra work, I wrote the minimum number of tests. It could also have been beneficial to test the mapper instead
// of mocking it. Additionally, field validation could have been tested. However, the project has already grown quite
// large for a test project.
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private FieldsValidation fieldsValidation;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private final UserResponseDto RESPONSE_DTO = new UserResponseDto();
    private final Long USER_ID = 1L;
    private final User USER = new User();
    private final UserRequestDto REQUEST_DTO = new UserRequestDto();
    private final int TIME_OF_INVOCATION = 1;
    private final String EMAIL = "test email";
    private final String PHONE = "test phone";
    private final String EMAIL_2 = "test email 2";
    private final String PHONE_2 = "test phone 2";
    private final LocalDate FROM_DATE = LocalDate.of(2024, 1, 1);
    private final LocalDate TO_DATE = LocalDate.of(2024, 12, 31);
    private final PageRequest PAGE_REQUEST = PageRequest.of(0, 10);

    // --- findById
    @Test
    public void FindById_ShouldReturnUser() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(USER));
        when(modelMapper.map(USER, UserResponseDto.class)).thenReturn(RESPONSE_DTO);

        assertEquals(userService.findById(USER_ID), RESPONSE_DTO);
        verify(userRepository, times(TIME_OF_INVOCATION)).findById(USER_ID);
    }

    @Test
    public void FindById_ThrowsNotFound_IfUserByIdNotFound() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(USER_ID))
                .isInstanceOf(NotFoundException.class);
        verify(userRepository, times(TIME_OF_INVOCATION)).findById(USER_ID);
    }

    // -- save
    @Test
    public void Save_ShouldSaveUser() {
        REQUEST_DTO.setEmail(EMAIL);
        REQUEST_DTO.setPhoneNumber(PHONE);

        when(modelMapper.map(REQUEST_DTO, User.class)).thenReturn(USER);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumber(PHONE)).thenReturn(Optional.empty());
        when(userRepository.save(USER)).thenReturn(USER);
        when(modelMapper.map(USER, UserResponseDto.class)).thenReturn(RESPONSE_DTO);

        assertEquals(userService.save(REQUEST_DTO), RESPONSE_DTO);
        verify(userRepository, times(TIME_OF_INVOCATION)).save(USER);
        verify(userRepository, times(TIME_OF_INVOCATION)).findByEmail(EMAIL);
        verify(userRepository, times(TIME_OF_INVOCATION)).findByPhoneNumber(PHONE);

    }

    @Test
    public void Save_ThrowsAlreadyExists_IfUserByEmailExist() {
        REQUEST_DTO.setEmail(EMAIL);
        REQUEST_DTO.setPhoneNumber(PHONE);

        when(modelMapper.map(REQUEST_DTO, User.class)).thenReturn(USER);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(USER));

        assertThatThrownBy(() -> userService.save(REQUEST_DTO))
                .isInstanceOf(AlreadyExistsException.class);
        verify(userRepository, never()).save(USER);
        verify(userRepository, times(TIME_OF_INVOCATION)).findByEmail(EMAIL);
        verify(userRepository, never()).findByPhoneNumber(PHONE);
    }

    @Test
    public void Save_ThrowsAlreadyExists_IfUserByPhoneExist() {
        REQUEST_DTO.setEmail(EMAIL);
        REQUEST_DTO.setPhoneNumber(PHONE);

        when(modelMapper.map(REQUEST_DTO, User.class)).thenReturn(USER);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumber(PHONE)).thenReturn(Optional.of(USER));

        assertThatThrownBy(() -> userService.save(REQUEST_DTO))
                .isInstanceOf(AlreadyExistsException.class);
        verify(userRepository, never()).save(USER);
        verify(userRepository, times(TIME_OF_INVOCATION)).findByEmail(EMAIL);
        verify(userRepository, times(TIME_OF_INVOCATION)).findByPhoneNumber(PHONE);
    }

    // -- updateFullUser
    @Test
    public void UpdateFullUser_ShouldUpdateUser_WithNewEmailAndPhone() {
        REQUEST_DTO.setEmail(EMAIL);
        REQUEST_DTO.setPhoneNumber(PHONE);
        USER.setEmail(EMAIL_2);
        USER.setPhoneNumber(PHONE_2);

        setupCommonMocksForUpdateFullUser(USER, REQUEST_DTO);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumber(PHONE)).thenReturn(Optional.empty());
        when(userRepository.save(USER)).thenReturn(USER);

        assertEquals(userService.updateFullUser(USER_ID, REQUEST_DTO), RESPONSE_DTO);

        verify(userRepository, times(TIME_OF_INVOCATION)).save(USER);
        verify(userRepository, times(TIME_OF_INVOCATION)).findById(USER_ID);
        verify(userRepository, times(TIME_OF_INVOCATION)).findByEmail(EMAIL);
        verify(userRepository, times(TIME_OF_INVOCATION)).findByPhoneNumber(PHONE);
    }

    @Test
    public void UpdateFullUser_ShouldUpdateUser() {
        REQUEST_DTO.setEmail(EMAIL);
        REQUEST_DTO.setPhoneNumber(PHONE);
        USER.setEmail(EMAIL);
        USER.setPhoneNumber(PHONE);

        setupCommonMocksForUpdateFullUser(USER, REQUEST_DTO);
        when(userRepository.save(USER)).thenReturn(USER);

        assertEquals(userService.updateFullUser(USER_ID, REQUEST_DTO), RESPONSE_DTO);

        verify(userRepository, times(TIME_OF_INVOCATION)).save(USER);
        verify(userRepository, times(TIME_OF_INVOCATION)).findById(USER_ID);
        verify(userRepository, never()).findByEmail(EMAIL);
        verify(userRepository, never()).findByPhoneNumber(PHONE);
    }

    @Test
    public void UpdateFullUser_ThrowsNotFound_IfUserByIdNotFound() {

        when(userRepository.findById(USER_ID)).thenThrow(new NotFoundException());

        assertThatThrownBy(() -> userService.updateFullUser(USER_ID, REQUEST_DTO))
                .isInstanceOf(NotFoundException.class);

        verify(userRepository, never()).save(USER);
        verify(userRepository, times(TIME_OF_INVOCATION)).findById(USER_ID);
        verify(userRepository, never()).findByEmail(EMAIL);
        verify(userRepository, never()).findByPhoneNumber(PHONE);
    }

    @Test
    public void UpdateFullUser_ThrowsAlreadyExist_IfUserWithEmailExist() {
        REQUEST_DTO.setEmail(EMAIL);
        REQUEST_DTO.setPhoneNumber(PHONE);
        USER.setEmail(EMAIL_2);
        USER.setPhoneNumber(PHONE_2);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(USER));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(USER));

        assertThatThrownBy(() -> userService.updateFullUser(USER_ID, REQUEST_DTO))
                .isInstanceOf(AlreadyExistsException.class);

        verify(userRepository, never()).save(USER);
        verify(userRepository, times(TIME_OF_INVOCATION)).findById(USER_ID);
        verify(userRepository, times(TIME_OF_INVOCATION)).findByEmail(EMAIL);
        verify(userRepository, never()).findByPhoneNumber(PHONE);
    }

    @Test
    public void UpdateFullUser_ThrowsAlreadyExist_IfUserWithPhoneExist() {
        REQUEST_DTO.setEmail(EMAIL);
        REQUEST_DTO.setPhoneNumber(PHONE);
        USER.setEmail(EMAIL_2);
        USER.setPhoneNumber(PHONE_2);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumber(PHONE)).thenReturn(Optional.of(USER));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(USER));

        assertThatThrownBy(() -> userService.updateFullUser(USER_ID, REQUEST_DTO))
                .isInstanceOf(AlreadyExistsException.class);

        verify(userRepository, never()).save(USER);
        verify(userRepository, times(TIME_OF_INVOCATION)).findById(USER_ID);
        verify(userRepository, times(TIME_OF_INVOCATION)).findByEmail(EMAIL);
        verify(userRepository, times(TIME_OF_INVOCATION)).findByPhoneNumber(PHONE);
    }

    // -- updatePartUser
    @Test
    public void UpdatePartUser_ShouldUpdateUser_WithNewEmailAndPhone() {
        USER.setEmail(EMAIL_2);
        USER.setPhoneNumber(PHONE_2);
        REQUEST_DTO.setEmail(EMAIL);
        REQUEST_DTO.setPhoneNumber(PHONE);

        setupCommonMocksPartlyUpdate(USER, REQUEST_DTO);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumber(PHONE)).thenReturn(Optional.empty());
        when(userRepository.save(USER)).thenReturn(USER);
        when(modelMapper.map(USER, UserResponseDto.class)).thenReturn(RESPONSE_DTO);

        assertEquals(userService.updatePartUser(USER_ID, new HashMap<>()), RESPONSE_DTO);

        verify(userRepository, times(TIME_OF_INVOCATION)).save(USER);
        verify(userRepository, times(TIME_OF_INVOCATION)).findById(USER_ID);
        verify(userRepository, times(TIME_OF_INVOCATION)).findByEmail(EMAIL);
        verify(userRepository, times(TIME_OF_INVOCATION)).findByPhoneNumber(PHONE);
        verify(fieldsValidation, times(TIME_OF_INVOCATION)).validateFields(REQUEST_DTO);
    }

    @Test
    public void UpdatePartUser_ShouldUpdateUser() {
        USER.setEmail(EMAIL);
        USER.setPhoneNumber(PHONE);
        REQUEST_DTO.setEmail(EMAIL);
        REQUEST_DTO.setPhoneNumber(PHONE);

        setupCommonMocksPartlyUpdate(USER, REQUEST_DTO);
        when(modelMapper.map(USER, UserResponseDto.class)).thenReturn(RESPONSE_DTO);
        when(userRepository.save(USER)).thenReturn(USER);

        assertEquals(userService.updatePartUser(USER_ID, new HashMap<>()), RESPONSE_DTO);

        verify(userRepository, times(TIME_OF_INVOCATION)).save(USER);
        verify(userRepository, times(TIME_OF_INVOCATION)).findById(USER_ID);
        verify(userRepository, never()).findByEmail(EMAIL);
        verify(userRepository, never()).findByPhoneNumber(PHONE);
        verify(fieldsValidation, times(TIME_OF_INVOCATION)).validateFields(REQUEST_DTO);
    }

    @Test
    public void UpdatePartUser_ThrowsNotFound_IfUserByIdNotFound() {

        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updatePartUser(USER_ID, new HashMap<>()))
                .isInstanceOf(NotFoundException.class);

        verify(userRepository, never()).save(USER);
        verify(userRepository, times(TIME_OF_INVOCATION)).findById(USER_ID);
        verify(userRepository, never()).findByEmail(EMAIL);
        verify(userRepository, never()).findByPhoneNumber(PHONE);
        verify(fieldsValidation, never()).validateFields(REQUEST_DTO);
    }

    @Test
    public void UpdatePartUser_ThrowsAlreadyExist_IfUserWithEmailExist() {
        USER.setEmail(EMAIL_2);
        USER.setPhoneNumber(PHONE_2);
        REQUEST_DTO.setEmail(EMAIL);
        REQUEST_DTO.setPhoneNumber(PHONE);

        setupCommonMocksPartlyUpdate(USER, REQUEST_DTO);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(USER));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(USER));

        assertThatThrownBy(() -> userService.updatePartUser(USER_ID, new HashMap<>()))
                .isInstanceOf(AlreadyExistsException.class);

        verify(userRepository, never()).save(USER);
        verify(userRepository, times(TIME_OF_INVOCATION)).findById(USER_ID);
        verify(userRepository, times(TIME_OF_INVOCATION)).findByEmail(EMAIL);
        verify(userRepository, never()).findByPhoneNumber(PHONE);
        verify(fieldsValidation, times(TIME_OF_INVOCATION)).validateFields(REQUEST_DTO);
    }

    @Test
    public void UpdatePartUser_ThrowsAlreadyExist_IfUserWithPhoneExist() {
        USER.setEmail(EMAIL_2);
        USER.setPhoneNumber(PHONE_2);
        REQUEST_DTO.setEmail(EMAIL);
        REQUEST_DTO.setPhoneNumber(PHONE);

        setupCommonMocksPartlyUpdate(USER, REQUEST_DTO);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumber(PHONE)).thenReturn(Optional.of(USER));

        assertThatThrownBy(() -> userService.updatePartUser(USER_ID, new HashMap<>()))
                .isInstanceOf(AlreadyExistsException.class);

        verify(userRepository, never()).save(USER);
        verify(userRepository, times(TIME_OF_INVOCATION)).findById(USER_ID);
        verify(userRepository, times(TIME_OF_INVOCATION)).findByEmail(EMAIL);
        verify(userRepository, times(TIME_OF_INVOCATION)).findByPhoneNumber(PHONE);
        verify(fieldsValidation, times(TIME_OF_INVOCATION)).validateFields(REQUEST_DTO);
    }

    @Test
    public void UpdatePartUser_ThrowsNotValidFieldsException_IfMapParametersNotValid() {

        when(modelMapper.map(USER, UserRequestDto.class)).thenReturn(REQUEST_DTO);
        doNothing().when(modelMapper).map(new HashMap<>(), REQUEST_DTO);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(USER));
        doThrow(new NotValidFieldsException()).when(fieldsValidation).validateFields(any(UserRequestDto.class));

        assertThatThrownBy(() -> userService.updatePartUser(USER_ID, new HashMap<>()))
                .isInstanceOf(NotValidFieldsException.class);

        verify(userRepository, never()).save(USER);
        verify(userRepository, times(TIME_OF_INVOCATION)).findById(USER_ID);
        verify(userRepository, never()).findByEmail(EMAIL);
        verify(userRepository, never()).findByPhoneNumber(PHONE);
        verify(fieldsValidation, times(TIME_OF_INVOCATION)).validateFields(REQUEST_DTO);
    }

    // -- findByDateBetween
    @Test
    public void FindByDateBetween_ShouldReturnListOfUsers() {
        Page<User> usersPage = new PageImpl<>(List.of(USER, USER));

        when(userRepository.findByBirthDateBetween(FROM_DATE, TO_DATE, PAGE_REQUEST)).thenReturn(usersPage);
        when(modelMapper.map(USER, UserResponseDto.class)).thenReturn(RESPONSE_DTO);

        Page<UserResponseDto> byDateBetween = userService.findByDateBetween(FROM_DATE, TO_DATE, PAGE_REQUEST);

        assertEquals(byDateBetween.getContent().size(), 2);
        assertEquals(byDateBetween.getTotalPages(), 1);
        assertEquals(byDateBetween.getTotalElements(), 2);

        verify(userRepository, times(TIME_OF_INVOCATION)).findByBirthDateBetween(FROM_DATE, TO_DATE, PAGE_REQUEST);
    }

    @Test
    public void FindByDateBetween_ThrowsNotValidFieldsException_IfStartDateAfterEndDate() {

        assertThatThrownBy(() -> userService.findByDateBetween(TO_DATE, FROM_DATE, PAGE_REQUEST))
                .isInstanceOf(NotValidFieldsException.class);

        verify(userRepository, never()).findByBirthDateBetween(TO_DATE, FROM_DATE, PAGE_REQUEST);
    }

    // -- private methods

    private void setupCommonMocksPartlyUpdate(User user, UserRequestDto requestDto) {
        when(modelMapper.map(user, UserRequestDto.class)).thenReturn(requestDto);
        when(modelMapper.map(requestDto, User.class)).thenReturn(user);
        doNothing().when(modelMapper).map(anyMap(), eq(requestDto));
        doNothing().when(fieldsValidation).validateFields(requestDto);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    }

    private void setupCommonMocksForUpdateFullUser(User user, UserRequestDto requestDto) {
        doNothing().when(modelMapper).map(requestDto, user);
        when(modelMapper.map(user, UserResponseDto.class)).thenReturn(RESPONSE_DTO);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

    }
}
