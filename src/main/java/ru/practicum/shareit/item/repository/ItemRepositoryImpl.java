package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ItemNotExistException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private Long id = 0L;

    @Override
    public Item create(Item item, long userId) {
        item.setItemId(++id);
        items.put(id, item);
        return getById(id);
    }

    @Override
    public Item update(Item item, long id, long userId) {
        Item receivedItem = checkItemFromUpdate(item, id, userId);
        items.put(id, receivedItem);
        return receivedItem;
    }

    @Override
    public List<Item> getAll(long userId) {
        return items.values().stream()
                .filter(i -> i.getOwner().getId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public Item getById(long id) {
        if (!items.containsKey(id)) {
            throw new ItemNotExistException("Вещи с айди " + id + " не существует");
        }
        //если данной вещи нет нужно создать на нее запрос new ItemRequest();
        return items.get(id);
    }

    @Override
    public List<Item> searchItems(String text, long userId) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }
        return items.values().stream()
                .filter(i -> i.getName().toLowerCase().contains(text.toLowerCase()) ||
                        i.getDescription().toLowerCase().contains(text.toLowerCase()) && i.getAvailable())
                .collect(Collectors.toList());
    }

    private Item checkItemFromUpdate(Item item, long itemId, long userId) {
        Item otherItem = getById(itemId);
        if (otherItem.getOwner().getId() != userId) {
            throw new NotOwnerException("Пользователь с айди " + userId + " не является владельцем вещи");
        }
        if (item.getName() != null) {
            otherItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            otherItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            otherItem.setAvailable(item.getAvailable());
        }

        return otherItem;
    }
}
