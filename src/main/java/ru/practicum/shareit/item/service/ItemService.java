package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Component
public interface ItemService {

    ItemDto createItem(ItemDto itemDto, long userId);

    ItemDto updateItem(ItemDto itemDto, long id, long userId);

    List<ItemDto> getAllItem(long userId);

    ItemDto getItemById(long id, long userId);

    List<ItemDto> searchItems(String text, long userId);
}
