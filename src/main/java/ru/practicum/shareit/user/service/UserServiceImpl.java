package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        validateUser(userDto);
        checkExistEmail(userDto);
        log.info("Юзер " + userDto + " создан");
        User newUser = UserDtoMapper.toUser(userDto);
        return UserDtoMapper.toUserDto(userRepository.create(newUser));
    }

    @Override
    public UserDto updateUser(UserDto userDto, long id) {
        User newUser = UserDtoMapper.toUser(userDto);
        log.info("Юзер " + userDto + " обновлен");
        return UserDtoMapper.toUserDto(userRepository.update(newUser, id));
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.info("Количесвто юзеров " + userRepository.getAll().size());
        return userRepository.getAll().stream()
                .map(UserDtoMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(long id) {
        log.info("Юзер с id " + id + " получен");
        return UserDtoMapper.toUserDto(userRepository.getById(id));
    }

    @Override
    public void deleteUserById(long id) {
        log.info("Юзер с id " + id + " удален");
        userRepository.deleteById(id);
    }

    private void validateUser(UserDto userDto) {
        if (userDto.getName() == null || userDto.getEmail() == null ||
                userDto.getName().isEmpty() || userDto.getEmail().isEmpty()) {
            throw new ValidationException();
        }
    }

    private void checkExistEmail(UserDto userDto) {
        List<UserDto> usersDto = getAllUsers();
        for (UserDto u : usersDto) {
            if (u.getEmail().equals(userDto.getEmail())) {
                log.info("Юзер с email " + userDto.getEmail() + " уже существует");
                throw new RuntimeException("Юзер с данныи имейлом уже существует");
            }
        }
    }
}
