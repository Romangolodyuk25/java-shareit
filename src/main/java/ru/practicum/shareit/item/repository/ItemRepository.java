package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository {

    Item create(Item item, long userId);

    Item update(Item item, long id, long userId);

    List<Item> getAll(long userId);

    Item getById(long id);

    List<Item> searchItems(String text, long userId);
}
