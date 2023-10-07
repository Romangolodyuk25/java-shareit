package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoIn;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(BookingDtoIn bookingDtoIn, long userId);

    BookingDto updateBooking(long bookerId, Boolean approved, long userId);

    List<BookingDto> getAllBookings(long userId);

    BookingDto getBookingById(long bookingId, long userId);

    List<BookingDto> getAllBookingsByUserIdAndState(long userId, String state, Integer from, Integer size);

    List<BookingDto> getAllBookingsCurrentUser(long userId, String state, Integer from, Integer size);
}
