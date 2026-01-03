package ru.yandex.practicum.ewm.users.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.ewm.users.dto.UserDto;
import ru.yandex.practicum.ewm.users.dto.UserShortDto;
import ru.yandex.practicum.ewm.users.service.UserService;



@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/users")
public class UserController {
    private final UserService userService;
    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping()
    public UserDto addNewUser(@Valid @RequestBody UserShortDto userDto) {
        return userService.addNewUser(userDto);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
    }
}
