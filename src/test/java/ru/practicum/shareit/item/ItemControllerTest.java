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
import ru.practicum.shareit.exception.ItemNotExistException;
import ru.practicum.shareit.exception.UserNotExistObject;
import ru.practicum.shareit.item.comment.dto.CommentDtoIn;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.service.CommentService;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ValidationException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
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

    private Comment comment = Comment.builder()
            .id(1L)
            .author(UserDtoMapper.toUser(userDto))
            .item(ItemDtoMapper.toItem(itemDto, UserDtoMapper.toUser(userDto), ItemRequest.builder()
                    .id(1L)
                    .requestor(UserDtoMapper.toUser(userDto))
                    .description("request")
                    .created(LocalDateTime.now())
                    .build()))
            .created(LocalDateTime.now())
            .text("Comment")
            .build();
    private CommentDtoIn commentDtoIn = new CommentDtoIn("text");

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
    @DisplayName("should throw validation exception item for create item")
    void shouldReturnBadRequestForSaveItem() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);

        when(itemService.createItem(any(), anyLong()))
                .thenThrow(new ValidationException());

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1))
                .andExpect(status().isBadRequest());

        verify(itemService, times(1))
                .createItem(any(), anyLong());
    }

    @Test
    @DisplayName("should throw not exist user exception item for create item")
    void shouldNotFoundForSaveItem() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);

        when(itemService.createItem(any(), anyLong()))
                .thenThrow(new UserNotExistObject("user not found"));

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1))
                .andExpect(status().isNotFound());

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
    @DisplayName("should search items")
    void shouldSearchItems() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);

        List<ItemDto> list = List.of(itemDto);
        when(itemService.searchItemsWithPagination("ПиЛит", userDto.getId(), 0, 1))
                .thenReturn(list);

        mvc.perform(get("/items/search?text=ПиЛит").header(HEADER, userDto.getId())
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
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
    @DisplayName("should throw not exist for update item")
    void shouldThrowExceptionForUpdateWhereHeaderNotExist() throws Exception {

        ItemDto itemDtoFromUpdate = ItemDto.builder()
                .id(1L)
                .name("update")
                .description("updateDescription")
                .available(false)
                .build();

        when(itemService.updateItem(any(), anyLong(), anyLong()))
                .thenThrow(new UserNotExistObject("user not exist"));

        mvc.perform(patch("/items/1").header(HEADER, 1)
                        .content(mapper.writeValueAsString(itemDtoFromUpdate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

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

    @Test
    @DisplayName("should return not exist for item by id")
    void shouldReturnNotExistForItemById() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong()))
                .thenThrow(new ItemNotExistException("item not exist"));
        mvc.perform(get("/items/1").header(HEADER, 1))
                .andExpect(status().isNotFound());

        verify(itemService, times(1))
                .getItemById(anyLong(), anyLong());

    }
}
