package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.ItemDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;


@JsonTest
@DisplayName("Item json dto")
public class ItemDtoJsonTest {
    @Autowired
    private JacksonTester<ItemDto> jacksonTester;

    private ItemDto itemDto = new ItemDto(1L, "Пила", "Ей можно спилить дерево", true, null, null, null, null);

    @Test
    @DisplayName("should serialize")
    void testSerialize() throws IOException {
        var json = jacksonTester.write(itemDto);

        assertThat(json).hasJsonPath("$.id");
        assertThat(json).hasJsonPath("$.name");
        assertThat(json).hasJsonPath("$.description");
        assertThat(json).hasJsonPath("$.available");

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(itemDto.getId().intValue());
        assertThat(json).extractingJsonPathStringValue("$.name").isEqualTo(itemDto.getName());
        assertThat(json).extractingJsonPathStringValue("$.description").isEqualTo(itemDto.getDescription());
        assertThat(json).extractingJsonPathBooleanValue("$.available").isEqualTo(itemDto.getAvailable());
    }
}
