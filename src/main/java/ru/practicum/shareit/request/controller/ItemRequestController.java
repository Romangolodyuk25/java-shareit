package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createRequest(@RequestBody ItemRequestDtoIn itemRequestDtoIn,
                                        @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return itemRequestService.createRequest(itemRequestDtoIn, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getAllRequestsForOwner(@RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return itemRequestService.getAllRequestsForOwner(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequestsForOtherUser(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                                           @RequestParam(required = false, defaultValue = "0") Integer from,
                                                           @RequestParam(required = false) Integer size) {
        return itemRequestService.getAllRequestsForOtherUser(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                         @PathVariable long requestId) {
        return itemRequestService.getRequestById(userId, requestId);
    }
}
