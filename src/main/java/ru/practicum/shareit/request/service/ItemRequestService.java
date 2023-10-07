package ru.practicum.shareit.request.service;

import ru.practicum.shareit.exception.ItemRequestNotExist;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createRequest(ItemRequestDtoIn itemRequestDtoIn, long userId);

    List<ItemRequestDto> getAllRequestsForOwner(long userId);

    List<ItemRequestDto> getAllRequestsForOtherUser(long userId, Integer from, Integer size);

    ItemRequestDto getRequestById(long userId, long requestId) throws ItemRequestNotExist;
}
