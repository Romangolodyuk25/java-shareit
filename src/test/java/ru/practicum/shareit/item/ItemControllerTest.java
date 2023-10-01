package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.comment.service.CommentService;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

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
    private ItemService itemService;

    @MockBean
    private UserService userService;

    @MockBean
    private CommentService commentService;

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

    @Test
    @DisplayName("should save item")
    void saveItem() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);

        when(itemService.createItem(any(), anyLong()))
                .thenReturn(itemDto);

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

        verify(itemService, times(1))
                .createItem(any(), anyLong());
    }

    @Test
    @DisplayName("should return all items")
    void shouldReturnAllItems() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);

        when(itemService.getAllItemWithPagination(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items").header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemDto.getAvailable())));

        verify(itemService, times(1))
                .getAllItemWithPagination(anyLong(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("should update item")
    void shouldUpdate() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);
        when(itemService.getItemById(anyLong(), anyLong()))
                .thenReturn(itemDto);

        ItemDto itemDtoFromUpdate = ItemDto.builder()
                .id(1L)
                .name("update")
                .description("updateDescription")
                .available(false)
                .build();

        when(itemService.updateItem(any(), anyLong(), anyLong()))
                .thenReturn(itemDtoFromUpdate);

        mvc.perform(patch("/items/1").header(HEADER, 1)
                        .content(mapper.writeValueAsString(itemDtoFromUpdate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoFromUpdate.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoFromUpdate.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoFromUpdate.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoFromUpdate.getAvailable())));

        verify(itemService, times(1))
                .updateItem(any(), anyLong(), anyLong());
    }

    @Test
    @DisplayName("should return item by id")
    void shouldReturnItemById() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong()))
                .thenReturn(itemDto);
        mvc.perform(get("/items/1").header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));

        verify(itemService, times(1))
                .getItemById(anyLong(), anyLong());

    }

}
