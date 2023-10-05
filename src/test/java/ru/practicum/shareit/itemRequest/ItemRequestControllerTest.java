package ru.practicum.shareit.itemRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.BookingNotExistException;
import ru.practicum.shareit.exception.ItemRequestNotExist;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {

    public static final String HEADER = "X-Sharer-User-Id";

    @MockBean
    private ItemService itemService;

    @MockBean
    private UserService userService;

    @MockBean
    private ItemRequestService itemRequestService;

    @InjectMocks
    private ItemRequestController itemRequestController;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    private UserDto userDto = UserDto.builder()
            .id(1L)
            .name("Ваня")
            .email("ваня@mail.ru")
            .build();

    private ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .id(1L)
            .description("testRequest")
            .created(LocalDateTime.now().plusHours(1))
            .build();


    @Test
    @DisplayName("should save itemRequest")
    void saveItem() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);

        when(itemRequestService.createRequest(any(), anyLong()))
                .thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));

        verify(itemRequestService, times(1))
                .createRequest(any(), anyLong());
    }

    @Test
    @DisplayName("should return all request for owner")
    void shouldReturnAllItems() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);

        when(itemRequestService.getAllRequestsForOwner(anyLong()))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests").header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.[0].created", is(itemRequestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));

        verify(itemRequestService, times(1))
                .getAllRequestsForOwner(anyLong());
    }

    @Test
    @DisplayName("should not return itemRequest by id")
    void shouldNotReturnItemRequestById() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);
        when(itemRequestService.getRequestById(anyLong(), anyLong()))
                .thenThrow(new ItemRequestNotExist("Запроса не существует"));

        mvc.perform(get("/requests/9999").header(HEADER, 1))
                .andExpect(status().isNotFound());

    }

}
