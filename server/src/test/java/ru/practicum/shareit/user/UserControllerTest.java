package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.UserNotExistObject;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {


    @MockBean
    private UserService service;

    @InjectMocks
    private UserController userController;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    private UserDto userDto = UserDto.builder()
            .id(1L)
            .name("Ваня")
            .email("ваня@mail.ru")
            .build();

    @Test
    @DisplayName("should save users")
    void saveUser() throws Exception {
        when(service.createUser(any()))
                .thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        verify(service, times(1))
                .createUser(any());
    }

    @Test
    @DisplayName("should return all users")
    void shouldReturnAllUsers() throws Exception {
        when(service.getAllUsers())
                .thenReturn(List.of(userDto));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(userDto.getName())))
                .andExpect(jsonPath("$.[0].email", is(userDto.getEmail())));

        verify(service, times(1))
                .getAllUsers();
    }

    @Test
    @DisplayName("should update users")
    void shouldUpdate() throws Exception {
        when(service.getUserById(anyLong()))
                .thenReturn(userDto);

        UserDto userDtoFromUpdate = UserDto.builder()
                .id(1L)
                .name("update")
                .email("update@mail.ru")
                .build();
        when(service.updateUser(any(), anyLong()))
                .thenReturn(userDtoFromUpdate);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDtoFromUpdate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoFromUpdate.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDtoFromUpdate.getName())))
                .andExpect(jsonPath("$.email", is(userDtoFromUpdate.getEmail())));

        verify(service, times(1))
                .updateUser(any(), anyLong());
    }

    @Test
    @DisplayName("should return users by id")
    void shouldReturnUserById() throws Exception {
        when(service.getUserById(anyLong()))
                .thenReturn(userDto);

        mvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        verify(service, times(1))
                .getUserById(anyLong());
    }

    @Test
    @DisplayName("should return not exist for users by id")
    void shouldReturnNotExist() throws Exception {
        when(service.getUserById(anyLong()))
                .thenThrow(new UserNotExistObject("user not exist"));

        mvc.perform(get("/users/1"))
                .andExpect(status().isNotFound());

        verify(service, times(1))
                .getUserById(anyLong());
    }

    @Test
    @DisplayName("should delete users")
    void shouldDeleteUser() throws Exception {
        List<UserDto> users = new ArrayList<>();
        users.add(userDto);
        when(service.getAllUsers())
                .thenReturn(users);

        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

    }
}
