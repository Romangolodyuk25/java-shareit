package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class BookingServiceImplTest {
    private final EntityManager em;
    private final UserService userService;
    private final BookingService bookingService;
    private final ItemService itemService;

    UserDto userDto1;
    UserDto userDto2;

    ItemDto itemDto;
    ItemDto itemDto2;

    Booking booking;
    ItemRequest itemRequest;

    @BeforeEach
    public void beforeEach() {
        userDto1 = userService.createUser(UserDto.builder()
                .name("test")
                .email("test@mail.ru")
                .build());
        userDto2 = userService.createUser(UserDto.builder()
                .name("test2")
                .email("test@mail2.ru")
                .build());
        itemDto = itemService.createItem(ItemDto.builder()
                .name("Пила")
                .description("Что-то пилит")
                .available(true)
                .build(), userDto1.getId());

        itemDto2 = itemService.createItem(ItemDto.builder()
                .name("Молоток")
                .description("Что-то стучит")
                .available(false)
                .build(), userDto1.getId());

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Запрос")
                .created(LocalDateTime.now())
                .requestor(UserDtoMapper.toUser(userDto2))
                .build();

        booking = Booking.builder()
                .id(1L)
                .item(ItemDtoMapper.toItem(itemDto, UserDtoMapper.toUser(userDto1),itemRequest))
                .booker(UserDtoMapper.toUser(userDto2))
                .status(Status.WAITING)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .build();

    }

    @Test
    @DisplayName("should create booking")
    void shouldCreateBooking() {
        BookingDto bookingDto;
        BookingDtoIn bookingDtoIn = makeBookingDto(itemDto.getId(), LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(1));
        bookingDto = bookingService.createBooking(bookingDtoIn, userDto2.getId());

        TypedQuery<Booking> query = em.createQuery("select b from Booking as b where b.id = :id", Booking.class);
        Booking booking = query.setParameter("id", bookingDto.getId())
                .getSingleResult();

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStart(), equalTo(bookingDtoIn.getStart()));
        assertThat(booking.getEnd(), equalTo(bookingDtoIn.getEnd()));
        assertThat(booking.getStatus(), equalTo(Status.WAITING));
    }

    @Test
    @DisplayName("should throw user is owner exception booking")
    void shouldThrowExceptionUserIsOwnerBooking() {
        BookingDto bookingDto;
        BookingDtoIn bookingDtoIn = makeBookingDto(itemDto.getId(), LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(1));
        assertThrows(UserIsOwnerException.class, () -> bookingService.createBooking(bookingDtoIn, userDto1.getId()));
    }

    @Test
    @DisplayName("should update booking")
    void shouldUpdateBooking() {
        ItemDto itemDto3 = itemService.createItem(ItemDto.builder()
                .name("Дрель")
                .description("Что-то сверлит")
                .available(true)
                .build(), userDto1.getId());

        BookingDto bookingDto;
        BookingDtoIn bookingDtoIn = makeBookingDto(itemDto3.getId(), LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(1));
        bookingDto = bookingService.createBooking(bookingDtoIn, userDto2.getId());

        BookingDto updateBooking = bookingService.updateBooking(bookingDto.getId(), true, userDto1.getId());
        assertThat(updateBooking.getStatus().name(), equalTo(Status.APPROVED.name()));
    }

    @Test
    @DisplayName("should throw exception status already exist update booking")
    void shouldThrowStatusAlreadyExistUpdateBooking() {
        ItemDto itemDto3 = itemService.createItem(ItemDto.builder()
                .name("Дрель")
                .description("Что-то сверлит")
                .available(true)
                .build(), userDto1.getId());

        BookingDto bookingDto;
        BookingDtoIn bookingDtoIn = makeBookingDto(itemDto3.getId(), LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(1));
        bookingDto = bookingService.createBooking(bookingDtoIn, userDto2.getId());

        BookingDto updateBooking = bookingService.updateBooking(bookingDto.getId(), true, userDto1.getId());
        assertThat(updateBooking.getStatus().name(), equalTo(Status.APPROVED.name()));

        assertThrows(StatusAlreadyApprovedException.class, () -> bookingService.updateBooking(bookingDto.getId(), true, userDto1.getId()));
    }

    @Test
    @DisplayName("should throw is not owner exception for update booking")
    void shouldThrowIsNotOwnerExceptionForUpdateBooking() {
        BookingDtoIn bookingDtoIn = makeBookingDto(itemDto.getId(), LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(1));
        BookingDto bookingDto = bookingService.createBooking(bookingDtoIn, userDto2.getId());

        assertThrows(IsNotOwnerException.class, () -> bookingService.updateBooking(bookingDto.getId(), true, userDto2.getId()));

    }

    @Test
    @DisplayName("should throw exception for create booking item available false")
    void shouldThrowValidateExceptionCreateBookingItemAvailable() {
        BookingDtoIn bookingDtoIn = makeBookingDto(itemDto2.getId(), LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(1));

        assertThrows(IsNotAvailableException.class, () -> bookingService.createBooking(bookingDtoIn, userDto2.getId()));
    }

    @Test
    @DisplayName("should throw exception for create booking empty start")
    void shouldThrowValidateExceptionCreateBookingStartEmpty() {
        BookingDtoIn bookingDtoIn = makeBookingDto(itemDto.getId(), null, LocalDateTime.now().plusDays(1));
        assertThrows(ValidationException.class, () -> bookingService.createBooking(bookingDtoIn, userDto2.getId()));
    }

    @Test
    @DisplayName("should throw exception for create booking start is before now")
    void shouldThrowValidateExceptionCreateBookingStartIsBeforeNow() {
        BookingDtoIn bookingDtoIn = makeBookingDto(itemDto.getId(), LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(1));
        assertThrows(ValidationException.class, () -> bookingService.createBooking(bookingDtoIn, userDto2.getId()));
    }

    @Test
    @DisplayName("should throw exception for create booking start is equal end")
    void shouldThrowValidateExceptionCreateBookingStartIsEqualEnd() {
        BookingDtoIn bookingDtoIn = makeBookingDto(itemDto.getId(), LocalDateTime.of(2024, 5, 10, 10, 0), LocalDateTime.of(2024, 5, 10, 10, 0));
        assertThrows(ValidationException.class, () -> bookingService.createBooking(bookingDtoIn, userDto2.getId()));
    }

    @Test
    @DisplayName("should throw exception for create booking empty end")
    void shouldThrowValidateExceptionCreateBookingEndEmpty() {
        BookingDtoIn bookingDtoIn = makeBookingDto(itemDto.getId(), LocalDateTime.now().plusHours(1), null);
        assertThrows(ValidationException.class, () -> bookingService.createBooking(bookingDtoIn, userDto2.getId()));

    }

    @Test
    @DisplayName("should throw exception for create booking end is Before now")
    void shouldThrowValidateExceptionCreateBookingEndIsBeforeNow() {
        BookingDtoIn bookingDtoIn = makeBookingDto(itemDto.getId(), LocalDateTime.now().plusHours(1), LocalDateTime.now().minusDays(1));
        assertThrows(ValidationException.class, () -> bookingService.createBooking(bookingDtoIn, userDto2.getId()));

    }

    @Test
    @DisplayName("should throw exception for create booking end is Before start")
    void shouldThrowValidateExceptionCreateBookingEndIsBeforeStart() {
        BookingDtoIn bookingDtoIn = makeBookingDto(itemDto.getId(), LocalDateTime.now().plusHours(1), LocalDateTime.now());
        assertThrows(ValidationException.class, () -> bookingService.createBooking(bookingDtoIn, userDto2.getId()));

    }

    @Test
    @DisplayName("should return all bookings")
    void shouldReturnAllUsers() {
        BookingDtoIn bookingDtoIn = BookingDtoIn.builder()
                .itemId(itemDto.getId())
                .start(LocalDateTime.now().plusHours(2))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        bookingService.createBooking(bookingDtoIn, userDto2.getId());
        List<BookingDto> bookingDtoList = bookingService.getAllBookings(userDto2.getId());
        assertThat(bookingDtoList.size(), equalTo(1));
    }

    @Test
    @DisplayName("should return empty list for all bookings")
    void shouldReturnEmptyListForAllUsers() {
        List<BookingDto> bookingDtoList = bookingService.getAllBookings(userDto2.getId());
        assertThat(bookingDtoList.size(), equalTo(0));
    }

    @Test
    @DisplayName("should return exception for all bookings")
    void shouldReturnExceptionForAllUsers() {
        BookingDtoIn bookingDtoIn = BookingDtoIn.builder()
                .itemId(itemDto.getId())
                .start(LocalDateTime.now().plusHours(2))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        bookingService.createBooking(bookingDtoIn, userDto2.getId());

        assertThrows(UserNotExistObject.class, () -> bookingService.getAllBookings(9999));
    }

    @Test
    @DisplayName("Should get all bookings by user id and state ALL")
    void shouldGetAllBookingsByUserIdAndStateAll() {
        BookingDto bookingDto;
        BookingDtoIn bookingDtoIn = makeBookingDto(itemDto.getId(), LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(5));
        bookingDto = bookingService.createBooking(bookingDtoIn, userDto2.getId());
        bookingDto.setStatus(Status.WAITING);

        BookingDto bookingDto2;
        BookingDtoIn bookingDtoIn2 = makeBookingDto(itemDto.getId(), LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(5));
        bookingDto2 = bookingService.createBooking(bookingDtoIn, userDto2.getId());
        bookingDto2.setStatus(Status.REJECTED);

        List<BookingDto> list = bookingService.getAllBookingsByUserIdAndState(userDto2.getId(), State.ALL.name(), 0, 2);

        assertThat(list.size(), equalTo(2));
        assertThat(list.get(0).getId(), equalTo(bookingDto.getId()));
        assertThat(list.get(0).getStart(), equalTo(bookingDto.getStart()));
        assertThat(list.get(0).getEnd(), equalTo(bookingDto.getEnd()));

        assertThat(list.get(1).getId(), equalTo(bookingDto2.getId()));
        assertThat(list.get(1).getStart(), equalTo(bookingDto2.getStart()));
        assertThat(list.get(1).getEnd(), equalTo(bookingDto2.getEnd()));
    }

    @Test
    @DisplayName("Should return unsupported exception for ")
    void shouldReturnUnsupportedExceptionForStateAll() {
        BookingDto bookingDto;
        BookingDtoIn bookingDtoIn = makeBookingDto(itemDto.getId(), LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(5));
        bookingDto = bookingService.createBooking(bookingDtoIn, userDto2.getId());
        bookingDto.setStatus(Status.WAITING);

        BookingDto bookingDto2;
        BookingDtoIn bookingDtoIn2 = makeBookingDto(itemDto.getId(), LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(5));
        bookingDto2 = bookingService.createBooking(bookingDtoIn, userDto2.getId());
        bookingDto2.setStatus(Status.REJECTED);

        assertThrows(UnsupportedStatusExist.class, () -> bookingService.getAllBookingsByUserIdAndState(userDto2.getId(), "UNSUPPORTED STATUS", 0, 2));

    }

    @Test
    @DisplayName("Should get all bookings by user id and state CURRENT")
    void shouldGetAllBookingsByUserIdAndStateCurrent() {
        BookingDto bookingDto;
        BookingDtoIn bookingDtoIn = makeBookingDto(itemDto.getId(), LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        bookingDto = bookingService.createBooking(bookingDtoIn, userDto2.getId());
        bookingDto.setStatus(Status.WAITING);

        BookingDto bookingDto2;
        BookingDtoIn bookingDtoIn2 = makeBookingDto(itemDto.getId(), LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        bookingDto2 = bookingService.createBooking(bookingDtoIn, userDto2.getId());
        bookingDto2.setStatus(Status.REJECTED);

        List<BookingDto> list = bookingService.getAllBookingsByUserIdAndState(userDto2.getId(), State.CURRENT.name(), 0, 1);

        assertThat(list.size(), equalTo(1));
        assertThat(list.get(0).getId(), equalTo(bookingDto.getId()));
        assertThat(list.get(0).getStart(), equalTo(bookingDto.getStart()));
        assertThat(list.get(0).getEnd(), equalTo(bookingDto.getEnd()));
    }

    @Test
    @DisplayName("Should get all bookings for current user state ALL")
    void shouldGetAllBookingsForCurrentUserStateAll() {
        BookingDto bookingDto;
        BookingDtoIn bookingDtoIn = makeBookingDto(itemDto.getId(), LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        bookingDto = bookingService.createBooking(bookingDtoIn, userDto2.getId());
        bookingDto.setStatus(Status.WAITING);

        BookingDto bookingDto2;
        BookingDtoIn bookingDtoIn2 = makeBookingDto(itemDto.getId(), LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        bookingDto2 = bookingService.createBooking(bookingDtoIn, userDto2.getId());
        bookingDto2.setStatus(Status.WAITING);

        List<BookingDto> list = bookingService.getAllBookingsCurrentUser(userDto1.getId(), State.ALL.name(), 0, 10);
        assertThat(list.size(), equalTo(2));
    }

    @Test
    @DisplayName("Should get all bookings for current user state canceled")
    void shouldGetAllBookingsForCurrentUserStateCanceled() {
        BookingDto bookingDto;
        BookingDtoIn bookingDtoIn = makeBookingDto(itemDto.getId(), LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        bookingDto = bookingService.createBooking(bookingDtoIn, userDto2.getId());
        bookingDto.setStatus(Status.CANCELED);

        BookingDto bookingDto2;
        BookingDtoIn bookingDtoIn2 = makeBookingDto(itemDto.getId(), LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        bookingDto2 = bookingService.createBooking(bookingDtoIn, userDto2.getId());
        bookingDto2.setStatus(Status.WAITING);

        List<BookingDto> list = bookingService.getAllBookingsCurrentUser(userDto2.getId(), State.REJECTED.name(), 0, 10);
        assertThat(list.size(), equalTo(0));
    }

    @Test
    void shouldMappingToBookingDto() {
        BookingDtoForItem bookingDtoForItem = BookingDtoMapper.toBookingDtoForItem(booking);
        assertThat(bookingDtoForItem.getId(), notNullValue());
        assertThat(bookingDtoForItem.getStart(), equalTo(booking.getStart()));
        assertThat(bookingDtoForItem.getEnd(), equalTo(booking.getEnd()));
        assertThat(bookingDtoForItem.getBookerId(), equalTo(booking.getBooker().getId()));
    }

    @Test
    @DisplayName("Should get all bookings for current user not exist")
    void shouldGetAllBookingsForCurrentUserStateUserNotExist() {
        BookingDto bookingDto;
        BookingDtoIn bookingDtoIn = makeBookingDto(itemDto.getId(), LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        bookingDto = bookingService.createBooking(bookingDtoIn, userDto2.getId());
        bookingDto.setStatus(Status.CANCELED);

        BookingDto bookingDto2;
        BookingDtoIn bookingDtoIn2 = makeBookingDto(itemDto.getId(), LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        bookingDto2 = bookingService.createBooking(bookingDtoIn, userDto2.getId());
        bookingDto2.setStatus(Status.WAITING);

        assertThrows(UserNotExistObject.class, () -> bookingService.getAllBookingsCurrentUser(9999, State.REJECTED.name(), 0, 10));
    }

    @Test
    @DisplayName("Should get all bookings by user id and state All")
    void shouldGetAllBookingsCurrentUser() {
        BookingDto bookingDto;
        BookingDtoIn bookingDtoIn = makeBookingDto(itemDto.getId(), LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        bookingDto = bookingService.createBooking(bookingDtoIn, userDto2.getId());
        bookingDto.setStatus(Status.WAITING);

        BookingDto bookingDto2;
        BookingDtoIn bookingDtoIn2 = makeBookingDto(itemDto.getId(), LocalDateTime.now(), LocalDateTime.now().plusDays(2));
        bookingDto2 = bookingService.createBooking(bookingDtoIn, userDto2.getId());
        bookingDto2.setStatus(Status.REJECTED);

        List<BookingDto> list = bookingService.getAllBookingsByUserIdAndState(userDto2.getId(), State.ALL.name(), 0, 1);

        assertThat(list.size(), equalTo(1));
        assertThat(list.get(0).getId(), equalTo(bookingDto.getId()));
        assertThat(list.get(0).getStart(), equalTo(bookingDto.getStart()));
        assertThat(list.get(0).getEnd(), equalTo(bookingDto.getEnd()));

    }

    private BookingDtoIn makeBookingDto(Long id, LocalDateTime start, LocalDateTime end) {
        return BookingDtoIn.builder()
                .itemId(id)
                .start(start)
                .end(end)
                .build();
    }
}
