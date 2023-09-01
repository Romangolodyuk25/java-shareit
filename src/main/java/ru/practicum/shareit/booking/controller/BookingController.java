package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    BookingDto createBooking(@RequestBody BookingDto booking,
                             @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return bookingService.createBooking(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    BookingDto changeRequestBooking(@RequestBody BookingDto bookingDto,
                                    @RequestParam Boolean approved,
                                    @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return bookingService.changeRequestBooking(bookingDto, approved, userId);
    }
}
