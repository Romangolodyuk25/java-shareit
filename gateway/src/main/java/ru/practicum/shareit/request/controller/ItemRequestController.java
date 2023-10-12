package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/requests")
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestBody ItemRequestDtoIn itemRequestDtoIn,
                                                @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return itemRequestClient.createRequest(itemRequestDtoIn, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequestsForOwner(@RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return itemRequestClient.getAllRequestsForOwner(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequestsForOtherUser(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                                             @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
                                                             @Positive @RequestParam(required = false, defaultValue = "10") Integer size) {
        return itemRequestClient.getAllRequestsForOtherUser(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                                 @PathVariable long requestId) {
        return itemRequestClient.getRequestById(requestId, userId);
    }
}
