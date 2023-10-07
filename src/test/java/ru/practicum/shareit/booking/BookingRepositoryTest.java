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
import java.util.List;

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
        user1 = userRepository.save(User.builder()
                .name("Ваня")
                .email("иванов@mail.ru")
                .build());
        item1 = itemRepository.save(Item.builder()
                .name("Вещь1")
                .description("Умеет что-то делать")
                .available(true)
                .owner(user1)
                .build());
        booking1 = bookingRepository.save(Booking.builder()
                        .start(LocalDateTime.now().plusHours(1))
                        .end(LocalDateTime.now().plusDays(1))
                        .item(item1)
                        .booker(user1)
                        .status(Status.WAITING)
                        .build());

        user2 = userRepository.save(User.builder()
                .name("Александр")
                .email("смирнов@mail.ru")
                .build());

        item2 = itemRepository.save(Item.builder()
                .name("Вещь2")
                .description("Ничего не умеет делать")
                .available(true)
                .owner(user2)
                .build());

        booking2 = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .item(item2)
                .booker(user2)
                .status(Status.APPROVED)
                .build());

        booking3 = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .item(item2)
                .booker(user2)
                .status(Status.REJECTED)
                .build());

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
        Page<Booking> bookings = bookingRepository.findAllBookingsForStateCurrent(booking1.getBooker().getId(), LocalDateTime.now().plusHours(5), page);

        assertThat(bookings.getContent().size(), equalTo(1));
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
        Page<Booking> bookings = bookingRepository.findByBooker_IdAndStatus(booking2.getBooker().getId(), Status.REJECTED, page);

        assertThat(bookings.getContent().size(), equalTo(1));
        assertThat(bookings.getContent().get(0).getId(), equalTo(booking3.getId()));
        assertThat(bookings.getContent().get(0).getEnd(), equalTo(booking3.getEnd()));
        assertThat(bookings.getContent().get(0).getBooker().getId(), equalTo(booking3.getBooker().getId()));
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
        Page<Booking> bookings = bookingRepository.findAllBookingsForStateCurrent(item2.getOwner().getId(), LocalDateTime.now().plusHours(5), page);
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

    @Test
    @DisplayName("should return booking by owner id and start is after")
    void shouldReturnBookingByOwnerIdAndStartIsAfter() {
        Pageable page = PageRequest.of(0,10, SORT);
        Page<Booking> bookings = bookingRepository.findByItem_Owner_IdAndStartIsAfter(item1.getOwner().getId(), LocalDateTime.now().minusDays(1), page);
        assertThat(bookings.getContent().size(), equalTo(1));
    }

    @Test
    @DisplayName("should return booking by owner id and status")
    void shouldReturnBookingByOwnerIdAndStatus() {
        Pageable page = PageRequest.of(0,10, SORT);
        Page<Booking> bookings = bookingRepository.findByItem_Owner_IdAndStatus(item2.getOwner().getId(), Status.APPROVED, page);
        assertThat(bookings.getContent().size(), equalTo(1));
        assertThat(bookings.getContent().get(0).getStatus(), equalTo(booking2.getStatus()));
    }

    @Test
    @DisplayName("should return booking by Item id for last booking")
    void shouldReturnLastBooking() {
        List<Booking> bookings = bookingRepository.findAllBookingByItemIdForLastBooking(item2.getId(), LocalDateTime.now().plusHours(6));
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(booking2.getId()));
    }

    @Test
    @DisplayName("should return booking by Item id for next booking")
    void shouldReturnNextBooking() {
        List<Booking> bookings = bookingRepository.findAllBookingByItemIdForNextBooking(item2.getId(), LocalDateTime.now().minusDays(1));
        assertThat(bookings.size(), equalTo(2));
        assertThat(bookings.get(0).getId(), equalTo(booking2.getId()));
        assertThat(bookings.get(1).getId(), equalTo(booking3.getId()));
    }

    @Test
    @DisplayName("find all bookings by user id")
    void shouldReturnAllBookingsByUserId() {
        List<Booking> bookings = bookingRepository.findAllBookingByUserId(booking1.getBooker().getId());

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(booking1.getId()));

    }

}
