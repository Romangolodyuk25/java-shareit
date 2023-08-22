package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.exception.model.UserNotExistObject;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto createItem(ItemDto itemDto, long userId) {
        validationItem(itemDto, userId);
        log.info("Item " + itemDto + " создан");
        Item newItem = ItemDtoMapper.toItem(itemDto, userId);
        return ItemDtoMapper.toItemDto(itemRepository.create(newItem, userId));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long id, long userId) {
        userRepository.getById(userId);
        Item newItem = ItemDtoMapper.toItem(itemDto, userId);
        log.info("Item " + itemDto + " обновлен");
        return ItemDtoMapper.toItemDto(itemRepository.update(newItem, id, userId));
    }

    @Override
    public List<ItemDto> getAllItem(long userId) {
        return itemRepository.getAll(userId).stream()
                .map(ItemDtoMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(long id, long userId) {
        return ItemDtoMapper.toItemDto(itemRepository.getById(id, userId));
    }

    @Override
    public List<ItemDto> searchItems(String text, long userId) {
        userRepository.getById(userId);
        return itemRepository.searchItems(text, userId).stream()
                .map(ItemDtoMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void validationItem(ItemDto itemDto, Long userId) {
        if (userId == null || itemDto.getAvailable() == null || itemDto.getName() == null ||
                itemDto.getDescription() == null || itemDto.getName().isEmpty() || itemDto.getDescription().isEmpty()) {
            throw new ValidationException("Ошибка валидации");
        }
        if (userRepository.getById(userId) == null) {
            throw new UserNotExistObject("Юзера с айди " + userId + " не существует");
        }
    }
}
