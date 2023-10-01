package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
public class BookingRepositoryTest {
    public static final Sort SORT = Sort.by(Sort.Direction.DESC, "start");

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BookingRepository bookingRepository;

    User user1;
    User user2;

    Item item1;
    Item item2;

    Booking booking1;
    Booking booking2;
    Booking booking3;

    @BeforeEach
    public void beforeEach() {
        user1 = userRepository.save(new User(1L, "Ваня", "иванов@mail.ru"));
        item1 = itemRepository.save(new Item(1L, "Вещь1", "Умеет что-то делать", true, user1, null));
        booking1 = bookingRepository.save(new Booking(1L,  LocalDateTime.of(2023, 9,27,10,0), LocalDateTime.of(2023, 9,25,10,30), item1, user1, Status.WAITING));

        user2 = userRepository.save(new User(2L, "Александр", "смирнов@mail.ru"));
        item2 = itemRepository.save(new Item(2L, "Вещь2", "Ничего не умеет делать", true, user2, null));
        booking2 = bookingRepository.save(new Booking(2L, LocalDateTime.of(2023, 9,27,10,0), LocalDateTime.of(2023, 9,28,10,30), item2, user2, Status.WAITING));
        booking2.setStatus(Status.APPROVED);

        booking3 = bookingRepository.save(new Booking(2L, LocalDateTime.of(2023, 9,27,10,0), LocalDateTime.of(2023, 9,30,10,30), item2, user2, Status.WAITING));
        booking3.setStatus(Status.REJECTED);

    }

    @Test
    @DisplayName("should return booking by booker id")
    void shouldReturnBookingByBookerId() {
        Pageable page = PageRequest.of(0,10, SORT);
        Page<Booking> bookings = bookingRepository.findByBooker_Id(booking2.getBooker().getId(), page);

        assertThat(bookings.getContent().size(), equalTo(2));
    }

    @Test
    @DisplayName("should return booking by booker id state current")
    void shouldReturnBookingByBookerIdCurrentState() {
        Pageable page = PageRequest.of(0,10, SORT);
        Page<Booking> bookings = bookingRepository.findAllBookingsForStateCurrent(user2.getId(), LocalDateTime.of(2023, 9,27,10,30), page);

        assertThat(bookings.getContent().size(), equalTo(2));
    }

    @Test
    @DisplayName("should return booking by booker id and end is before")
    void shouldReturnBookingByBookerIdAndEndIsBefore() {
        Pageable page = PageRequest.of(0,10, SORT);
        Page<Booking> bookings = bookingRepository.findByBooker_IdAndEndIsBefore(booking1.getBooker().getId(), LocalDateTime.of(2024, 9,27,10,30), page);

        assertThat(bookings.getContent().size(), equalTo(1));
        assertThat(bookings.getContent().get(0).getId(), equalTo(booking1.getId()));
        assertThat(bookings.getContent().get(0).getEnd(), equalTo(booking1.getEnd()));
        assertThat(bookings.getContent().get(0).getBooker().getId(), equalTo(booking1.getBooker().getId()));
    }

    @Test
    @DisplayName("should return booking by booker id and start is after")
    void shouldReturnBookingByBookerIdAndStartIsAfter() {
        Pageable page = PageRequest.of(0,10, SORT);
        Page<Booking> bookings = bookingRepository.findByBooker_IdAndStartIsAfter(booking1.getBooker().getId(), LocalDateTime.of(2022, 9,27,10,30), page);

        assertThat(bookings.getContent().size(), equalTo(1));
        assertThat(bookings.getContent().get(0).getId(), equalTo(booking1.getId()));
        assertThat(bookings.getContent().get(0).getEnd(), equalTo(booking1.getEnd()));
        assertThat(bookings.getContent().get(0).getBooker().getId(), equalTo(booking1.getBooker().getId()));
    }

    @Test
    @DisplayName("should return booking by booker id and status")
    void shouldReturnBookingByBookerIdAndStatus() {
        Pageable page = PageRequest.of(0,10, SORT);
        Page<Booking> bookings = bookingRepository.findByBooker_IdAndStatus(2L, Status.REJECTED, page);

        assertThat(bookings.getContent().size(), equalTo(1));
        assertThat(bookings.getContent().get(0).getId(), equalTo(booking2.getId()));
        assertThat(bookings.getContent().get(0).getEnd(), equalTo(booking2.getEnd()));
        assertThat(bookings.getContent().get(0).getBooker().getId(), equalTo(booking2.getBooker().getId()));
    }

    @Test
    @DisplayName("should return booking by owner id")
    void shouldReturnBookingByOwnerId() {
        Pageable page = PageRequest.of(0,10, SORT);
        Page<Booking> bookings = bookingRepository.findByItem_Owner_Id(item2.getOwner().getId(), page);

        assertThat(bookings.getContent().size(), equalTo(2));

    }

    @Test
    @DisplayName("should return booking by owner id state current")
    void shouldReturnBookingByOwnerIdCurrentState() {
        Pageable page = PageRequest.of(0,10, SORT);
        Page<Booking> bookings = bookingRepository.findAllBookingsForStateCurrent(user2.getId(), LocalDateTime.of(2023, 9,27, 14,30), page);
        assertThat(bookings.getContent().size(), equalTo(2));
    }

    @Test
    @DisplayName("should return booking by owner id and end is before")
    void shouldReturnBookingByOwnerIdAndEndIsBefore() {
        Pageable page = PageRequest.of(0,10, SORT);
        Page<Booking> bookings = bookingRepository.findByItem_Owner_IdAndEndIsBefore(item1.getOwner().getId(), LocalDateTime.of(2024, 9,27,10,30), page);
        assertThat(bookings.getContent().size(), equalTo(1));
        assertThat(bookings.getContent().get(0).getId(), equalTo(booking1.getId()));
        assertThat(bookings.getContent().get(0).getEnd(), equalTo(booking1.getEnd()));
        assertThat(bookings.getContent().get(0).getItem().getOwner().getId(), equalTo(booking1.getItem().getOwner().getId()));
    }

}
