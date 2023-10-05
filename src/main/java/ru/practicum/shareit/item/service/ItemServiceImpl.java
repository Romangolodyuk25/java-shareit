package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
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

    public Sort sort = Sort.by(Sort.Direction.ASC, "id");
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemDto createItem(ItemDto itemDto, long userId) {
        validationItem(itemDto, userId);
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotExistObject("User not exist"));
        log.info("Item " + itemDto + " создан");

        Item newItem;
        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(itemDto.getRequestId()).orElseThrow();
            newItem = ItemDtoMapper.toItem(itemDto, user, itemRequest);
        } else {
            newItem = ItemDtoMapper.toItem(itemDto, user, null);
        }

        ItemDto finalItemDto = ItemDtoMapper.toItemDto(itemRepository.save(newItem), commentRepository.findAllByItem(newItem));
        log.info("Объект Item " + finalItemDto);
        return finalItemDto;
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long id, long userId) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotExistObject("User not exist"));
        Item item = itemRepository.findById(id).orElseThrow(() -> new ItemNotExistException("Item not exist"));
        Item newItem = checkFromUpdate(itemDto, item);
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
    public List<ItemDto> getAllItemWithPagination(long userId, Integer from, Integer size) {
        Pageable page = validationForPagination(from, size);
        Page<Item> itemPage = itemRepository.findAllByOwnerId(userId, page);

        List<ItemDto> items = itemPage.getContent().stream()
                .map(x -> ItemDtoMapper.toItemDto(x, commentRepository.findAllByItem(x)))
                .collect(Collectors.toList());

        if (bookingRepository.findAll().size() != 0) {
            return updateFromGetAllWithBooking(items);
        }
        return itemPage.getContent().stream()
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
    public List<ItemDto> searchItemsWithPagination(String text, long userId, Integer from, Integer size) {
        userRepository.findById(userId).orElseThrow();
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        Pageable page = validationForPagination(from, size);
        Page<Item> itemPage = itemRepository.searchItemsPageable(text, page, userId);

        return itemPage.getContent().stream()
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

    private Item checkFromUpdate(ItemDto itemDto, Item item) {
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

    public List<ItemDto> updateFromGetAllWithBooking(List<ItemDto> items) {
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

    private Pageable validationForPagination(Integer from, Integer size) {
        Pageable page;
        if (from == null || size == null) {
            page = PageRequest.of(0, 10, sort);
            return page;
        }
        if (from == 0 && size == 0) {
            throw new ValidationException("Ошибка параметров пагинации параметр size = 0");
        }
        if (from < 0 || size < 0) {
            throw new ValidationException("Ошибка параметров пагинации");
        }
        page = PageRequest.of(from / size, size, sort);
        return page;
    }
}

