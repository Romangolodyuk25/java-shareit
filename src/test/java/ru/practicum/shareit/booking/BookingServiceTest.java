package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.BookingNotExistException;
import ru.practicum.shareit.exception.ItemNotExistException;
import ru.practicum.shareit.exception.UserIsOwnerException;
import ru.practicum.shareit.exception.UserNotExistObject;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    private BookingRepository bookingRepository;
    private UserRepository userRepository;
    private ItemRepository itemRepository;
    private BookingService bookingService;

    User user;
    User user2;
    UserDto userDto;

    Item item;
    ItemDto itemDto;

    Booking booking;
    BookingDto bookingDto;
    BookingDtoIn bookingDtoIn;

    @BeforeEach
    void beforeEach() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        bookingRepository = mock(BookingRepository.class);
        bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);
        user = User.builder()
                .id(1L)
                .name("test")
                .email("test@mail.ru")
                .build();
        user2 = User.builder()
                .id(2L)
                .name("test2")
                .email("test2@mail.ru")
                .build();
        item = Item.builder()
                .id(1L)
                .name("Вещь 1")
                .description("Умеет что-то")
                .available(true)
                .owner(user)
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .name("test")
                .email("test@mail.ru")
                .build();

        itemDto = ItemDto.builder()
                .id(1L)
                .name("Вещь 1")
                .description("Умеет что-то")
                .available(true)
                .build();

        bookingDtoIn = BookingDtoIn.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .build();

        booking = Booking.builder()
                .start(bookingDtoIn.getStart())
                .end(bookingDtoIn.getEnd())
                .item(item)
                .booker(user)
                .status(Status.WAITING)
                .build();

        bookingDto = BookingDto.builder()
                .id(1L)
                .start(bookingDtoIn.getStart())
                .end(bookingDtoIn.getEnd())
                .booker(user)
                .item(item)
                .status(Status.WAITING)
                .build();
    }

    @Test
    @DisplayName("should create booking")
    void shouldCreateBooking() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(booking))
                .thenReturn(booking);

        BookingDto receivedBooking = bookingService.createBooking(bookingDtoIn, 2);

        assertThat(receivedBooking.getId(), equalTo(booking.getId()));
        assertThat(receivedBooking.getStatus(), equalTo(booking.getStatus()));
        assertThat(receivedBooking.getStart(), equalTo(booking.getStart()));
        assertThat(receivedBooking.getEnd(), equalTo(booking.getEnd()));
        assertThat(receivedBooking.getItem(), equalTo(booking.getItem()));
    }

    @Test
    @DisplayName("should not create booking if user not exist")
    void shouldReturnUserNotExistForSaveBooking() {
        when(userRepository.findById(anyLong()))
                .thenThrow(new UserNotExistObject("user not exist"));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        assertThrows(UserNotExistObject.class, () -> bookingService.createBooking(bookingDtoIn, 2));

    }

    @Test
    @DisplayName("should not create booking if item not exist")
    void shouldReturnItemNotExistForSaveBooking() {
        when(itemRepository.findById(anyLong()))
                .thenThrow(new ItemNotExistException("item not exist"));

        assertThrows(ItemNotExistException.class, () -> bookingService.createBooking(bookingDtoIn, 2));
    }

    @Test
    @DisplayName("should return validation exception for save booking")
    void shouldReturnValidationExceptionForSaveBookingStartIsEmpty() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        bookingDtoIn.setStart(null);
        assertThrows(ValidationException.class, () -> bookingService.createBooking(bookingDtoIn, 2));
    }

    @Test
    @DisplayName("should return validation exception for save booking")
    void shouldReturnValidationExceptionForSaveBookingStartIsBeforeNow() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        bookingDtoIn.setStart(LocalDateTime.now().minusDays(1));
        assertThrows(ValidationException.class, () -> bookingService.createBooking(bookingDtoIn, 2));
    }

    @Test
    @DisplayName("should return validation exception for save booking")
    void shouldReturnValidationExceptionForSaveBookingEndIsBeforeNow() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        bookingDtoIn.setItemId(itemDto.getId());
        bookingDtoIn.setEnd(LocalDateTime.now().minusDays(1));
        assertThrows(ValidationException.class, () -> bookingService.createBooking(bookingDtoIn, 2));
    }

    @Test
    @DisplayName("should return validation exception for save booking")
    void shouldReturnValidationExceptionForSaveBookingEndIsEmpty() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        bookingDtoIn.setEnd(null);
        assertThrows(ValidationException.class, () -> bookingService.createBooking(bookingDtoIn, 2));
    }

    @Test
    @DisplayName("should get all booking")
    void shouldGetAllBooking() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(booking))
                .thenReturn(booking);
        bookingService.createBooking(bookingDtoIn, 2);


        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);

        when(bookingRepository.findAll())
                .thenReturn(bookings);

        List<BookingDto> receivedBookingList = bookingService.getAllBookings(1);

        assertThat(receivedBookingList.get(0).getId(), equalTo(booking.getId()));
        assertThat(receivedBookingList.get(0).getStatus(), equalTo(booking.getStatus()));
        assertThat(receivedBookingList.get(0).getStart(), equalTo(booking.getStart()));
        assertThat(receivedBookingList.get(0).getEnd(), equalTo(booking.getEnd()));
        assertThat(receivedBookingList.get(0).getItem(), equalTo(booking.getItem()));
    }

    @Test
    @DisplayName("should find booking by Id")
    void shouldReturnBookingById() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(booking))
                .thenReturn(booking);
        bookingService.createBooking(bookingDtoIn, 2);

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));

        BookingDto receivedBooking = bookingService.getBookingById(1, 1);

        assertThat(receivedBooking.getId(), equalTo(booking.getId()));
        assertThat(receivedBooking.getStatus(), equalTo(booking.getStatus()));
        assertThat(receivedBooking.getStart(), equalTo(booking.getStart()));
        assertThat(receivedBooking.getEnd(), equalTo(booking.getEnd()));
        assertThat(receivedBooking.getItem(), equalTo(booking.getItem()));
    }

    @Test
    @DisplayName("should throw exception for booking by Id")
    void shouldThrowExceptionForBookingById() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        when(bookingRepository.save(booking))
                .thenReturn(booking);
        bookingService.createBooking(bookingDtoIn, 2);

        when(bookingRepository.findById(3L))
                .thenThrow(new BookingNotExistException("Брони не существует"));

        assertThrows(BookingNotExistException.class, () -> bookingService.getBookingById(3, 1));
    }

    @Test
    @DisplayName("should get all bookings by user id and state")
    void shouldNotGetAllBookingsByUserIdAndState() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        assertThrows(UserIsOwnerException.class, () -> bookingService.createBooking(bookingDtoIn, bookingDto.getId()));
    }
}
