package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@DataJpaTest
@DisplayName("Item Repository")
public class ItemRepositoryTest {
    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    User user1;
    User user2;

    Item item1;
    Item item2;

    @BeforeEach
    public void beforeEach() {
        user1 = userRepository.save(new User(1L, "Ваня", "иванов@mail.ru"));
        item1 = itemRepository.save(new Item(1L, "Вещь1", "Умеет что-то делать", true, user1, null));

        user2 = userRepository.save(new User(2L, "Александр", "смирнов@mail.ru"));
        item2 = itemRepository.save(new Item(2L, "Вещь2", "Ничего не умеет делать", true, user2, null));
    }

    @Test
    @DisplayName("should find all items by Owner id")
    void shouldReturnAllByOwnerIdOrderBy() {
        List<Item> items = itemRepository.findAllByOwnerIdOrderBy(item1.getOwner().getId());

        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0).getId(),notNullValue());
        assertThat(items.get(0).getName(), equalTo("Вещь1"));
        assertThat(items.get(0).getDescription(), equalTo("Умеет что-то делать"));
        assertThat(items.get(0).getAvailable(), equalTo(true));
    }

    @Test
    @DisplayName("should return empty list for find all items by Owner id")
    void shouldReturnEmptyListForAllByOwnerIdOrderBy() {
        List<Item> items = itemRepository.findAllByOwnerIdOrderBy(9999);

        assertThat(items.size(), equalTo(0));
    }

    @Test
    @DisplayName("should find search items")
    void shouldSearchItemsWithPagination() {
        Pageable page = PageRequest.of(0, 1);
        Page<Item> items = itemRepository.searchItemsPageable("что-то делать", page, 1);

        assertThat(items.getContent().size(), equalTo(1));
        assertThat(items.getContent().get(0).getId(), equalTo(item1.getId()));
        assertThat(items.getContent().get(0).getName(), equalTo("Вещь1"));
        assertThat(items.getContent().get(0).getDescription(), equalTo("Умеет что-то делать"));
        assertThat(items.getContent().get(0).getAvailable(), equalTo(true));
    }

    @Test
    @DisplayName("should return items by owner id with pagination")
    void shouldReturnAllByOwnerIdOrderByWithPagination() {
        Pageable page = PageRequest.of(0, 10);
        Page<Item> items = itemRepository.findAllByOwnerId(user1.getId(), page);

        assertThat(items.getContent().size(), equalTo(1));
        assertThat(items.getContent().get(0).getId(), equalTo(item1.getId()));
        assertThat(items.getContent().get(0).getName(), equalTo("Вещь1"));
        assertThat(items.getContent().get(0).getDescription(), equalTo("Умеет что-то делать"));
        assertThat(items.getContent().get(0).getAvailable(), equalTo(true));
    }
}
