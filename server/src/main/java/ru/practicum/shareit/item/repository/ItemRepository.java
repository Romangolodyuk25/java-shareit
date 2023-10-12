package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select i from Item i " +
            "where upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%')) AND i.available = true")
    List<Item> searchItems(String text, long userId);

    @Query("select i " +
            "from Item i " +
            "where i.owner.id = ?1 " +
            "order by i.id asc ")
    List<Item> findAllByOwnerIdOrderBy(long ownerId);

    Page<Item> findAllByOwnerId(Long ownerId, Pageable pageable);

    @Query("select i from Item i " +
            "where upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%')) AND i.available = true")
    Page<Item> searchItemsPageable(String text, Pageable pageable, long userId);

}
