package ru.practicum.shareit.itemRequest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

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

        ItemRequestDto ItemRequestDto = itemRequestService.createRequest(itemRequestDtoIn, userDto2.getId());

        TypedQuery<ItemRequest> query = em.createQuery("Select i " +
                "from ItemRequest as i " +
                "where i.id = :id ", ItemRequest.class);
        ItemRequest itemRequest = query
                .setParameter("id", ItemRequestDto.getId())
                .getSingleResult();

        assertThat(itemRequest.getId(), notNullValue());
        assertThat(itemRequest.getDescription(), equalTo(ItemRequestDto.getDescription()));
        assertThat(itemRequest.getCreated(), equalTo(ItemRequestDto.getCreated()));
    }
    @Test
    @DisplayName("should create itemRequest for owner")
    void shouldCreateItemRequestForOwner() {
        ItemRequestDtoIn itemRequestDtoIn = new ItemRequestDtoIn();
        itemRequestDtoIn.setDescription("Нужно что-то на чем можно лететь");

        ItemRequestDto ItemRequestDto = itemRequestService.createRequest(itemRequestDtoIn, userDto2.getId());
        List<ItemRequestDto> list = itemRequestService.getAllRequestsForOwner(userDto2.getId());

        assertThat(list.size(), equalTo(1));
        assertThat(list.get(0).getId(), notNullValue());
        assertThat(list.get(0).getCreated(), equalTo(ItemRequestDto.getCreated()));
        assertThat(list.get(0).getDescription(), equalTo(ItemRequestDto.getDescription()));
    }

    @Test
    @DisplayName("should create itemRequest for other User")
    void shouldCreateItemRequestForOtherUser() {
        ItemRequestDtoIn itemRequestDtoIn = new ItemRequestDtoIn();
        itemRequestDtoIn.setDescription("Нужно что-то на чем можно лететь");
        ItemRequestDto ItemRequestDto = itemRequestService.createRequest(itemRequestDtoIn, userDto1.getId());

        ItemRequestDtoIn itemRequestDtoIn2 = new ItemRequestDtoIn();
        itemRequestDtoIn.setDescription("Нужно что-то на чем можно Ездить");
        ItemRequestDto ItemRequestDto2 = itemRequestService.createRequest(itemRequestDtoIn, userDto2.getId());

        UserDto userDto3 = userService.createUser(UserDto.builder()
                .name("test")
                .email("test3@mail.ru")
                .build());
        List<ItemRequestDto> list = itemRequestService.getAllRequestsForOtherUser(userDto3.getId(),0, 2);
        assertThat(list.size(), equalTo(2));
        assertThat(list.get(1).getId(), equalTo(ItemRequestDto.getId()));
        assertThat(list.get(1).getCreated(), equalTo(ItemRequestDto.getCreated()));
        assertThat(list.get(1).getDescription(), equalTo(ItemRequestDto.getDescription()));

        assertThat(list.get(0).getId(), equalTo(ItemRequestDto2.getId()));
        assertThat(list.get(0).getCreated(), equalTo(ItemRequestDto2.getCreated()));
        assertThat(list.get(0).getDescription(), equalTo(ItemRequestDto2.getDescription()));
    }
}
