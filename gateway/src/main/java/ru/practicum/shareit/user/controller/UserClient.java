package ru.practicum.shareit.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.ValidationException;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createUser(UserDto userDto) {
        validateUser(userDto);
        return post("", userDto);
    }

    public ResponseEntity<Object> updateUser(long id, UserDto userDto) {
        return patch("/" + id, userDto);
    }

    public ResponseEntity<Object> getUserById(long id) {
        return get("/" + id);
    }

    public ResponseEntity<Object> getAllUser() {
        return get("");
    }

    public ResponseEntity<Object> deleteUserById(long id) {
        return delete("/" + id);
    }



    private void validateUser(UserDto userDto) {
        if (userDto.getName() == null || userDto.getEmail() == null ||
                userDto.getName().isEmpty() || userDto.getEmail().isEmpty()) {
            throw new ValidationException();
        }
    }
}
