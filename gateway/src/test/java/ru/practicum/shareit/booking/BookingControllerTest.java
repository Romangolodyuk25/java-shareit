package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.controller.BookingClient;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.controller.ItemClient;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.controller.UserClient;
import ru.practicum.shareit.user.model.User;

import javax.validation.ValidationException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    public static final String HEADER = "X-Sharer-User-Id";

    @MockBean
    private BookingClient bookingClient;

    @MockBean
    private ItemClient itemClient;

    @MockBean
    private UserClient userClient;

    @InjectMocks
    private BookingController bookingController;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    private final Item item = Item.builder()
            .id(1L)
            .name("Пила")
            .description("Ей можно спилить дерево")
            .available(true)
            .build();

    private final User user = User.builder()
            .id(1L)
            .name("Ваня")
            .email("ваня@mail.ru")
            .build();

    private final BookingDto bookingDto = BookingDto.builder()
            .id(1L)
            .start(LocalDateTime.now().plusHours(1))
            .end(LocalDateTime.now().plusDays(1))
            .booker(user)
            .item(item)
            .status(Status.WAITING)
            .build();

    private BookingDtoIn bookingDtoIn = BookingDtoIn.builder()
            .itemId(2L)
            .start(LocalDateTime.now().plusHours(1))
            .end(LocalDateTime.now().plusDays(1))
            .build();

    private ResponseEntity<Object> response = new ResponseEntity<>(bookingDto, HttpStatus.OK);

    @Test
    @DisplayName("should not save booking")
    void shouldReturnNotFoundForSaveBookingUserNotExist() throws Exception {

        when(bookingClient.bookItem(anyLong(), any()))
                .thenThrow(new UserNotExistObject("user not exist"));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 100))
                .andExpect(status().isNotFound());

        verify(bookingClient, times(1))
                .bookItem(anyLong(), any());
    }

    @Test
    @DisplayName("should not save booking")
    void shouldReturnNotFoundForSaveBookingUserIsOwner() throws Exception {

        when(bookingClient.bookItem(anyLong(), any()))
                .thenThrow(new UserIsOwnerException("Юзер является владельцем вещи"));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 100))
                .andExpect(status().isNotFound());

        verify(bookingClient, times(1))
                .bookItem(anyLong(), any());
    }

    @Test
    @DisplayName("should not save booking")
    void shouldReturnIsNotAvailableException() throws Exception {

        when(bookingClient.bookItem(anyLong(), any()))
                .thenThrow(new IsNotAvailableException("Вещь нельзя забронировать"));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 100))
                .andExpect(status().isBadRequest());

        verify(bookingClient, times(1))
                .bookItem(anyLong(), any());
    }


    @Test
    @DisplayName("should not save booking")
    void shouldReturnNotFoundForSaveBookingItemNotExist() throws Exception {

        when(bookingClient.bookItem(anyLong(), any()))
                .thenThrow(new ItemNotExistException("item not exist"));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 100))
                .andExpect(status().isNotFound());

        verify(bookingClient, times(1))
                .bookItem(anyLong(), any());
    }

    @Test
    @DisplayName("should not save booking validation")
    void shouldReturnValidationExceptionForSaveBooking() throws Exception {

        when(bookingClient.bookItem(anyLong(), any()))
                .thenThrow(new ValidationException("validation"));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1))
                .andExpect(status().isBadRequest());

        verify(bookingClient, times(1))
                .bookItem(anyLong(), any());
    }

    @Test
    @DisplayName("should save booking ")
    void shouldSaveBooking() throws Exception {

        when(bookingClient.bookItem(anyLong(), any()))
                .thenReturn(response);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().name())))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId().intValue())))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId().intValue())));

        verify(bookingClient, times(1))
                .bookItem(anyLong(), any());
    }
}
