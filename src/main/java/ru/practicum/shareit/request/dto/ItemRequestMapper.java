package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();

    }

    public static ItemRequest toItemRequestForItemRequestDtoIn(ItemRequestDtoIn itemRequestDtoIn, User requestor) {
        return ItemRequest.builder()
                .description(itemRequestDtoIn.getDescription())
                .created(LocalDateTime.now())
                .requestor(requestor)
                .build();
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User requestor) {
        return ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .created(itemRequestDto.getCreated())
                .requestor(requestor)
                .build();
    }
}
