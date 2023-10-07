package ru.practicum.shareit.itemRequest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ItemRequestNotExist;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ItemRequestServiceImplTest {

    private final EntityManager em;
    private final UserService userService;
    private final ItemService itemService;
    private final ItemRequestService itemRequestService;

    UserDto userDto1;
    UserDto userDto2;

    ItemDto itemDto;

    @BeforeEach
    public void beforeEach() {
        userDto1 = userService.createUser(UserDto.builder()
                .name("test")
                .email("test@mail.ru")
                .build());
        userDto2 = userService.createUser(UserDto.builder()
                .name("test2")
                .email("test@mail2.ru")
                .build());
        itemDto = itemService.createItem(ItemDto.builder()
                .id(1L)
                .name("Пила")
                .description("Что-то пилит")
                .available(true)
                .build(), userDto1.getId());
    }

    @Test
    @DisplayName("should create itemRequest")
    void shouldCreateItemRequest() {
        ItemRequestDtoIn itemRequestDtoIn = new ItemRequestDtoIn();
        itemRequestDtoIn.setDescription("Нужно что-то что пилит");

        ItemRequestDto itemRequestDto = itemRequestService.createRequest(itemRequestDtoIn, userDto2.getId());

        TypedQuery<ItemRequest> query = em.createQuery("Select i " +
                "from ItemRequest as i " +
                "where i.id = :id ", ItemRequest.class);
        ItemRequest itemRequest = query
                .setParameter("id", itemRequestDto.getId())
                .getSingleResult();

        assertThat(itemRequest.getId(), notNullValue());
        assertThat(itemRequest.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(itemRequest.getCreated(), equalTo(itemRequestDto.getCreated()));
    }

    @Test
    @DisplayName("should create itemRequest for owner")
    void shouldCreateItemRequestForOwner() {
        ItemRequestDtoIn itemRequestDtoIn = new ItemRequestDtoIn();
        itemRequestDtoIn.setDescription("Нужно что-то на чем можно лететь");

        ItemRequestDto itemRequestDto = itemRequestService.createRequest(itemRequestDtoIn, userDto2.getId());
        List<ItemRequestDto> list = itemRequestService.getAllRequestsForOwner(userDto2.getId());

        assertThat(list.size(), equalTo(1));
        assertThat(list.get(0).getId(), notNullValue());
        assertThat(list.get(0).getCreated(), equalTo(itemRequestDto.getCreated()));
        assertThat(list.get(0).getDescription(), equalTo(itemRequestDto.getDescription()));
    }

    @Test
    @DisplayName("should throw exception not exist")
    void shouldThrowExceptionForGetById() {
        ItemRequestDtoIn itemRequestDtoIn = new ItemRequestDtoIn();
        itemRequestDtoIn.setDescription("Нужно что-то что пилит");

        ItemRequestDto itemRequestDto = itemRequestService.createRequest(itemRequestDtoIn, userDto2.getId());

        assertThrows(ItemRequestNotExist.class, () -> itemRequestService.getRequestById(userDto1.getId(), 9999));
    }

    @Test
    @DisplayName("should get item request")
    void shouldGetItemRequestById() {
        ItemRequestDtoIn itemRequestDtoIn = new ItemRequestDtoIn("что-то что пилит");
        ItemRequestDto itemRequestDto = itemRequestService.createRequest(itemRequestDtoIn, userDto1.getId());
        ItemRequest itemRequest = ItemRequestMapper.toItemRequestForItemRequestDtoIn(itemRequestDtoIn, UserDtoMapper.toUser(userDto1));

        ItemDto itemDto = makeItemDto("Пила", "Пилит и пилит", true);
        itemDto.setRequestId(itemRequestDto.getId());
        itemDto = itemService.createItem(itemDto, userDto1.getId());

        ItemRequestDto receivedItemRequest = itemRequestService.getRequestById(userDto2.getId(), itemRequestDto.getId());
        assertThat(receivedItemRequest.getId(), equalTo(itemRequestDto.getId()));

    }

    @Test
    @DisplayName("should create itemRequest for other User")
    void shouldCreateItemRequestForOtherUser() {
        ItemRequestDtoIn itemRequestDtoIn = new ItemRequestDtoIn();
        itemRequestDtoIn.setDescription("Нужно что-то на чем можно лететь");
        ItemRequestDto itemRequestDto = itemRequestService.createRequest(itemRequestDtoIn, userDto1.getId());

        ItemRequestDtoIn itemRequestDtoIn2 = new ItemRequestDtoIn();
        itemRequestDtoIn.setDescription("Нужно что-то на чем можно Ездить");
        ItemRequestDto itemRequestDto2 = itemRequestService.createRequest(itemRequestDtoIn, userDto2.getId());

        UserDto userDto3 = userService.createUser(UserDto.builder()
                .name("test")
                .email("test3@mail.ru")
                .build());
        List<ItemRequestDto> list = itemRequestService.getAllRequestsForOtherUser(userDto3.getId(), 0, 2);
        assertThat(list.size(), equalTo(2));
        assertThat(list.get(1).getId(), equalTo(itemRequestDto.getId()));
        assertThat(list.get(1).getCreated(), equalTo(itemRequestDto.getCreated()));
        assertThat(list.get(1).getDescription(), equalTo(itemRequestDto.getDescription()));

        assertThat(list.get(0).getId(), equalTo(itemRequestDto2.getId()));
        assertThat(list.get(0).getCreated(), equalTo(itemRequestDto2.getCreated()));
        assertThat(list.get(0).getDescription(), equalTo(itemRequestDto2.getDescription()));
    }

    private ItemDto makeItemDto(String name, String description, Boolean available) {
        return ItemDto.builder()
                .name(name)
                .description(description)
                .available(available)
                .build();
    }
}
