package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findByRequestor_Id(long userId, Sort sort);

    ItemRequest findByRequestor_Id(long userId);

    Page<ItemRequest> findByRequestor_IdNot(long userId, Pageable pageable);
}
