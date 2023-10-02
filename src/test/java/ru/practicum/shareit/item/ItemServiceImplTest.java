package ru.practicum.shareit.item;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ItemNotExistException;
import ru.practicum.shareit.exception.UserNotExistObject;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.validation.ValidationException;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ItemServiceImplTest {

    private final EntityManager em;

    private final ItemService itemService;
    private final UserService userService;

    UserDto userDto;

    @BeforeEach
    void beforeEach() {
        userDto = userService.createUser(UserDto.builder()
                .name("Vanya")
                .email("vanya@email.ru")
                .build()
        );
    }

    @Test
    @Order(value = 1)
    void saveItem() {
        ItemDto itemDto = makeItemDto("Пила", "Пилит и пилит", true);
        itemDto = itemService.createItem(itemDto, userDto.getId());

        TypedQuery<Item> query = em.createQuery("Select i " +
                "from Item i " +
                "where i.id = :id ", Item.class);
        Item item = query
                .setParameter("id", itemDto.getId())
                .getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(item.getAvailable(), equalTo(true));
    }

    @Test
    @DisplayName("should return all items")
    @Order(value = 2)
    void shouldReturnAllItems() {
        ItemDto itemDto = makeItemDto("Пила", "Пилит и пилит", true);
        itemDto = itemService.createItem(itemDto, userDto.getId());

        TypedQuery<Item> query = em.createQuery("Select i " +
                "from Item i " +
                "where i.id = :id ", Item.class);
        List<Item> items = query
                .setParameter("id", itemDto.getId())
                .getResultList();

        assertThat(items.size(), equalTo(1));
    }

    @Test
    @DisplayName("should update item")
    @Order(value = 3)
    void shouldUpdateItem() {
        ItemDto itemDto = makeItemDto("Пила", "Пилит и пилит", true);
        itemDto = itemService.createItem(itemDto, userDto.getId());

        ItemDto receivedItemDto = new ItemDto();
        receivedItemDto.setName("Молоток");
        receivedItemDto.setDescription("Стучит");
        itemService.updateItem(itemDto, itemDto.getId(), userDto.getId());

        TypedQuery<Item> query = em.createQuery("Select i " +
                "from Item i " +
                "where i.id = :id ", Item.class);
        Item item = query.setParameter("id", itemDto.getId())
                        .getSingleResult();

        assertThat(item.getId(), notNullValue());
    }

    @Test
    @DisplayName("should get item by id")
    @Order(value = 4)
    void shouldGetItemById() {
        ItemDto itemDto = makeItemDto("Пила", "Пилит и пилит", true);
        itemDto = itemService.createItem(itemDto, userDto.getId());

        ItemDto receivedItemDto = itemService.getItemById(itemDto.getId(),userDto.getId());

        assertThat(itemDto.getId(), equalTo(receivedItemDto.getId()));
        assertThat(itemDto.getName(), equalTo(receivedItemDto.getName()));
        assertThat(itemDto.getDescription(), equalTo(receivedItemDto.getDescription()));
        assertThat(itemDto.getAvailable(), equalTo(receivedItemDto.getAvailable()));
    }

    @Test
    @DisplayName("should search item")
    @Order(value = 5)
    void shouldSearchItem() {
        ItemDto itemDto = makeItemDto("Пила", "Пилит и пилит", true);
        itemDto = itemService.createItem(itemDto, userDto.getId());

        List<ItemDto> searchItems = itemService.searchItemsWithPagination("ПиЛиТ", userDto.getId(), 0, 10);
        assertThat(searchItems.size(), equalTo(1));
    }

    @Test
    @Order(value = 6)
    @DisplayName("should throw exception for method save is empty name")
    void shouldReturnExceptionForEmptyName() {
        ItemDto itemDto = new ItemDto();
        itemDto.setDescription("Пилит и пилит");
        itemDto.setAvailable(true);

        assertThrows(ValidationException.class, () -> itemService.createItem(itemDto, userDto.getId()));

    }

    @Test
    @Order(value = 7)
    @DisplayName("should throw exception for method save is empty description")
    void shouldReturnExceptionForEmptyDescription() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Пилa");
        itemDto.setAvailable(true);

        assertThrows(ValidationException.class, () -> itemService.createItem(itemDto, userDto.getId()));

    }

    @Test
    @DisplayName("should search empty list item")
    @Order(value = 8)
    void shouldSearchItemEmptyList() {
        ItemDto itemDto = makeItemDto("Пила", "Пилит и пилит", true);
        itemDto = itemService.createItem(itemDto, userDto.getId());

        List<ItemDto> searchItems = itemService.searchItemsWithPagination("Дрель", userDto.getId(), 0, 10);
        assertThat(searchItems.size(), equalTo(0));
    }

    @Test
    @DisplayName("should return not exist item for get item by id")
    @Order(value = 9)
    void shouldReturnNotExistItemForGetItemById() {
        ItemDto itemDto = makeItemDto("Пила", "Пилит и пилит", true);
        itemDto = itemService.createItem(itemDto, userDto.getId());

        assertThrows(ItemNotExistException.class,() -> itemService.getItemById(9999,userDto.getId()));

    }

    @Test
    @DisplayName("should return not exist user for save item")
    @Order(value = 10)
    void shouldReturnNotExistUserForSaveItem() {
        ItemDto itemDto = makeItemDto("Пила", "Пилит и пилит", true);

        assertThrows(UserNotExistObject.class,() -> itemService.createItem(itemDto ,9999));
    }

    private ItemDto makeItemDto(String name, String description, Boolean available) {
        return ItemDto.builder()
                .name(name)
                .description(description)
                .available(available)
                .build();
    }
}
