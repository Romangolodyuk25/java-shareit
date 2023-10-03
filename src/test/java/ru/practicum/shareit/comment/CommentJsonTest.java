package ru.practicum.shareit.comment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentJsonTest {
    @Autowired
    private JacksonTester<CommentDto> jacksonTester;

    private CommentDto commentDto = new CommentDto(1L, "text" , "name", LocalDateTime.now());


    @Test
    @DisplayName("should serialize")
    void testSerialize() throws IOException {
        var json = jacksonTester.write(commentDto);

        assertThat(json).hasJsonPath("$.id");
        assertThat(json).hasJsonPath("$.text");
        assertThat(json).hasJsonPath("$.authorName");
        assertThat(json).hasJsonPath("$.created");

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(commentDto.getId().intValue());
        assertThat(json).extractingJsonPathStringValue("$.text").isEqualTo(commentDto.getText());
        assertThat(json).extractingJsonPathStringValue("$.authorName").isEqualTo(commentDto.getAuthorName());
        assertThat(json).extractingJsonPathStringValue("$.created").isEqualTo(commentDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}
