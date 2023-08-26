package ru.practicum.shareit.requests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ItemRequestMapper {

    private UserMapper userMapper;

    @Autowired
    public ItemRequestMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest, List<ItemDto> items) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                userMapper.toUserDto(itemRequest.getRequestor()),
                itemRequest.getCreated(),
                items
        );
    }

    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto, LocalDateTime created, User user) {
        return new ItemRequest(
                null,
                itemRequestDto.getDescription(),
                user,
                created
        );
    }
}
