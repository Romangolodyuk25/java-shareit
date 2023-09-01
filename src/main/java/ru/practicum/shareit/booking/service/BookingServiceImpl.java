package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ItemNotExistException;
import ru.practicum.shareit.exception.UserNotExistObject;
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

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto createBooking(BookingDto bookingDto, long userId) {
        validated(bookingDto, userId);
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotExistObject("user not exist"));
        Item item = itemRepository.findById(bookingDto.getId()).orElseThrow(() -> new ItemNotExistException("item not exist"));

        Booking booking = BookingDtoMapper.toBooking(bookingDto, user, item);
        booking.setStatus(Status.WAITING);

        return BookingDtoMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto updateBooking(BookingDto bookingDto, long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotExistObject("user not exist"));
        Item item = itemRepository.findById(bookingDto.getId()).orElseThrow(() -> new ItemNotExistException("item not exist"));
        Booking booking = BookingDtoMapper.toBooking(bookingDto, user, item);

        return BookingDtoMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public List<BookingDto> getAllBookings(long userId) {
        return bookingRepository.findAll().stream()
                .filter(u -> u.getId() == userId)
                .map(BookingDtoMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookingDto getBookingById(long bookingId, long userId) {
        //просто написать запрос получения по 2 айди
        userRepository.findById(userId).orElseThrow(() -> new UserNotExistObject("user not exist"));
        return BookingDtoMapper.toBookingDto(bookingRepository.findByBookingIdAndUserUserId(bookingId, userId));
    }

    @Override
    public BookingDto changeRequestBooking(BookingDto bookingDto, Boolean approved, long userId) {
        BookingDto receivedBooking = getBookingById(bookingDto.getId(), userId);
        if (approved) {
            receivedBooking.setStatus(Status.APPROVED);
        } else {
            receivedBooking.setStatus(Status.REJECTED);
        }
        return updateBooking(bookingDto, userId);
    }

    private void validated(BookingDto bookingDto, long userId) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotExistObject("user not exist"));
        itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> new ItemNotExistException("item not exist"));

        if (bookingDto.getStart().isBefore(LocalDateTime.now()) || bookingDto.getStart().isEqual(bookingDto.getEnd()) ||
                bookingDto.getEnd().isBefore(LocalDateTime.now()) || bookingDto.getEnd().isBefore(bookingDto.getStart()) ||
                bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new ValidationException("Ошибка валидации");
        }
    }
}
