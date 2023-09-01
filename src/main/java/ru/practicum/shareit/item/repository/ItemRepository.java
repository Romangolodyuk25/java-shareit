package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT i " +
            "FROM ITEM i " +
            "WHERE UPPER(i.name) LIKE UPPER(concat('%', ?1, '%')) " +
            " OR UPPER(i.description) LIKE UPPER(concat('%', ?1, '%'))")
    List<Item> searchItems(String text, long userId);
}
