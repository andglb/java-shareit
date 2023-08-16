package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.service.CheckConsistencyService;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

@Component
public class BookingMapper {
    private UserService userService;
    private ItemService itemService;
    private UserMapper userMapper;
    private ItemMapper itemMapper;
    private CheckConsistencyService checker;

    @Autowired
    public BookingMapper(UserService userService, ItemService itemService,
                         UserMapper userMapper, ItemMapper itemMapper, CheckConsistencyService checker) {
        this.userService = userService;
        this.itemService = itemService;
        this.userMapper = userMapper;
        this.itemMapper = itemMapper;
        this.checker = checker;
    }

    public BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                itemMapper.toItemDto(booking.getItem(), checker.getCommentsByItemId(booking.getItem().getId())),
                userMapper.toUserDto(booking.getBooker()),
                booking.getStatus()
        );
    }

    public BookingShortDto toBookingShortDto(Booking booking) {
        return new BookingShortDto(
                booking.getId(),
                booking.getBooker().getId(),
                booking.getStart(),
                booking.getEnd()
        );
    }

    public Booking toBooking(BookingInputDto bookingInputDto, Long bookerId) {
        return new Booking(
                null,
                bookingInputDto.getStart(),
                bookingInputDto.getEnd(),
                itemService.findItemById(bookingInputDto.getItemId()),
                userService.findUserById(bookerId),
                Status.WAITING
        );
    }
}
