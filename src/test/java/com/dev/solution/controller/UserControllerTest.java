package com.dev.solution.controller;

import com.dev.solution.exception.AlreadyExistsException;
import com.dev.solution.exception.NotFoundException;
import com.dev.solution.exception.NotValidFieldsException;
import com.dev.solution.exception.handler.ExceptionResponse;
import com.dev.solution.model.dto.DataResponse;
import com.dev.solution.model.dto.DataResponsePage;
import com.dev.solution.model.dto.UserRequestDto;
import com.dev.solution.model.dto.UserResponseDto;
import com.dev.solution.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    private final String JSON = "application/json";
    private final UserResponseDto USER_RESPONSE_DTO = new UserResponseDto();
    private final UserRequestDto USER_REQUEST_DTO = new UserRequestDto();
    private final DataResponse<UserResponseDto> RESPONSE_DTO = new DataResponse<>();
    private final DataResponsePage<UserResponseDto> RESPONSE_PAGE_DTO = new DataResponsePage<>();
    private final Long USER_ID = 1L;
    private final String USER_URL = "/users";
    private final List<UserResponseDto> USER_DTO_LIST = new ArrayList<>();
    private final LocalDate FROM_DATE = LocalDate.of(2024, 1, 1);
    private final LocalDate TO_DATE = LocalDate.of(2024, 12, 31);
    private final PageRequest PAGE_REQUEST = PageRequest.of(0, 10);
    private final int TIME_OF_INVOCATION = 1;
    private final String SLASH = "/";
    private final ExceptionResponse exceptionResponse = new ExceptionResponse();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    // --- findById --
    @Test
    void FindById_ShouldReturnUser() throws Exception {
        setUpUserResponseDto();
        RESPONSE_DTO.setData(USER_RESPONSE_DTO);

        when(userService.findById(USER_ID)).thenReturn(USER_RESPONSE_DTO);

        MvcResult mvcResult = mockMvc.perform(get(USER_URL + SLASH + USER_ID)
                .contentType(JSON)).andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(RESPONSE_DTO));

        verify(userService, times(TIME_OF_INVOCATION)).findById(USER_ID);
    }

    @Test
    void FindById_ThrowsNotFound_IfUserNotFound() throws Exception {
        when(userService.findById(USER_ID)).thenThrow(new NotFoundException());

        mockMvc.perform(get(USER_URL + SLASH + USER_ID)
                        .contentType(JSON))
                .andExpect(status().isNotFound());
        verify(userService, times(TIME_OF_INVOCATION)).findById(USER_ID);
    }

    // --- deleteById --
    @Test
    void DeleteById_ShouldReturnOk() throws Exception {

        doNothing().when(userService).deleteById(USER_ID);

        mockMvc.perform(delete(USER_URL + SLASH + USER_ID)
                .contentType(JSON)).andExpect(status().isOk());
        verify(userService, times(TIME_OF_INVOCATION)).deleteById(USER_ID);
    }

    // --- findByDateBetween --
    @Test
    void FindByDateBetween_ShouldReturnResponsePageDto() throws Exception {
        setUpUserResponseDto();
        USER_DTO_LIST.add(USER_RESPONSE_DTO);
        RESPONSE_PAGE_DTO.setData(USER_DTO_LIST);
        RESPONSE_PAGE_DTO.setTotalPages(1);
        RESPONSE_PAGE_DTO.setTotalElements(1);

        Page<UserResponseDto> usersPage = new PageImpl<>(USER_DTO_LIST);

        when(userService.findByDateBetween(FROM_DATE, TO_DATE, PAGE_REQUEST)).thenReturn(usersPage);

        MvcResult mvcResult = mockMvc.perform(get(USER_URL)
                        .param("from", FROM_DATE.toString())
                        .param("to", TO_DATE.toString())
                        .contentType(JSON))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(RESPONSE_PAGE_DTO));

        verify(userService, times(TIME_OF_INVOCATION)).findByDateBetween(FROM_DATE, TO_DATE, PAGE_REQUEST);
    }

    @Test
    void FindByDateBetween_ThrowsNotValidFields_IfStartDateAfterEndDate() throws Exception {
        when(userService.findByDateBetween(TO_DATE, FROM_DATE, PAGE_REQUEST))
                .thenThrow(new NotValidFieldsException());

        mockMvc.perform(get(USER_URL)
                        .param("from", TO_DATE.toString())
                        .param("to", FROM_DATE.toString())
                        .contentType(JSON))
                .andExpect(status().isBadRequest());

        verify(userService, times(TIME_OF_INVOCATION)).findByDateBetween(TO_DATE, FROM_DATE, PAGE_REQUEST);
    }

    @Test
    void FindByDateBetween_ThrowsBadRequest_IfFromOrToIsNull() throws Exception {

        LocalDate nullFromDate = null;
        mockMvc.perform(get(USER_URL)
                        .param("from", String.valueOf(nullFromDate))
                        .param("to", FROM_DATE.toString())
                        .contentType(JSON))
                .andExpect(status().isBadRequest());

        LocalDate nullToDate = null;
        mockMvc.perform(get(USER_URL)
                        .param("from", FROM_DATE.toString())
                        .param("to", String.valueOf(nullToDate))
                        .contentType(JSON))
                .andExpect(status().isBadRequest());

        verify(userService, never()).findByDateBetween(any(), any(), any());
    }

    // --- save --
    @Test
    void Save_SuccessfullySavesUser() throws Exception {
        setUpUserRequestDto();
        setUpUserResponseDto();
        RESPONSE_DTO.setData(USER_RESPONSE_DTO);

        when(userService.save(USER_REQUEST_DTO)).thenReturn(USER_RESPONSE_DTO);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(USER_URL)
                .contentType(JSON)
                .content(objectMapper.writeValueAsString(USER_REQUEST_DTO))).andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(RESPONSE_DTO));

        verify(userService, times(TIME_OF_INVOCATION)).save(USER_REQUEST_DTO);
    }

    @Test
    void Save_SuccessfullySavesUser_WithAllowableNullFields() throws Exception {
        setUpUserRequestDto();
        setUpUserResponseDto();
        RESPONSE_DTO.setData(USER_RESPONSE_DTO);
        USER_REQUEST_DTO.setAddress(null);
        USER_REQUEST_DTO.setPhoneNumber(null);

        when(userService.save(USER_REQUEST_DTO)).thenReturn(USER_RESPONSE_DTO);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(USER_URL)
                .contentType(JSON)
                .content(objectMapper.writeValueAsString(USER_REQUEST_DTO))).andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(RESPONSE_DTO));

        verify(userService, times(TIME_OF_INVOCATION)).save(USER_REQUEST_DTO);
    }

    @Test
    void Save_throwsMethodArgumentNotValidException_InvalidFields() throws Exception {
        setUpUserResponseDto();
        RESPONSE_DTO.setData(USER_RESPONSE_DTO);
        USER_REQUEST_DTO.setAddress("");
        USER_REQUEST_DTO.setPhoneNumber("");

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(USER_URL)
                        .contentType(JSON)
                        .content(objectMapper.writeValueAsString(USER_REQUEST_DTO)))
                .andReturn();

        mockMvc.perform(MockMvcRequestBuilders.post(USER_URL)
                        .contentType(JSON)
                        .content(objectMapper.writeValueAsString(USER_REQUEST_DTO)))
                .andExpect(status().isBadRequest());

        List<String> expectedErrorMessages = getInvalidFieldsErrorMessageExpected();
        List<String> actualErrorMessages = getInvalidFieldsErrorMessageActual(mvcResult.getResponse().getContentAsString());

        assertEquals(actualErrorMessages.size(), expectedErrorMessages.size());
        assertTrue(actualErrorMessages.containsAll(expectedErrorMessages));
        verify(userService, never()).save(USER_REQUEST_DTO);
    }

    @Test
    void Save_ThrowsAlreadyExists_DuplicateFields() throws Exception {
        setUpUserRequestDto();

        when(userService.save(USER_REQUEST_DTO)).thenThrow(new AlreadyExistsException());

        mockMvc.perform(MockMvcRequestBuilders.post(USER_URL)
                        .contentType(JSON)
                        .content(objectMapper.writeValueAsString(USER_REQUEST_DTO)))
                .andExpect(status().isConflict());

        verify(userService, times(TIME_OF_INVOCATION)).save(USER_REQUEST_DTO);
    }

    @Test
    void SaveUser_ThrowsBadRequest_NullDto() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post(USER_URL)
                        .contentType(JSON)
                        .content(objectMapper.writeValueAsString(null)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).save(any());
    }

    // --- updateFullUser --
    @Test
    void UpdateFullUser_SuccessfullyUpdateUser() throws Exception {
        setUpUserRequestDto();
        setUpUserResponseDto();
        RESPONSE_DTO.setData(USER_RESPONSE_DTO);

        when(userService.updateFullUser(USER_ID, USER_REQUEST_DTO)).thenReturn(USER_RESPONSE_DTO);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(USER_URL + SLASH + USER_ID)
                .contentType(JSON)
                .content(objectMapper.writeValueAsString(USER_REQUEST_DTO))).andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(RESPONSE_DTO));

        verify(userService, times(TIME_OF_INVOCATION)).updateFullUser(USER_ID, USER_REQUEST_DTO);
    }

    @Test
    void UpdateFullUser_SuccessfullyUpdateUser_WithNullAllowableFields() throws Exception {
        setUpUserRequestDto();
        setUpUserResponseDto();
        RESPONSE_DTO.setData(USER_RESPONSE_DTO);
        USER_REQUEST_DTO.setAddress(null);
        USER_REQUEST_DTO.setPhoneNumber(null);

        when(userService.updateFullUser(USER_ID, USER_REQUEST_DTO)).thenReturn(USER_RESPONSE_DTO);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(USER_URL + SLASH + USER_ID)
                .contentType(JSON)
                .content(objectMapper.writeValueAsString(USER_REQUEST_DTO))).andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(RESPONSE_DTO));

        verify(userService, times(TIME_OF_INVOCATION)).updateFullUser(USER_ID, USER_REQUEST_DTO);
    }

    @Test
    void UpdateFullUser_ThrowsMethodArgumentNotValidException_InvalidFields() throws Exception {
        setUpUserResponseDto();
        RESPONSE_DTO.setData(USER_RESPONSE_DTO);
        USER_REQUEST_DTO.setAddress("");
        USER_REQUEST_DTO.setPhoneNumber("");

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(USER_URL + SLASH + USER_ID)
                        .contentType(JSON)
                        .content(objectMapper.writeValueAsString(USER_REQUEST_DTO)))
                .andReturn();

        mockMvc.perform(MockMvcRequestBuilders.post(USER_URL)
                        .contentType(JSON)
                        .content(objectMapper.writeValueAsString(USER_REQUEST_DTO)))
                .andExpect(status().isBadRequest());

        List<String> expectedErrorMessages = getInvalidFieldsErrorMessageExpected();
        List<String> actualErrorMessages = getInvalidFieldsErrorMessageActual(mvcResult.getResponse().getContentAsString());

        assertTrue(actualErrorMessages.containsAll(expectedErrorMessages));
        verify(userService, never()).updateFullUser(USER_ID, USER_REQUEST_DTO);
    }

    @Test
    void UpdateFullUser_ThrowsAlreadyExists_DuplicateFields() throws Exception {
        setUpUserRequestDto();

        when(userService.updateFullUser(USER_ID, USER_REQUEST_DTO)).thenThrow(new AlreadyExistsException());

        mockMvc.perform(MockMvcRequestBuilders.put(USER_URL + SLASH + USER_ID)
                        .contentType(JSON)
                        .content(objectMapper.writeValueAsString(USER_REQUEST_DTO)))
                .andExpect(status().isConflict());

        verify(userService, times(TIME_OF_INVOCATION)).updateFullUser(USER_ID, USER_REQUEST_DTO);
    }

    @Test
    void UpdateFullUser_ThrowsNotFound_UserByIdNotFound() throws Exception {
        setUpUserRequestDto();

        when(userService.updateFullUser(USER_ID, USER_REQUEST_DTO)).thenThrow(new NotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.put(USER_URL + SLASH + USER_ID)
                        .contentType(JSON)
                        .content(objectMapper.writeValueAsString(USER_REQUEST_DTO)))
                .andExpect(status().isNotFound());

        verify(userService, times(TIME_OF_INVOCATION)).updateFullUser(USER_ID, USER_REQUEST_DTO);
    }

    @Test
    void UpdateFullUser_ThrowsBadRequest_NullDto() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.put(USER_URL + SLASH + USER_ID)
                        .contentType(JSON)
                        .content(objectMapper.writeValueAsString(null)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).updateFullUser(USER_ID, null);
    }

    // --- updatePartUser --
    @Test
    void UpdatePartUser_SuccessfullyUpdateUser() throws Exception {
        setUpUserResponseDto();
        RESPONSE_DTO.setData(USER_RESPONSE_DTO);

        when(userService.updatePartUser(USER_ID, new HashMap<>())).thenReturn(USER_RESPONSE_DTO);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch(USER_URL + SLASH + USER_ID)
                .contentType(JSON)
                .content(objectMapper.writeValueAsString(new HashMap<>()))).andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(RESPONSE_DTO));

        verify(userService, times(1)).updatePartUser(USER_ID, new HashMap<>());
    }

    @Test
    void UpdatePartUser_ThrowsMethodArgumentNotValidException_InvalidFields() throws Exception {
        when(userService.updatePartUser(USER_ID, new HashMap<>())).thenThrow(new NotValidFieldsException());

        mockMvc.perform(MockMvcRequestBuilders.post(USER_URL)
                        .contentType(JSON)
                        .content(objectMapper.writeValueAsString(new HashMap<>())))
                .andExpect(status().isBadRequest());

    }

    @Test
    void UpdatePartUser_ThrowsAlreadyExists_DuplicateFields() throws Exception {
        setUpUserRequestDto();

        when(userService.updatePartUser(USER_ID, new HashMap<>())).thenThrow(new AlreadyExistsException());

        mockMvc.perform(MockMvcRequestBuilders.patch(USER_URL + SLASH + USER_ID)
                        .contentType(JSON)
                        .content(objectMapper.writeValueAsString(new HashMap<>())))
                .andExpect(status().isConflict());

        verify(userService, times(1)).updatePartUser(USER_ID, new HashMap<>());
    }

    @Test
    void UpdatePartUser_ThrowsNotFound_UserByIdNotFound() throws Exception {

        when(userService.updatePartUser(USER_ID, new HashMap<>())).thenThrow(new NotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.patch(USER_URL + SLASH + USER_ID)
                        .contentType(JSON)
                        .content(objectMapper.writeValueAsString(new HashMap<>())))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).updatePartUser(USER_ID, new HashMap<>());
    }

    @Test
    void UpdatePartUser_ThrowsBadRequest_NullParameters() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.patch(USER_URL + SLASH + USER_ID)
                        .contentType(JSON)
                        .content(objectMapper.writeValueAsString(null)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).updatePartUser(anyLong(), anyMap());
    }

    private List<String> getInvalidFieldsErrorMessageActual(String actualResponseBody) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(actualResponseBody);
        JsonNode messageNode = jsonNode.get("message");
        List<String> actualErrorMessages = new ArrayList<>();
        if (messageNode != null && messageNode.isArray()) {
            for (JsonNode errorMessage : messageNode) {
                actualErrorMessages.add(errorMessage.asText());
            }
        }
        return actualErrorMessages;
    }

    private List<String> getInvalidFieldsErrorMessageExpected() {
        return Arrays.asList(
                "Birth Date can not be null.",
                "Last name can not be null.",
                "Field can not be blank.",
                "Email can not be null.",
                "Last name can not be empty.",
                "First name can not be empty.",
                "First name can not be null.",
                "Age must be over 18.",
                "Email can not be empty.",
                "Invalid email format.",
                "Field can not be blank."
        );
    }


    private void setUpUserResponseDto() {
        USER_RESPONSE_DTO.setId(USER_ID);
        USER_RESPONSE_DTO.setAddress("Address");
        USER_RESPONSE_DTO.setBirthDate(LocalDate.of(1995, 1, 1));
        USER_RESPONSE_DTO.setFirstName("FirstName");
        USER_RESPONSE_DTO.setPhoneNumber("+380");
        USER_RESPONSE_DTO.setLastName("LastName");
        USER_RESPONSE_DTO.setEmail("email");
    }

    private void setUpUserRequestDto() {
        USER_REQUEST_DTO.setAddress("Address");
        USER_REQUEST_DTO.setBirthDate(LocalDate.of(1995, 1, 1));
        USER_REQUEST_DTO.setFirstName("FirstName");
        USER_REQUEST_DTO.setPhoneNumber("+380");
        USER_REQUEST_DTO.setLastName("LastName");
        USER_REQUEST_DTO.setEmail("email@gmail.com");
    }

}
