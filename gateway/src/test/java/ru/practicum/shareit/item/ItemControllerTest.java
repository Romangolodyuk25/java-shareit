package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.comment.dto.CommentDtoIn;
import ru.practicum.shareit.item.controller.CommentClient;
import ru.practicum.shareit.item.controller.ItemClient;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.controller.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.ValidationException;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    public static final String HEADER = "X-Sharer-User-Id";

    @MockBean
    private ItemClient itemClient;

    @MockBean
    private UserClient userClient;

    @MockBean
    private CommentClient commentClient;

    @InjectMocks
    private ItemController itemController;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    private ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("Пила")
            .description("Ей можно спилить дерево")
            .available(true)
            .build();

    private UserDto userDto = UserDto.builder()
            .id(1L)
            .name("Ваня")
            .email("ваня@mail.ru")
            .build();

    private CommentDtoIn commentDtoIn = new CommentDtoIn("text");

    private ResponseEntity<Object> response = new ResponseEntity<>(itemDto, HttpStatus.OK);

    @Test
    @DisplayName("should throw validation exception item for create item")
    void shouldReturnBadRequestForSaveItem() throws Exception {
        when(itemClient.createItem(any(), anyLong()))
                .thenThrow(new ValidationException());

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1))
                .andExpect(status().isBadRequest());

        verify(itemClient, times(1))
                .createItem(any(), anyLong());
    }

    @Test
    @DisplayName("should save item")
    void saveItem() throws Exception {
        when(itemClient.createItem(any(), anyLong()))
                .thenReturn(response);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));

        verify(itemClient, times(1))
                .createItem(any(), anyLong());
    }

}
