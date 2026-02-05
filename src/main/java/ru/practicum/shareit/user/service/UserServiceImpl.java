package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DuplicatedDataException;
import ru.practicum.shareit.exceptions.UserNotValidException;
import ru.practicum.shareit.user.dto.NewUserRequestDto;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.NoSuchElementException;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    public UserServiceImpl(
            @Qualifier("UserStorageImpl") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public UserDto addUser(NewUserRequestDto newUserRequestDto) {
        log.info("UserServiceImpl:addUser(): запрос на создание нового пользователя {}", newUserRequestDto);

        validateNewUserRequestDto(newUserRequestDto);
        User newUser = UserMapper.newUserRequestDtoToUser(newUserRequestDto);

        if (userStorage.existsByEmail(newUser.getEmail())) {
            throw new DuplicatedDataException("Пользователь с email " + newUser.getEmail() + " уже имеется в базе данных");
        }

        User createdUser = userStorage.addUser(newUser);
        log.info("UserServiceImpl:addUser(): создан новый пользователь {}", createdUser);
        return UserMapper.userToUserDto(createdUser);
    }

    @Override
    public UserDto getUserById(int userId) {
        log.info("UserServiceImpl:getUserById(): запрос на получение пользователя с id {}", userId);
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователя с ID " + userId + " не существует"));
        return UserMapper.userToUserDto(user);
    }

    @Override
    public void validateNewUserRequestDto(NewUserRequestDto newUserRequestDto) {
        if (newUserRequestDto.getName() == null || newUserRequestDto.getName().isBlank()) {
            throw new UserNotValidException("Имя пользователя не может быть пустым или null");
        }
        if (newUserRequestDto.getEmail() == null || newUserRequestDto.getEmail().isBlank()) {
            throw new UserNotValidException("Email не может быть пустым или null");
        }
        if (!newUserRequestDto.getEmail().contains("@")) {
            throw new UserNotValidException("Email должен содержать @");
        }
    }

    @Override
    public UserDto updateUser(int userId, UpdateUserRequestDto updateUserRequestDto) {
        log.info("UserServiceImpl:updateUser(): запрос на редактирование пользователя с id={}, новые данные: {}", userId, updateUserRequestDto);

        User userToUpdate = checkUserExists(userId);

        User updatedUser = UserMapper.updateUserFields(userToUpdate, updateUserRequestDto);

        if (updatedUser.getEmail() != null && userStorage.emailUsedByOtherUser(updatedUser.getEmail(), userId)) {
            throw new DuplicatedDataException("email " + updatedUser.getEmail() + " используется другим пользователем");
        }

        updatedUser = userStorage.updateUser(updatedUser);
        log.info("UserServiceImpl:updateUser(): пользователь с id={} отредактирован, новые данные: {}", userId, updatedUser);
        return UserMapper.userToUserDto(updatedUser);
    }

    private User checkUserExists(int userId) {
        return userStorage.getUserById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователя с ID " + userId + " не существует"));
    }

    @Override
    public void deleteUser(int userId) {
        log.info("UserServiceImpl:deleteUser(): запрос на удаление пользователя с id={}", userId);

        userStorage.getUserById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователя с ID " + userId + " не существует"));

        userStorage.deleteUser(userId);
        log.info("UserServiceImpl:deleteUser(): пользователь с id={} удален", userId);
    }
}
