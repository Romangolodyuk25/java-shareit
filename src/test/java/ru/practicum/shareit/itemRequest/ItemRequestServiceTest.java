package ru.practicum.shareit.itemRequest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.ItemRequestNotExist;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemRequestServiceTest {

    private ItemRequestRepository itemRequestRepository;
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private CommentRepository commentRepository;

    private ItemRequestService itemRequestService;

    User user;

    Item item;
    ItemDto itemDto;

    ItemRequest itemRequest;
    ItemRequestDto itemRequestDto;
    ItemRequestDtoIn itemRequestDtoIn;


    @BeforeEach
    void beforeEach() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        commentRepository = mock(CommentRepository.class);
        itemRequestRepository = mock(ItemRequestRepository.class);
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemRepository, commentRepository);

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

        itemRequestDtoIn = new ItemRequestDtoIn("Запрос на что-то");

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Запрос на что-то")
                .created(LocalDateTime.now().plusHours(1))
                .requestor(user)
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Запрос на что-то")
                .created(LocalDateTime.now().plusHours(1))
                .items(new ArrayList<>())
                .build();

    }

    @Test
    @DisplayName("should create itemRequest")
    void shouldCreateItemRequest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        when(itemRequestRepository.save(any()))
                .thenReturn(itemRequest);

        ItemRequestDto receivedItemRequest = itemRequestService.createRequest(itemRequestDtoIn, 1);

        assertThat(receivedItemRequest.getId(), equalTo(itemRequest.getId()));
        assertThat(receivedItemRequest.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(receivedItemRequest.getCreated(), equalTo(itemRequest.getCreated()));
    }

    @Test
    @DisplayName("should get itemRequest for owner")
    void shouldGetRequestForOwner() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        when(itemRequestRepository.save(any()))
                .thenReturn(itemRequest);

       itemRequestService.createRequest(itemRequestDtoIn, 1);

        List<ItemRequest> itemRequests = new ArrayList<>();
        itemRequests.add(itemRequest);

        when(itemRequestRepository.findByRequestor_Id(1, Sort.by(Sort.Direction.DESC, "created")))
                .thenReturn(itemRequests);

        List<ItemRequestDto> receivedItemRequest = itemRequestService.getAllRequestsForOwner(1);

        assertThat(receivedItemRequest.get(0).getId(), equalTo(itemRequest.getId()));
        assertThat(receivedItemRequest.get(0).getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(receivedItemRequest.get(0).getCreated(), equalTo(itemRequest.getCreated()));
    }

    @Test
    @DisplayName("should find itemRequest by Id")
    void shouldReturnItemRequestById() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        when(itemRequestRepository.save(any()))
                .thenReturn(itemRequest);

        itemRequestService.createRequest(itemRequestDtoIn, 1);

        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(itemRequest));

        ItemRequestDto receivedItemRequest = itemRequestService.getRequestById(1, 1);
        assertThat(receivedItemRequest.getId(), equalTo(itemRequest.getId()));
        assertThat(receivedItemRequest.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(receivedItemRequest.getCreated(), equalTo(itemRequest.getCreated()));
    }

    @Test
    @DisplayName("should throw for itemRequest by Id")
    void shouldThowItemRequestById() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        when(itemRequestRepository.save(any()))
                .thenReturn(itemRequest);

        itemRequestService.createRequest(itemRequestDtoIn, 1);

        when(itemRequestRepository.findById(3L))
                .thenThrow(new ItemRequestNotExist("Запрос не существует"));

        Assertions.assertThrows(ItemRequestNotExist.class, () -> itemRequestService.getRequestById(3,1));

    }
}

