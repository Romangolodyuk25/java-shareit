package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Repository
public interface UserRepository {

    User create(User user);

    User update(User user, long id);

    List<User> getAll();

    User getById(long id);

    void deleteById(long id);
}
