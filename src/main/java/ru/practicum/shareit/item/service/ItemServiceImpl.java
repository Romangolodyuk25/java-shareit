package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ItemNotExistException;
import ru.practicum.shareit.exception.UserNotExistObject;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

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
        userRepository.findById(userId).orElseThrow(() -> new UserNotExistObject("User not exist"));
        Item item = itemRepository.findById(id).orElseThrow(() -> new ItemNotExistException("Item not exist"));
        Item newItem = checkFromUpdate(itemDto, item, id);
        log.info("Item " + itemDto + " обновлен");
        return ItemDtoMapper.toItemDto(itemRepository.save(newItem));
    }

    @Override
    public List<ItemDto> getAllItem(long userId) {

        return itemRepository.findAll().stream()
                .filter(i -> i.getOwner().getId() == userId)
                .map(ItemDtoMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(long id, long userId) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new ItemNotExistException("Item not exist"));
        if (bookingRepository.findAll().size() != 0 && item.getOwner().getId() == userId) {
            BookingDtoForItem lastBooking = BookingDtoMapper.toBookingDtoForItem(bookingRepository.findAllBookingByItemIdForLastBooking(id, LocalDateTime.now()).get(0));
            BookingDtoForItem nextBooking = BookingDtoMapper.toBookingDtoForItem(bookingRepository.findAllBookingByItemIdForNextBooking(id, LocalDateTime.now()).get(0));
            return ItemDtoMapper.toItemDtoWithBooking(item, lastBooking, nextBooking);
        }
        return ItemDtoMapper.toItemDto(itemRepository.findById(id).orElseThrow(() -> new ItemNotExistException("Item not exist")));
    }

    @Override
    public List<ItemDto> searchItems(String text, long userId) {
        userRepository.findById(userId).orElseThrow();
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
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

    private Item checkFromUpdate(ItemDto itemDto, Item item, long id) {
        if (itemDto.getAvailable()!= null) {
            item.setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getName()!=null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription()!=null) {
            item.setDescription(itemDto.getDescription());
        }
        return item;
    }
}
