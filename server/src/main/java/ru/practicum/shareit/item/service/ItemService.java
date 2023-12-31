package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto createItem(ItemDto itemDto, long userId);

    ItemDto updateItem(ItemDto itemDto, long id, long userId);

    List<ItemDto> getAllItem(long userId);

    List<ItemDto> getAllItemWithPagination(long userId, Integer from, Integer size);

    ItemDto getItemById(long id, long userId);

    List<ItemDto> searchItemsWithPagination(String text, long userId, Integer from, Integer size);
}
