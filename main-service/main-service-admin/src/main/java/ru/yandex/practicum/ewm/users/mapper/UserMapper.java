package ru.yandex.practicum.ewm.users.mapper;

import ru.yandex.practicum.ewm.model.User;
import ru.yandex.practicum.ewm.users.dto.NewUserRequest;
import ru.yandex.practicum.ewm.users.dto.UserDto;
import ru.yandex.practicum.ewm.users.dto.UserShortDto;


public class UserMapper {
    public static User toEntity(NewUserRequest dto) {
        if (dto == null) return null;
        return new User(dto.getName(), dto.getEmail());
    }

    public static UserDto toDto(User user) {
        if (user == null) return null;
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public static UserShortDto toShortDto(User user) {
        if (user == null) return null;
        return new UserShortDto(user.getId(), user.getName());
    }

}
