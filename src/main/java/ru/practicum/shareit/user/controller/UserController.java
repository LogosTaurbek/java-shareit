package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.NewUserRequestDto;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@RequestBody NewUserRequestDto newUserRequestDto) {
        log.info("UserController:addUser(): запрос на создание нового пользователя {}", newUserRequestDto);
        return userService.addUser(newUserRequestDto);
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUserById(@PathVariable int userId) {
        log.info("UserController:getUserById(): запрос на получение пользователя с id {}", userId);
        return userService.getUserById(userId);
    }

    @PatchMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto updateUser(@PathVariable int userId, @RequestBody UpdateUserRequestDto updateUserRequestDto) {
        log.info("UserController:updateUser(): запрос на обновление пользователя с id {}. Новые данные: {}", userId, updateUserRequestDto);
        return userService.updateUser(userId, updateUserRequestDto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable int userId) {
        log.info("UserController:deleteUser(): запрос на удаление пользователя с id {}", userId);
        userService.deleteUser(userId);
    }
}