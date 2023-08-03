package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserAlreadyExistsException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper mapper;
    private static final String USER_NOT_FOUND = "Пользователь с ID = %d  не найден!";
    public static final String EMAIL_ALREADY_EXISTS = "Пользователь с E-mail = %s уже существует!";

    @Autowired
    public UserServiceImpl(UserRepository repository, UserMapper userMapper) {
        this.repository = repository;
        this.mapper = userMapper;
    }

    @Override
    public List<UserDto> getUsers() {
        return repository.findAll().stream()
                .map(mapper::toUserDto)
                .collect(toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        return mapper.toUserDto(repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND, id))));
    }

    @Override
    public UserDto create(UserDto userDto) {
        try {
            return mapper.toUserDto(repository.save(mapper.toUser(userDto)));
        } catch (DataIntegrityViolationException e) {
            throw new UserAlreadyExistsException(String.format(EMAIL_ALREADY_EXISTS, userDto.getEmail()));
        }
    }

    @Override
    public UserDto update(UserDto dto, Long id) {
        Optional<User> userOpt = repository.findById(id);
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException(String.format(USER_NOT_FOUND, dto.getId()));
        }
        User user = userOpt.get();
        if (dto.getEmail() != null && !dto.getEmail().equals(user.getEmail())) {
            checkEmailExistException(dto.getEmail());
        }
        User updateUser = repository.save(updateUserFields(dto, user));
        return mapper.toUserDto(updateUser);
    }

    @Override
    public void delete(Long userId) {
        try {
            repository.deleteById(userId);
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException(String.format(USER_NOT_FOUND, userId));
        }
    }

    @Override
    public User findUserById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND, id)));
    }

    private void checkEmailExistException(String email) {
        if (repository.findAll().stream().anyMatch(a -> a.getEmail().equals(email)))
            throw new UserAlreadyExistsException(String.format(EMAIL_ALREADY_EXISTS, email)
            );
    }

    private User updateUserFields(UserDto dto, User user) {
        if (dto.getName() != null && dto.getName() != user.getName()) {
            user.setName(dto.getName());
        }
        if (dto.getEmail() != null && dto.getEmail() != user.getEmail()) {
            user.setEmail(dto.getEmail());
        }
        return user;
    }
}
