package ru.yandex.practicum.ewm.users.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.ewm.model.User;
import ru.yandex.practicum.ewm.repository.UserRepository;
import ru.yandex.practicum.ewm.users.dto.UserDto;
import ru.yandex.practicum.ewm.users.dto.UserShortDto;
import ru.yandex.practicum.ewm.users.mapper.UserMapper;


import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto getUserById(Long id) {
       Optional<User>  user = userRepository.findById(id);
       if (user.isEmpty()){
           throw new IllegalArgumentException("Не найдет пользователь"); // НУЖНО ДОДЕЛАТЬ EXCEPTIONS
       }
        return UserMapper.toDto(user.get());
    }

    @Override
    public UserDto addNewUser(UserShortDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw  new IllegalArgumentException("Не найдет пользователь");
        }
        User user = UserMapper.toEntity(userDto);
        User saved = userRepository.save(user);
        return UserMapper.toDto(saved);
    }

    @Override
    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
                throw new IllegalArgumentException("Не найдет пользователь"); // НУЖНО ДОДЕЛАТЬ EXCEPTIONS
            }
            userRepository.deleteById(id);
    }
}
