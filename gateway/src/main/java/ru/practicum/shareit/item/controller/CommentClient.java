package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.controller.BookingClient;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.comment.dto.CommentDtoIn;

import javax.validation.ValidationException;

@Service
public class CommentClient extends BaseClient {

    private static final String API_PREFIX = "/items";
    private BookingController bookingController;
    private BookingClient bookingClient;

    @Autowired
    public CommentClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createComment(long itemId, CommentDtoIn commentDtoIn, long userId) {
        validated(commentDtoIn);
        return post("/" + itemId + "/comment", userId, commentDtoIn);
    }

    private void validated(CommentDtoIn commentDtoIn) {
        if (commentDtoIn == null || commentDtoIn.getText().isEmpty()) {
            throw new ValidationException();
        }
    }
}
