package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.exceptions.DuplicatedDataException;
import ru.practicum.shareit.user.model.User;

public interface UserStorage {
    User addUser(User newUser);

    User getUserById(int userId);

    void checkIfUserExists(User newUser) throws DuplicatedDataException;

    User updateUser(User updatedUser);

    void deleteUser(int userId);
}
