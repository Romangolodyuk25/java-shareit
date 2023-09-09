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
import ru.practicum.shareit.item.comment.repository.CommentRepository;
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
    private final CommentRepository commentRepository;

    @Override
    public ItemDto createItem(ItemDto itemDto, long userId) {
        validationItem(itemDto, userId);
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotExistObject("User not exist"));
        log.info("Item " + itemDto + " создан");
        Item newItem = ItemDtoMapper.toItem(itemDto, user);
        return ItemDtoMapper.toItemDto(itemRepository.save(newItem), commentRepository.findAllByItem(newItem));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long id, long userId) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotExistObject("User not exist"));
        Item item = itemRepository.findById(id).orElseThrow(() -> new ItemNotExistException("Item not exist"));
        Item newItem = checkFromUpdate(itemDto, item, id);
        log.info("Item " + itemDto + " обновлен");
        return ItemDtoMapper.toItemDto(itemRepository.save(newItem), commentRepository.findAllByItem(newItem));
    }

    @Override
    public List<ItemDto> getAllItem(long userId) {
        List<ItemDto> items = itemRepository.findAllByOwnerIdOrderBy(userId).stream()
                .map(x -> ItemDtoMapper.toItemDto(x, commentRepository.findAllByItem(x)))
                .collect(Collectors.toList());
        if (bookingRepository.findAll().size() != 0) {
            return updateFromGetAllWithBooking(items);
        }
        return itemRepository.findAll().stream()
                .filter(i -> i.getOwner().getId() == userId)
                .map(x -> ItemDtoMapper.toItemDto(x, commentRepository.findAllByItem(x)))
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(long id, long userId) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new ItemNotExistException("Item not exist"));
        if (bookingRepository.findAll().size() != 0 && item.getOwner().getId() == userId) {
            List<Booking> lastBookings = bookingRepository.findAllBookingByItemIdForLastBooking(id, LocalDateTime.now());
            BookingDtoForItem lastBooking = lastBookings.size() > 0 ? BookingDtoMapper.toBookingDtoForItem(lastBookings.get(0)) : null;

            List<Booking> nextBookings = bookingRepository.findAllBookingByItemIdForNextBooking(id, LocalDateTime.now());
            BookingDtoForItem nextBooking = nextBookings.size() > 0 ? BookingDtoMapper.toBookingDtoForItem(nextBookings.get(0)) : null;

            if (lastBooking == null) {
                nextBooking = null;
            }

            return ItemDtoMapper.toItemDtoWithBooking(item, lastBooking, nextBooking, commentRepository.findAllByItem(item));
        } else if (bookingRepository.findAll().size() != 0 && item.getOwner().getId() != userId) {

            return ItemDtoMapper.toItemDtoWithBooking(item, null, null, commentRepository.findAllByItem(item));
        }
        return ItemDtoMapper.toItemDto(itemRepository.findById(id).orElseThrow(() -> new ItemNotExistException("Item not exist")), commentRepository.findAllByItem(item));
    }

    @Override
    public List<ItemDto> searchItems(String text, long userId) {
        userRepository.findById(userId).orElseThrow();
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.searchItems(text, userId).stream()
                .map(x -> ItemDtoMapper.toItemDto(x, commentRepository.findAllByItem(x)))
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
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        return item;
    }

    private List<ItemDto> updateFromGetAllWithBooking(List<ItemDto> items) {
        List<ItemDto> finalItems = new ArrayList<>();
        for (ItemDto i : items) {
            List<Booking> lastBookings = bookingRepository.findAllBookingByItemIdForLastBooking(i.getId(), LocalDateTime.now());
            List<Booking> nextBookings = bookingRepository.findAllBookingByItemIdForNextBooking(i.getId(), LocalDateTime.now());
            for (Booking b : lastBookings) {
                if (i.getId().equals(b.getItem().getId()) && i.getLastBooking() == null) {
                    i.setLastBooking(BookingDtoMapper.toBookingDtoForItem(b));
                }
            }
            for (Booking nb : nextBookings) {
                if (i.getId().equals(nb.getItem().getId()) && i.getNextBooking() == null) {
                    i.setNextBooking(BookingDtoMapper.toBookingDtoForItem(nb));
                }
            }
            finalItems.add(i);
        }
        return finalItems;
    }
}

