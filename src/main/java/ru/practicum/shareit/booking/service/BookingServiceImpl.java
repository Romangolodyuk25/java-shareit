package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class BookingServiceImpl implements BookingService {

    public static final LocalDateTime TIME = LocalDateTime.now();
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto createBooking(BookingDtoIn bookingDtoIn, long userId) {
        Item item = itemRepository.findById(bookingDtoIn.getItemId()).orElseThrow(() -> new ItemNotExistException("item not exist"));
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotExistObject("user not exist"));
        validated(bookingDtoIn, item, userId);
        if (!item.getAvailable()) {
            throw new IsNotAvailableException("Вещь нельзя забронировать");
        }
        Booking booking = BookingDtoMapper.toBookingFromBookingIn(bookingDtoIn, user, item);
        booking.setStatus(Status.WAITING);

        return BookingDtoMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto updateBooking(long bookerId, Boolean approved, long userId) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotExistObject("user not exist"));
        Booking receivedBooking = bookingRepository.findById(bookerId).orElseThrow();
        if (receivedBooking.getItem().getOwner().getId() != userId) {
            throw new IsNotOwnerException("Данный юзер не может изменить статус вещи так как не является ее владельцем");
        }
        if (receivedBooking.getStatus().equals(Status.APPROVED) && approved) {
            throw new StatusAlreadyApprovedException("Статус уже подтвержден");
        }
        checkFromUpdateBooking(approved, receivedBooking);
        return BookingDtoMapper.toBookingDto(bookingRepository.save(receivedBooking));
    }

    @Override
    public List<BookingDto> getAllBookings(long userId) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotExistObject("user not exist"));
        return bookingRepository.findAll().stream()
                .filter(booking -> booking.getBooker().getId() == userId)
                .map(BookingDtoMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookingDto getBookingById(long bookingId, long userId) {
        Booking receivedBooking = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotExistException("booking not exist"));
        userRepository.findById(userId).orElseThrow(() -> new UserNotExistObject("user not exist"));
        if (receivedBooking.getBooker().getId() == userId || receivedBooking.getItem().getOwner().getId() == userId) {
            return BookingDtoMapper.toBookingDto(receivedBooking);//либо в первом условии сравнить с bookerId
        }
        throw new IsNotOwnerException("У данного юзера нет вещей");
    }

    @Override
    public List<BookingDto> getAllBookingsByUserIdAndState(long userId, String state, Integer from, Integer size) {
        return getBookingsByState(userId, state, from, size);
    }

    @Override
    public List<BookingDto> getAllBookingsCurrentUser(long userId, String state, Integer from, Integer size) {
        return getBookingsByStateForOwner(userId, state, from, size);
    }

    private void validated(BookingDtoIn bookingDtoIn, Item item, long userId) {
        if (bookingDtoIn.getStart() == null || bookingDtoIn.getEnd() == null || bookingDtoIn.getStart().isBefore(TIME) ||
                bookingDtoIn.getStart().isEqual(bookingDtoIn.getEnd()) || bookingDtoIn.getEnd().isBefore(TIME) ||
                bookingDtoIn.getEnd().isBefore(bookingDtoIn.getStart())) {
            log.info("Время старта " + bookingDtoIn.getStart());
            log.info("Время конца " + bookingDtoIn.getEnd());
            throw new ValidationException("Ошибка валидации");
        }
        if (item.getOwner().getId() == userId) {
            throw new UserIsOwnerException("Юзер является владельцем");
        }
    }

    private void checkFromUpdateBooking(Boolean approved, Booking booking) {
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
    }

    private List<BookingDto> getBookingsByState(long userId, String state, Integer from, Integer size) {
        Pageable page = validationForPagination(from, size);

        State receivedState = State.from(state)
                .orElseThrow(() -> new UnsupportedStatusExist("Unknown state: " + state));

        userRepository.findById(userId).orElseThrow(() -> new UserNotExistObject("user not exist"));

        if (receivedState.equals(State.WAITING)) {
            return bookingRepository.findByBooker_IdAndStatus(userId, Status.WAITING, page).stream()
                    .map(BookingDtoMapper::toBookingDto)
                    .collect(Collectors.toList());
        }
        if (receivedState.equals(State.CURRENT)) {
            return bookingRepository.findAllBookingsForStateCurrent(userId, LocalDateTime.now(), page).stream()
                    .map(BookingDtoMapper::toBookingDto)
                    .collect(Collectors.toList());
        }
        if (receivedState.equals(State.PAST)) {
            return bookingRepository.findByBooker_IdAndEndIsBefore(userId, LocalDateTime.now(), page).stream()
                    .map(BookingDtoMapper::toBookingDto)
                    .collect(Collectors.toList());
        }
        if (receivedState.equals(State.REJECTED)) {
            return bookingRepository.findByBooker_IdAndStatus(userId, Status.REJECTED, page).stream()
                    .map(BookingDtoMapper::toBookingDto)
                    .collect(Collectors.toList());
        }
        if (receivedState.equals(State.FUTURE)) {
            return bookingRepository.findByBooker_IdAndStartIsAfter(userId, LocalDateTime.now(), page).stream()
                    .map(BookingDtoMapper::toBookingDto)
                    .collect(Collectors.toList());
        }

        return bookingRepository.findByBooker_Id(userId, page).stream()
                .map(BookingDtoMapper::toBookingDto)
                .collect(Collectors.toList());

}

    private List<BookingDto> getBookingsByStateForOwner(long userId, String state, Integer from, Integer size) {
        Pageable page = validationForPagination(from, size);

        State receivedState = State.from(state)
                .orElseThrow(() -> new UnsupportedStatusExist("Unknown state: " + state));

        userRepository.findById(userId).orElseThrow(() -> new UserNotExistObject("user not exist"));

        if (receivedState.equals(State.WAITING)) {
            return bookingRepository.findByItem_Owner_IdAndStatus(userId, Status.WAITING, page).stream()
                    .map(BookingDtoMapper::toBookingDto)
                    .collect(Collectors.toList());
        }
        if (receivedState.equals(State.CURRENT)) {
            return bookingRepository.findAllBookingsForStateCurrentForOwner(userId, LocalDateTime.now(), page).stream()
                    .map(BookingDtoMapper::toBookingDto)
                    .collect(Collectors.toList());
        }
        if (receivedState.equals(State.PAST)) {
            return bookingRepository.findByItem_Owner_IdAndEndIsBefore(userId, LocalDateTime.now(), page).stream()
                    .map(BookingDtoMapper::toBookingDto)
                    .collect(Collectors.toList());
        }
        if (receivedState.equals(State.REJECTED)) {
            return bookingRepository.findByItem_Owner_IdAndStatus(userId, Status.REJECTED, page).stream()
                    .map(BookingDtoMapper::toBookingDto)
                    .collect(Collectors.toList());
        }
        if (receivedState.equals(State.FUTURE)) {
            return bookingRepository.findByItem_Owner_IdAndStartIsAfter(userId, LocalDateTime.now(), page).stream()
                    .map(BookingDtoMapper::toBookingDto)
                    .collect(Collectors.toList());
        }
        return bookingRepository.findByItem_Owner_Id(userId, page).stream()
                .map(BookingDtoMapper::toBookingDto)
                .collect(Collectors.toList());
    }
    private Pageable validationForPagination(Integer from, Integer size) {
        Sort sortDesc = Sort.by(Sort.Direction.DESC, "start");
        Pageable page;
        if (from == null || size == null) {
            page = PageRequest.of(0, 10, sortDesc);
            return page;
        }
        if (from == 0 && size == 0) {
            throw new ValidationException("Ошибка параметров пагинации параметр size = 0");
        }
        if (from < 0 || size < 0) {
            throw new ValidationException("Ошибка параметров пагинации");
        }
        page = PageRequest.of(from / size, size, sortDesc);
        return page;
    }
}
