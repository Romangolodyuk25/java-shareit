package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/users")
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Create user userDto {}", userDto);
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@Valid @RequestBody UserDto userDto, @PathVariable Long id) {
        log.info("Update user userDto {}, id {}", userDto, id);
        return userClient.updateUser(id, userDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        return userClient.getAllUser();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable long id) {
        log.info("Get by id {}", id);
        return userClient.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable long id) {
        log.info("Delete by id {}", id);
        userClient.deleteUserById(id);
    }
}
