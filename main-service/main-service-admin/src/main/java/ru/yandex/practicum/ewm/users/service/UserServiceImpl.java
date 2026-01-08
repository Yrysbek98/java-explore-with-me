package ru.yandex.practicum.ewm.users.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ewm.exception.exceptionType.ConflictException;
import ru.yandex.practicum.ewm.exception.exceptionType.NotFoundException;
import ru.yandex.practicum.ewm.model.User;
import ru.yandex.practicum.ewm.repository.UserRepository;
import ru.yandex.practicum.ewm.users.dto.NewUserRequest;
import ru.yandex.practicum.ewm.users.dto.UserDto;

import ru.yandex.practicum.ewm.users.mapper.UserMapper;


import java.util.List;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);

        List<User> users;

        if (ids == null || ids.isEmpty()) {
            users = userRepository.findAll(pageable).getContent();
        } else {
            users = userRepository.findAllById(ids);
        }

        return users.stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto registerUser(NewUserRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new ConflictException("Пользователь с таким email уже существует");
            }
        }

        User user = UserMapper.toEntity(request);
        User saved = userRepository.save(user);
        return UserMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        userRepository.deleteById(userId);
    }
}
