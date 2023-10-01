package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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
    private BookingService bookingService;

    @MockBean
    private ItemService itemService;

    @MockBean
    private UserService userService;

    @InjectMocks
    private BookingController bookingController;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    private Item item = Item.builder()
            .id(1L)
            .name("Пила")
            .description("Ей можно спилить дерево")
            .available(true)
            .build();

    private User user = User.builder()
            .id(1L)
            .name("Ваня")
            .email("ваня@mail.ru")
            .build();

    private BookingDto bookingDto = BookingDto.builder()
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


    @Test
    @DisplayName("should save booking")
    void saveBooking() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(UserDtoMapper.toUserDto(user));
        when(itemService.getItemById(anyLong(), anyLong()))
                .thenReturn(ItemDtoMapper.toItemDto(item, new ArrayList<>()));
        when(bookingService.createBooking(any(), anyLong()))
                .thenReturn(bookingDto);

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

        verify(bookingService, times(1))
                .createBooking(any(), anyLong());
    }

    @Test
    @DisplayName("should return all bookings by user Id")
    void shouldReturnAllBookingsByUserId() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(UserDtoMapper.toUserDto(user));

        when(bookingService.getAllBookingsByUserIdAndState(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings").header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start", is(bookingDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].end", is(bookingDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].status", is(bookingDto.getStatus().name())))
                .andExpect(jsonPath("$.[0].booker.id", is(bookingDto.getBooker().getId().intValue())))
                .andExpect(jsonPath("$.[0].item.id", is(bookingDto.getItem().getId().intValue())));

        verify(bookingService, times(1))
                .getAllBookingsByUserIdAndState(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("should update booking")
    void shouldUpdateBooking() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(UserDtoMapper.toUserDto(user));

        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        BookingDto bookingDtoFromUpdate = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.now().plusHours(10))
                .end(LocalDateTime.now().plusDays(12))
                .booker(user)
                .item(item)
                .status(Status.WAITING)
                .build();

        when(bookingService.updateBooking(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(bookingDtoFromUpdate);

        mvc.perform(patch("/bookings/1").param("approved", "true").header(HEADER, 1)
                        .content(mapper.writeValueAsString(bookingDtoFromUpdate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoFromUpdate.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDtoFromUpdate.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(bookingDtoFromUpdate.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.status", is(bookingDtoFromUpdate.getStatus().name())))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoFromUpdate.getBooker().getId().intValue())))
                .andExpect(jsonPath("$.item.id", is(bookingDtoFromUpdate.getItem().getId().intValue())));

        verify(bookingService, times(1))
                .updateBooking(anyLong(), anyBoolean(), anyLong());
    }

    @Test
    @DisplayName("should return booking by id")
    void shouldReturnBookingById() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(UserDtoMapper.toUserDto(user));

        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/1").header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().name())))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId().intValue())))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId().intValue())));

        verify(bookingService, times(1))
                .getBookingById(anyLong(), anyLong());
    }
}
