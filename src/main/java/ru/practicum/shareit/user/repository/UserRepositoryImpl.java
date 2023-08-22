package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.model.UserNotExistObject;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class UserRepositoryImpl implements UserRepository {
    private final HashMap<Long, User> users = new HashMap<>();
    private Long id = 0L;

    @Override
    public User create(User user) {
        user.setId(++id);
        users.put(id, user);
        return getById(user.getId());
    }

    @Override
    public User update(User user, long id) {
        user.setId(id);
        User receivedUser = checkUserFromUpdate(user, id);
        users.put(user.getId(), receivedUser);
        return getById(id);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getById(long id) {
        User receivedUser = users.get(id);
        if (receivedUser == null) {
            throw new UserNotExistObject("Юзер с айди " + id + " Не существует");
        }
        return receivedUser;
    }

    @Override
    public void deleteById(long id) {
        users.remove(id);
    }

    public void clearUsers() {
        users.clear();
        log.info("Хранилище пользователей в памяти отчищенно");
    }

    public User checkUserFromUpdate(User user, long id) {
        User receivedUser = getById(id);
        if (user.getName() != null && user.getEmail() != null) {
            receivedUser.setName(user.getName());
            receivedUser.setEmail(user.getEmail());
        } else if (user.getName() != null && user.getEmail() == null) {
            receivedUser.setName(user.getName());
        } else {
            List<User> listUsers = getAll();
            if (receivedUser.getEmail().equals(user.getEmail())) {
                return receivedUser;
            }
            for (User u : listUsers) {
                if (u.getEmail().equals(user.getEmail())) {
                    log.info("Юзер с email " + user.getEmail() + " уже существует");
                    throw new RuntimeException();
                }
            }
            receivedUser.setEmail(user.getEmail());
        }
        return receivedUser;
    }
}
