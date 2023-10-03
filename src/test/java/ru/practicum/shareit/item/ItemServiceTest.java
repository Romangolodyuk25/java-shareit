package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ItemNotExistException;
import ru.practicum.shareit.exception.UserNotExistObject;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;


import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.item.dto.ItemDtoMapper.toItemDtoFroRequest;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;
    private ItemRequestRepository itemRequestRepository;

    private ItemService itemService;

    User user;

    Item item;
    ItemDto itemDto;

    Comment comment;
    Booking booking;

    @BeforeEach
    void beforeEach() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        bookingRepository = mock(BookingRepository.class);
        itemRequestRepository = mock(ItemRequestRepository.class);
        commentRepository = mock(CommentRepository.class);
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository, itemRequestRepository);

        user = User.builder()
                .id(1L)
                .name("test")
                .email("test@mail.ru")
                .build();
        item = Item.builder()
                .id(1L)
                .name("Вещь 1")
                .description("Умеет что-то")
                .available(true)
                .owner(user)
                .build();

        itemDto = ItemDto.builder()
                .id(1L)
                .name("Вещь 1")
                .description("Умеет что-то")
                .available(true)
                .build();

        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .item(item)
                .booker(user)
                .build();

        comment = Comment.builder()
                .id(1L)
                .text("Comment number one")
                .author(user)
                .item(item)
                .created(LocalDateTime.now().plusHours(1))
                .build();
    }

    @Test
    @DisplayName("should create item")
    void shouldCreateItem() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepository.save(any()))
                .thenReturn(item);

        ItemDto receivedItem = itemService.createItem(itemDto, 1);

        assertThat(receivedItem.getId(), equalTo(item.getId()));
        assertThat(receivedItem.getName(), equalTo(item.getName()));
        assertThat(receivedItem.getDescription(), equalTo(item.getDescription()));
        assertThat(receivedItem.getAvailable(), equalTo(item.getAvailable()));

        verify(itemRepository, times(1))
                .save(any());
    }

    @Test
    void shouldMappingItemDtoForItemRequest() {
       ItemDtoForItemRequest item1 = toItemDtoFroRequest(itemDto);
       assertThat(item1.getId(), equalTo(itemDto.getId()));
       assertThat(item1.getAvailable(), equalTo(itemDto.getAvailable()));
       assertThat(item1.getRequestId(), equalTo(itemDto.getRequestId()));
       assertThat(item1.getName(), equalTo(itemDto.getName()));
    }

    @Test
    @DisplayName("should not create item but user not exist")
    void shouldReturnExceptionForUserNotExist() {
        when(userRepository.findById(anyLong()))
                .thenThrow(new UserNotExistObject("user not exist"));

        assertThrows(UserNotExistObject.class, () -> itemService.createItem(itemDto, 99999));

    }

    @Test
    @DisplayName("should throw exception from create item is empty name or description")
    void shouldThrowExceptionEmptyNameOrDescription() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        when(itemRepository.save(any()))
                .thenThrow(new ValidationException());

        assertThrows(ValidationException.class, () -> itemService.createItem(itemDto, user.getId()));
    }

    @Test
    @DisplayName("should find all item")
    void shouldFindAllItem() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        List<Item> items = new ArrayList<>();
        items.add(item);

        when(itemRepository.findAll())
                .thenReturn(items);

        when(itemRepository.save(any()))
                .thenReturn(item);
        itemService.createItem(itemDto, 1);

        List<ItemDto> receivedItemDto = itemService.getAllItem(1);

        assertThat(receivedItemDto.size(), equalTo(1));
        assertThat(receivedItemDto.get(0).getId(), equalTo(itemDto.getId()));
        assertThat(receivedItemDto.get(0).getName(), equalTo(itemDto.getName()));
        assertThat(receivedItemDto.get(0).getDescription(), equalTo(itemDto.getDescription()));
        assertThat(receivedItemDto.get(0).getAvailable(), equalTo(itemDto.getAvailable()));

        verify(itemRepository, Mockito.times(1))
                .findAll();
        verify(itemRepository, Mockito.times(1))
                .save(item);
    }

    @Test
    @DisplayName("should find all item with pagination")
    void shouldFindAllItemWithPagination() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        PageImpl<Item> itemPage = new PageImpl<>(Collections.singletonList(item));
        Pageable page = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));

        when(itemRepository.findAllByOwnerId(1L, page))
                .thenReturn(itemPage);

        when(itemRepository.save(any()))
                .thenReturn(item);
        itemService.createItem(itemDto, 1);

        List<ItemDto> receivedItemDto = itemService.getAllItemWithPagination(1, page.getPageNumber(), page.getPageSize());

        assertThat(receivedItemDto.size(), equalTo(1));
        assertThat(receivedItemDto.get(0).getId(), equalTo(itemDto.getId()));
        assertThat(receivedItemDto.get(0).getName(), equalTo(itemDto.getName()));
        assertThat(receivedItemDto.get(0).getDescription(), equalTo(itemDto.getDescription()));
        assertThat(receivedItemDto.get(0).getAvailable(), equalTo(itemDto.getAvailable()));

        verify(itemRepository, Mockito.times(1))
                .findAllByOwnerId(1L, page);
        verify(itemRepository, Mockito.times(1))
                .save(item);

    }

    @Test
    @DisplayName("should search item with pagination")
    void shouldSearchItemsWithPagination() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        PageImpl<Item> itemPage = new PageImpl<>(Collections.singletonList(item));
        Pageable page = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));

        when(itemRepository.searchItemsPageable(anyString(), any(), anyLong()))
                .thenReturn(itemPage);

        when(itemRepository.save(any()))
                .thenReturn(item);
        itemService.createItem(itemDto, 1);

        List<ItemDto> receivedItemDto = itemService.searchItemsWithPagination("ВеЩь 1", page.getPageNumber(), page.getPageSize(), 1);

        assertThat(receivedItemDto.size(), equalTo(1));
        assertThat(receivedItemDto.get(0).getId(), equalTo(itemDto.getId()));
        assertThat(receivedItemDto.get(0).getName(), equalTo(itemDto.getName()));
        assertThat(receivedItemDto.get(0).getDescription(), equalTo(itemDto.getDescription()));
    }

    @Test
    @DisplayName("should find item by Id")
    void shouldReturnItemById() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepository.save(any()))
                .thenReturn(item);
        itemService.createItem(itemDto, 1L);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        ItemDto receivedItemDto = itemService.getItemById(1, 1);

        assertThat(receivedItemDto.getId(), equalTo(itemDto.getId()));
        assertThat(receivedItemDto.getName(), equalTo(itemDto.getName()));
        assertThat(receivedItemDto.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(receivedItemDto.getAvailable(), equalTo(itemDto.getAvailable()));

        verify(itemRepository, times(2))
                .findById(1L);
    }

    @Test
    @DisplayName("should find item by Id not exist item")
    void shouldReturnExceptionFromItemByIdNotExistItem() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepository.save(any()))
                .thenReturn(item);
        itemService.createItem(itemDto, 1L);

        when(itemRepository.findById(anyLong()))
                .thenThrow(new ItemNotExistException("item not exist"));

        assertThrows(ItemNotExistException.class, () -> itemService.getItemById(9999, user.getId()));

    }

    @Test
    @DisplayName("should throw exception at find item by Id")
    void shouldThrowAtReturnItemById() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepository.save(any()))
                .thenReturn(item);
        itemService.createItem(itemDto, 1L);

        when(itemRepository.findById(2L))
                .thenThrow(new ItemNotExistException("Вещи не существует"));

        assertThrows(ItemNotExistException.class, () -> itemService.getItemById(2,1));
    }
}
