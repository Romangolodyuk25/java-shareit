package ru.practicum.shareit.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@DisplayName("User json dto")
public class UserDtoJsonTest {
    @Autowired
    private JacksonTester<UserDto> jacksonTester;

    private UserDto userDto = new UserDto(1L, "test", "test@mail.ru");

    @Test
    @DisplayName("should serialize")
    void testSerialize() throws IOException {
        var json = jacksonTester.write(userDto);

        assertThat(json).hasJsonPath("$.id");
        assertThat(json).hasJsonPath("$.name");
        assertThat(json).hasJsonPath("$.email");

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(userDto.getId().intValue());
        assertThat(json).extractingJsonPathStringValue("$.name").isEqualTo(userDto.getName());
        assertThat(json).extractingJsonPathStringValue("$.email").isEqualTo(userDto.getEmail());
    }
}
