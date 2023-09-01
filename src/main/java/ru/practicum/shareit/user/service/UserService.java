package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Component
public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto updateUser(UserDto userDto, long id);

    List<UserDto> getAllUsers();

    UserDto getUserById(long id);

    void deleteUserById(long id);

}
