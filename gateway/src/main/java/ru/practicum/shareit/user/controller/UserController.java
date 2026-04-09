package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.dto.NewUserRequestDto;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> addUser(@RequestBody @Valid NewUserRequestDto newUserRequestDto) {
        log.info("UserController:addUser(): запрос на создание нового пользователя {}", newUserRequestDto);
        return userClient.addUser(newUserRequestDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable int userId) {
        log.info("UserController:getUserById(): запрос на получение пользователя с id {}", userId);
        return userClient.getUserById(userId);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable int userId, @RequestBody @Valid UpdateUserRequestDto updateUserRequestDto) {
        log.info("UserController:updateUser(): запрос на обновление пользователя с id {}. Новые данные: {}", userId, updateUserRequestDto);
        return userClient.updateUser(userId, updateUserRequestDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable int userId) {
        log.info("UserController:deleteUser(): запрос на удаление пользователя с id {}", userId);
        return userClient.deleteUser(userId);
    }
}
