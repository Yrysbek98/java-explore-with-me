package ru.yandex.practicum.ewm.users.service;

import ru.yandex.practicum.ewm.users.dto.UserDto;
import ru.yandex.practicum.ewm.users.dto.UserShortDto;



public interface UserService {
    UserDto getUserById(Long id);

    UserDto addNewUser(UserShortDto userDto);

    void deleteUserById(Long id);
}
