package ru.practicum.shareit.users;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.exception.UserNotExistObject;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    UserServiceImpl userService;

    UserRepository userRepository;

    UserDto userDto;

    User user;

    @BeforeEach
    public void beforeEach() {
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
        userDto = UserDto.builder()
                .id(1L)
                .name("Ваня")
                .email("иванов@mail.ru")
                .build();

        user = User.builder()
                .id(1L)
                .name("Ваня")
                .email("иванов@mail.ru")
                .build();
    }

    @Test
    @DisplayName("should find all user")
    void shouldFindAllUser() {
        List<User> users = new ArrayList<>();
        users.add(user);
        when(userRepository.findAll())
                .thenReturn(users);

        when(userRepository.save(any()))
                .thenReturn(user);

        userService.createUser(userDto);

        List<UserDto> userDtoList = userService.getAllUsers();

        assertThat(userDtoList.size(), equalTo(1));
        assertThat(userDtoList.get(0).getId(), equalTo(userDto.getId()));
        assertThat(userDtoList.get(0).getName(), equalTo(userDto.getName()));
        assertThat(userDtoList.get(0).getEmail(), equalTo(userDto.getEmail()));


        verify(userRepository, Mockito.times(2))
                .findAll();

    }

    @Test
    @DisplayName("should save user")
    void shouldSaveUser() {
        when(userRepository.save(any()))
                .thenReturn(user);

        UserDto userDtoMethod = userService.createUser(userDto);

        assertThat(userDtoMethod.getId(), equalTo(user.getId()));
        assertThat(userDtoMethod.getName(), equalTo(user.getName()));
        assertThat(userDtoMethod.getEmail(), equalTo(user.getEmail()));


        verify(userRepository, times(1))
                .save(any());


    }

    @Test
    @DisplayName("should return user by id")
    void shouldReturnUSerById() {
        when(userRepository.save(any()))
                .thenReturn(user);
        userService.createUser(userDto);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        UserDto receivedUser = userService.getUserById(1);

        assertThat(receivedUser.getId(), equalTo(user.getId()));
        assertThat(receivedUser.getName(), equalTo(user.getName()));
        assertThat(receivedUser.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    @DisplayName("should update user")
    void shouldUpdateUser() {
        when(userRepository.save(any()))
                .thenReturn(user);
        userService.createUser(userDto);

        User userFromUpdate = User.builder()
                .id(1L)
                .name("Рома")
                .email("Романов@mail.ru")
                .build();

        when(userRepository.save(any()))
                .thenReturn(userFromUpdate);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        UserDto receivedUserDto = userService.updateUser(UserDtoMapper.toUserDto(userFromUpdate), 1L);

        assertThat(receivedUserDto.getId(), equalTo(userFromUpdate.getId()));
        assertThat(receivedUserDto.getName(), equalTo(userFromUpdate.getName()));
        assertThat(receivedUserDto.getEmail(), equalTo(userFromUpdate.getEmail()));
    }

    @Test
    @DisplayName("should delete user")
    void shouldDeleteUser() {
        when(userRepository.save(any()))
                .thenReturn(user);
        UserDto userFromMethod = userService.createUser(userDto);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        UserDto receivedUser = userService.getUserById(1);
        assertThat(receivedUser.getId(), equalTo(userFromMethod.getId()));
        assertThat(receivedUser.getName(), equalTo(userFromMethod.getName()));
        assertThat(receivedUser.getEmail(), equalTo(userFromMethod.getEmail()));

        userService.deleteUserById(anyLong());
        verify(userRepository, times(1))
                .deleteById(anyLong());
    }

    @Test
    @DisplayName("should throw exception at find user by Id")
    void shouldThrowAtReturnItemById() {
        when(userRepository.save(any()))
                .thenReturn(user);
        userService.createUser(userDto);

        when(userRepository.findById(2L))
                .thenThrow(new UserNotExistObject("Юзера не существует"));

        Assertions.assertThrows(UserNotExistObject.class, () -> userService.getUserById(2));
    }
}
