package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.exception.model.ItemNotExistException;
import ru.practicum.shareit.user.exception.model.NotOwnerException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemRepositoryImpl implements ItemRepository {
    private final HashMap<Long, Item> items = new HashMap<>();
    private Long id = 0L;

    @Override
    public Item create(Item item, long userId) {
        item.setItemId(++id);
        item.setOwnerId(userId);
        items.put(id, item);
        return getById(id, userId);
    }

    @Override
    public Item update(Item item, long id, long userId) {
        Item receivedItem = checkItemFromUpdate(item, userId);
        items.put(id, receivedItem);
        return getById(id, userId);
    }

    @Override
    public List<Item> getAll(long userId) {
        return items.values().stream()
                .filter(i -> i.getOwnerId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public Item getById(long id, long userId) {
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

    private Item checkItemFromUpdate(Item item, long userId) {
        Item otherItem = getById(id, userId);
        if (otherItem.getOwnerId() != userId) {
            throw new NotOwnerException("Пользователь с айди " + userId + " не является владельцем вещи");
        }
        if (item.getName() != null && item.getDescription() == null && item.getAvailable() == null) {
            otherItem.setName(item.getName());
        } else if (item.getDescription() != null && item.getName() == null && item.getAvailable() == null) {
            otherItem.setDescription(item.getDescription());
        } else if (item.getAvailable() != null && item.getName() == null && item.getDescription() == null) {
            otherItem.setAvailable(item.getAvailable());
        } else {
            otherItem.setName(item.getName());
            otherItem.setDescription(item.getDescription());
            otherItem.setAvailable(item.getAvailable());
        }
        return otherItem;
    }
}
