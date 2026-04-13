package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public interface UserStorage {
    User addUser(User newUser);

    Optional<User> getUserById(int userId);

    boolean existsByEmail(String email);

    boolean emailUsedByOtherUser(String email, int userId);

    User updateUser(User updatedUser);

    void deleteUser(int userId);
}
