package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingDtoMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                booking.getBooker(),
                booking.getItem(),
                booking.getItem().getName()
        );
    }

    public static Booking toBooking(BookingDto bookingDto, User booker, Item item) {
        return new Booking(bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                booker,
                bookingDto.getStatus()
        );
    }

    public static Booking toBookingFromBookingIn(BookingDtoIn bookingDtoIn, User booker, Item item) {
        return new Booking(bookingDtoIn.getId(),
                bookingDtoIn.getStart(),
                bookingDtoIn.getEnd(),
                item,
                booker,
                bookingDtoIn.getStatus()
                );
    }

    public static BookingDtoForItem toBookingDtoForItem(Booking booking) {
        return new BookingDtoForItem(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getBooker().getId(),
                booking.getStatus()
                );
    }
}
