package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestBody BookingDtoIn bookingDtoIn,
                                    @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return bookingService.createBooking(bookingDtoIn, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@PathVariable long bookingId,
                                    @RequestParam Boolean approved,
                                    @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return bookingService.updateBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable Long bookingId,
                                     @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getAllBookingsByUserId(@RequestParam(defaultValue = "ALL") String state,
                                                   @RequestHeader(value = "X-Sharer-User-Id") long userId,
                                                   @RequestParam(required = false, defaultValue = "0") Integer from,
                                                   @RequestParam(required = false, defaultValue = "10")  Integer size) {
        return bookingService.getAllBookingsByUserIdAndState(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsForCurrentUser(@RequestParam(defaultValue = "ALL") String state,
                                                         @RequestHeader(value = "X-Sharer-User-Id") long userId,
                                                         @RequestParam(required = false, defaultValue = "0") Integer from,
                                                         @RequestParam(required = false, defaultValue = "10")  Integer size) {
        return bookingService.getAllBookingsCurrentUser(userId, state, from, size);
    }
}
