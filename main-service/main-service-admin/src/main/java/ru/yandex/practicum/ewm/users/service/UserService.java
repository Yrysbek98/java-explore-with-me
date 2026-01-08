package ru.yandex.practicum.ewm.users.service;

import ru.yandex.practicum.ewm.dto.UserDto.NewUserRequest;
import ru.yandex.practicum.ewm.dto.UserDto.UserDto;

import java.util.List;


public interface UserService {
    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);
    UserDto registerUser(NewUserRequest request);
    void deleteUser(Long userId);
}
