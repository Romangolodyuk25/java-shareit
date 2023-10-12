package ru.practicum.shareit.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.client.BaseClient;


import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";
    public static final LocalDateTime TIME = LocalDateTime.now();


    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> bookItem(long userId, BookItemRequestDto requestDto) {
        validated(requestDto);
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> getBookingById(long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> updateBooking(long bookingId, Boolean approved, long userId) {
        return patch("/" + bookingId + "?approved=" + approved, userId);
    }

    public ResponseEntity<Object> getBookings(long userId, BookingState state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getAllBookingsForCurrentUser(long userId, BookingState state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get("/owner?state={state}&from={from}&size={size}", userId, parameters);
    }

    private void validated(BookItemRequestDto bookingDtoIn) {
        if (bookingDtoIn.getStart() == null || bookingDtoIn.getEnd() == null || bookingDtoIn.getStart().isBefore(TIME) ||
                bookingDtoIn.getStart().isEqual(bookingDtoIn.getEnd()) || bookingDtoIn.getEnd().isBefore(TIME) ||
                bookingDtoIn.getEnd().isBefore(bookingDtoIn.getStart())) {
            throw new ValidationException("Ошибка валидации");
        }
    }

}
