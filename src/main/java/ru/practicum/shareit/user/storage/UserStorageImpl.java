package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component("UserStorageImpl")
@Slf4j
public class UserStorageImpl implements UserStorage {
    private final Map<Integer, User> userMap = new HashMap<>();
    private int currentId = 1;

    @Override
    public User addUser(User newUser) {
        log.info("UserStorageImpl:addUser(): запрос на создание нового пользователя {}", newUser);
        int id = getNextId();
        newUser.setId(id);
        userMap.put(id, newUser);
        log.info("UserStorageImpl:addUser(): создан новый пользователь {}", newUser);
        return newUser;
    }

    @Override
    public Optional<User> getUserById(int userId) {
        log.info("UserStorageImpl:getUserById(): запрос на получение пользователя с id {}", userId);
        return Optional.ofNullable(userMap.get(userId));
    }

    @Override
    public boolean existsByEmail(String email) {
        return userMap.values().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }

    @Override
    public boolean emailUsedByOtherUser(String email, int userId) {
        return userMap.values().stream()
                .anyMatch(user -> user.getId() != userId && user.getEmail().equals(email));
    }


    @Override
    public User updateUser(User updatedUser) {
        log.info("UserStorageImpl:updateUser(): запрос на обновление пользователя {}", updatedUser);
        userMap.put(updatedUser.getId(), updatedUser);
        log.info("UserStorageImpl:updateUser(): пользователь {} обновлен", updatedUser);
        return updatedUser;
    }

    @Override
    public void deleteUser(int userId) {
        log.info("UserStorageImpl:deleteUser(): запрос на удаление пользователя с id {}", userId);
        userMap.remove(userId);
        log.info("UserStorageImpl:deleteUser(): пользователь с id {} удалён", userId);
    }

    private int getNextId() {
        return currentId++;
    }

}
