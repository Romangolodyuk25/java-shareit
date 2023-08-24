package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.UserNotExistObject;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
@Slf4j
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private Long id = 0L;
    private final Set<String> emailUniqSet = new HashSet<>();

    @Override
    public User create(User user) {
        user.setId(++id);
        emailUniqSet.add(user.getEmail());
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

    public User checkUserFromUpdate(User user, long id) {
        User receivedUser = getById(id);
        if (receivedUser.getEmail().equals(user.getEmail())) {
            return receivedUser;
        }
        if (user.getName() != null) {
            receivedUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            if (receivedUser.getEmail().equals(user.getEmail())) {
                return receivedUser;
            }
            if (emailUniqSet.contains(user.getEmail())) {
                log.info("Юзер с email " + user.getEmail() + " уже существует");
                throw new RuntimeException("Юзер с данныи имейлом уже существует");
            }
            receivedUser.setEmail(user.getEmail());
        }
        return receivedUser;
    }
}
