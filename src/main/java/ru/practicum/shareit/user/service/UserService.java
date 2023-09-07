package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto updateUser(UserDto userDto, long id);

    List<UserDto> getAllUsers();

    UserDto getUserById(long id);

    void deleteUserById(long id);

}
