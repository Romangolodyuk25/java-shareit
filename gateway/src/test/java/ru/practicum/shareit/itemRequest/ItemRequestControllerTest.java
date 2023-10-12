package ru.practicum.shareit.itemRequest;

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
import ru.practicum.shareit.item.controller.ItemClient;
import ru.practicum.shareit.request.controller.ItemRequestClient;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.controller.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.ValidationException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {

    public static final String HEADER = "X-Sharer-User-Id";

    @MockBean
    private ItemClient itemClient;

    @MockBean
    private UserClient userClient;

    @MockBean
    private ItemRequestClient itemRequestClient;

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

    private ResponseEntity<Object> response = new ResponseEntity<>(itemRequestDto, HttpStatus.OK);


    @Test
    @DisplayName("should create request for")
    void shouldReturnAllItems() throws Exception {
        when(itemRequestClient.createRequest(any(), anyLong()))
                .thenThrow(new ValidationException());

        mvc.perform(post("/requests").header(HEADER, 1))
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("should save itemRequest")
    void saveItem() throws Exception {
        when(itemRequestClient.createRequest(any(), anyLong()))
                .thenReturn(response);

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

        verify(itemRequestClient, times(1))
                .createRequest(any(), anyLong());
    }

}
