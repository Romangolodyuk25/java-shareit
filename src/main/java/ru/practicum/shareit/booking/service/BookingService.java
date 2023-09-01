package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@Component
public interface BookingService {
    BookingDto createBooking(BookingDto bookingDto, long userId);

    BookingDto updateBooking(BookingDto bookingDto, long userId);

    List<BookingDto> getAllBookings(long userId);

    BookingDto getBookingById(long bookingId, long userId);

    List<BookingDto> getAllBookingsByUserIdAndState(long userId, String state);

    BookingDto changeRequestBooking(BookingDto bookingDto, Boolean approved, long userId);
}
