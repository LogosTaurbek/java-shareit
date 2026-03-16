package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.UserNotValidException;
import ru.practicum.shareit.user.dto.NewUserRequestDto;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto addUser(NewUserRequestDto newUserRequestDto) {
        log.info("UserServiceImpl:addUser(): запрос на создание нового пользователя {}", newUserRequestDto);
        validateNewUserRequestDto(newUserRequestDto);
        User newUser = UserMapper.newUserRequestDtoToUser(newUserRequestDto);
        User createdUser = userRepository.save(newUser);
        log.info("UserServiceImpl:addUser(): создан новый пользователь {}", createdUser);
        return UserMapper.userToUserDto(createdUser);
    }

    @Override
    public UserDto getUserById(int userId) {
        log.info("UserServiceImpl:getUserById(): запрос на получение пользователя с id {}", userId);
        User user = userRepository.findById(userId)
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
        log.info(
                "UserServiceImpl:updateUser(): запрос на редактирование пользователя с id={}, новые данные: {}",
                userId,
                updateUserRequestDto
        );
        User userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователя с ID " + userId + " не существует"));
        User updatedUser = UserMapper.updateUserFields(userToUpdate, updateUserRequestDto);
        updatedUser = userRepository.save(updatedUser);
        log.info(
                "UserServiceImpl:updateUser(): пользователь с id={} отредактирован, новые данные: {}",
                userId,
                updatedUser
        );
        return UserMapper.userToUserDto(updatedUser);
    }

    @Override
    public void deleteUser(int userId) {
        log.info("UserServiceImpl:deleteUser(): запрос на удаление пользователя с id={}", userId);
        userRepository.deleteById(userId);
        log.info("UserServiceImpl:deleteUser(): пользователь с id={} удален", userId);
    }
}
