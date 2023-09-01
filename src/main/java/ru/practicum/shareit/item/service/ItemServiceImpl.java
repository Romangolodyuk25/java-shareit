package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotExistException;
import ru.practicum.shareit.exception.UserNotExistObject;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
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
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotExistObject("User not exist"));
        log.info("Item " + itemDto + " создан");
        Item newItem = ItemDtoMapper.toItem(itemDto, user);
        return ItemDtoMapper.toItemDto(itemRepository.save(newItem));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long id, long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotExistObject("User not exist"));
        Item newItem = ItemDtoMapper.toItem(itemDto, user);
        log.info("Item " + itemDto + " обновлен");
        return ItemDtoMapper.toItemDto(itemRepository.save(newItem));
    }

    @Override
    public List<ItemDto> getAllItem(long userId) {
        return itemRepository.findAll().stream()
                .filter(u -> u.getItemId() == userId)
                .map(ItemDtoMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(long id, long userId) {
        return ItemDtoMapper.toItemDto(itemRepository.findById(id).orElseThrow(() -> new ItemNotExistException("Item not exist")));
    }

    @Override
    public List<ItemDto> searchItems(String text, long userId) {
        userRepository.findById(userId).orElseThrow();
        return itemRepository.searchItems(text, userId).stream()
                .map(ItemDtoMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void validationItem(ItemDto itemDto, Long userId) {
        if (userId == null || itemDto.getAvailable() == null || itemDto.getName() == null ||
                itemDto.getDescription() == null || itemDto.getName().isEmpty() || itemDto.getDescription().isEmpty()) {
            throw new ValidationException("Ошибка валидации");
        }
        userRepository.findById(userId);
    }
}
