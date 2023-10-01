package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

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
    @DisplayName("Should get all bookings by user id and state ALL")
    void shouldGetAllBookingsByUserIdAndStateAll() {
        BookingDto bookingDto;
        BookingDtoIn bookingDtoIn = makeBookingDto(itemDto.getId(),  LocalDateTime.now().plusHours(1),  LocalDateTime.now().plusDays(5));
        bookingDto = bookingService.createBooking(bookingDtoIn, userDto2.getId());
        bookingDto.setStatus(Status.WAITING);

        BookingDto bookingDto2;
        BookingDtoIn bookingDtoIn2 = makeBookingDto(itemDto.getId(), LocalDateTime.now().plusHours(1),  LocalDateTime.now().plusDays(5));
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
    @DisplayName("Should get all bookings by user id and state All")
    void shouldGetAllBookingsCurrentUser() {
        BookingDto bookingDto;
        BookingDtoIn bookingDtoIn = makeBookingDto(itemDto.getId(), LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        bookingDto = bookingService.createBooking(bookingDtoIn, userDto2.getId());
        bookingDto.setStatus(Status.WAITING);

        BookingDto bookingDto2;
        BookingDtoIn bookingDtoIn2 = makeBookingDto(itemDto.getId(), LocalDateTime.now(),  LocalDateTime.now().plusDays(2));
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
