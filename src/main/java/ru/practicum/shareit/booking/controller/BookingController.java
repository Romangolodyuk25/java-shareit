package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestBody BookingDto booking,
                                    @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return bookingService.createBooking(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto changeRequestBooking(@RequestBody BookingDto bookingDto,
                                           @RequestParam Boolean approved,
                                           @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return bookingService.changeRequestBooking(bookingDto, approved, userId);
    }

    @GetMapping
    public List<BookingDto> getAllBookings(@RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return bookingService.getAllBookings(userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable Long bookingId,
                                     @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getAllBookingsByUserId(@RequestParam(defaultValue = "ALL") String state,
                                                   @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return bookingService.getAllBookingsByUserIdAndState(userId, state);
    }
}
