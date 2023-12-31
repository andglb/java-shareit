package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTest {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;
    private User user = new User(300L, "UserFirstName", "UserFirst@yandex.ru");
    private UserDto userDto1 = new UserDto(301L, "AndrewOne", "andrewone@yandex.ru");
    private UserDto userDto2 = new UserDto(302L, "AndrewTwo", "andrewtwo@yandex.ru");
    private ItemDto itemDto1 = new ItemDto(301L, "ItemFirstName", "ItemFirstDescription", true,
            user, null, null, null, null);
    private ItemDto itemDto2 = new ItemDto(302L, "ItemSecondName", "ItemSecondDescription", true,
            user, null, null, null, null);

    @Test
    void shouldExceptionWhenCreateBookingByOwnerItem() {
        UserDto ownerDto = userService.create(userDto1);
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());
        BookingInputDto bookingInputDto = new BookingInputDto(
                newItemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 00, 00),
                LocalDateTime.of(2030, 12, 26, 12, 00, 00));
        BookingNotFoundException exp = assertThrows(BookingNotFoundException.class,
                () -> bookingService.create(bookingInputDto, ownerDto.getId()));
        assertEquals("Вещь с ID = " + newItemDto.getId() + " недоступна для бронирования самим владельцем!",
                exp.getMessage());
    }

    @Test
    void shouldExceptionWhenGetBookingByNotOwnerOrNotBooker() {
        UserDto ownerDto = userService.create(userDto1);
        UserDto newUserDto = userService.create(userDto2);
        UserDto userDto3 = new UserDto(303L, "AndrewThird", "andrewthird@yandex.ru");
        userDto3 = userService.create(userDto3);
        Long userId = userDto3.getId();
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());
        BookingInputDto bookingInputDto = new BookingInputDto(
                newItemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 00, 00),
                LocalDateTime.of(2030, 12, 26, 12, 00, 00));
        BookingDto bookingDto = bookingService.create(bookingInputDto, newUserDto.getId());
        UserNotFoundException exp = assertThrows(UserNotFoundException.class,
                () -> bookingService.getBookingById(bookingDto.getId(), userId));
        assertEquals("Посмотреть данные бронирования может только владелец вещи или бронирующий ее!",
                exp.getMessage());
    }

    @Test
    void shouldReturnBookingsWhenGetBookingsByBookerAndSizeIsNull() {
        UserDto ownerDto = userService.create(userDto1);
        UserDto newUserDto = userService.create(userDto2);
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());
        BookingInputDto bookingInputDto = new BookingInputDto(
                newItemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 00, 00),
                LocalDateTime.of(2030, 12, 26, 12, 00, 00));
        bookingService.create(bookingInputDto, newUserDto.getId());
        BookingInputDto bookingInputDto1 = new BookingInputDto(
                newItemDto.getId(),
                LocalDateTime.of(2031, 12, 25, 12, 00, 00),
                LocalDateTime.of(2031, 12, 26, 12, 00, 00));
        bookingService.create(bookingInputDto1, newUserDto.getId());
        List<BookingDto> listBookings = bookingService.getBookings("ALL", newUserDto.getId(), 0, null);
        assertEquals(2, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetBookingsByBookerAndSizeIsNotNull() {
        UserDto ownerDto = userService.create(userDto1);
        UserDto newUserDto = userService.create(userDto2);
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());
        BookingInputDto bookingInputDto = new BookingInputDto(
                newItemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 00, 00),
                LocalDateTime.of(2030, 12, 26, 12, 00, 00));
        bookingService.create(bookingInputDto, newUserDto.getId());
        BookingInputDto bookingInputDto1 = new BookingInputDto(
                newItemDto.getId(),
                LocalDateTime.of(2031, 12, 25, 12, 00, 00),
                LocalDateTime.of(2031, 12, 26, 12, 00, 00));
        bookingService.create(bookingInputDto1, newUserDto.getId());
        List<BookingDto> listBookings = bookingService.getBookings("ALL", newUserDto.getId(), 0, 1);
        assertEquals(1, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetBookingsInWaitingStatusByBookerAndSizeIsNull() {
        UserDto ownerDto = userService.create(userDto1);
        UserDto newUserDto = userService.create(userDto2);
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());
        BookingInputDto bookingInputDto = new BookingInputDto(
                newItemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 00, 00),
                LocalDateTime.of(2030, 12, 26, 12, 00, 00));
        bookingService.create(bookingInputDto, newUserDto.getId());
        BookingInputDto bookingInputDto1 = new BookingInputDto(
                newItemDto.getId(),
                LocalDateTime.of(2031, 12, 25, 12, 00, 00),
                LocalDateTime.of(2031, 12, 26, 12, 00, 00));
        bookingService.create(bookingInputDto1, newUserDto.getId());
        List<BookingDto> listBookings = bookingService.getBookings("WAITING", newUserDto.getId(),
                0, null);
        List<BookingDto> listBookingsCurrent = bookingService.getBookings("CURRENT", newUserDto.getId(),
                0, null);
        List<BookingDto> listBookingsPast = bookingService.getBookings("PAST", newUserDto.getId(),
                0, null);
        List<BookingDto> listBookingsFuture = bookingService.getBookings("FUTURE", newUserDto.getId(),
                0, null);
        assertEquals(0, listBookingsCurrent.size());
        assertEquals(0, listBookingsPast.size());
        assertEquals(2, listBookingsFuture.size());
        assertEquals(2, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetBookingsInWaitingStatusByBookerAndSizeNotNull() {
        UserDto ownerDto = userService.create(userDto1);
        UserDto newUserDto = userService.create(userDto2);
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());
        BookingInputDto bookingInputDto = new BookingInputDto(
                newItemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 00, 00),
                LocalDateTime.of(2030, 12, 26, 12, 00, 00));
        bookingService.create(bookingInputDto, newUserDto.getId());
        BookingInputDto bookingInputDto1 = new BookingInputDto(
                newItemDto.getId(),
                LocalDateTime.of(2031, 12, 25, 12, 00, 00),
                LocalDateTime.of(2031, 12, 26, 12, 00, 00));
        bookingService.create(bookingInputDto1, newUserDto.getId());
        List<BookingDto> listBookings = bookingService.getBookings("WAITING", newUserDto.getId(),
                0, 1);
        assertEquals(1, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetBookingsInRejectedStatusByBookerAndSizeIsNull() {
        UserDto ownerDto = userService.create(userDto1);
        UserDto newUserDto = userService.create(userDto2);
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());
        BookingInputDto bookingInputDto = new BookingInputDto(
                newItemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 00, 00),
                LocalDateTime.of(2030, 12, 26, 12, 00, 00));
        bookingService.create(bookingInputDto, newUserDto.getId());
        BookingInputDto bookingInputDto1 = new BookingInputDto(
                newItemDto.getId(),
                LocalDateTime.of(2031, 12, 25, 12, 00, 00),
                LocalDateTime.of(2031, 12, 26, 12, 00, 00));
        bookingService.create(bookingInputDto1, newUserDto.getId());
        List<BookingDto> listBookings = bookingService.getBookings("REJECTED", newUserDto.getId(),
                0, null);
        assertEquals(0, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetBookingsInRejectedStatusByBookerAndSizeNotNull() {
        UserDto ownerDto = userService.create(userDto1);
        UserDto newUserDto = userService.create(userDto2);
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());
        BookingInputDto bookingInputDto = new BookingInputDto(
                newItemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 00, 00),
                LocalDateTime.of(2030, 12, 26, 12, 00, 00));
        bookingService.create(bookingInputDto, newUserDto.getId());
        BookingInputDto bookingInputDto1 = new BookingInputDto(
                newItemDto.getId(),
                LocalDateTime.of(2031, 12, 25, 12, 00, 00),
                LocalDateTime.of(2031, 12, 26, 12, 00, 00));
        bookingService.create(bookingInputDto1, newUserDto.getId());
        List<BookingDto> listBookings = bookingService.getBookings("REJECTED", newUserDto.getId(),
                0, 1);
        assertEquals(0, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetBookingsByOwnerAndSizeIsNull() {
        UserDto ownerDto = userService.create(userDto1);
        UserDto newUserDto = userService.create(userDto2);
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());
        BookingInputDto bookingInputDto = new BookingInputDto(
                newItemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 00, 00),
                LocalDateTime.of(2030, 12, 26, 12, 00, 00));
        bookingService.create(bookingInputDto, newUserDto.getId());
        BookingInputDto bookingInputDto1 = new BookingInputDto(
                newItemDto.getId(),
                LocalDateTime.of(2031, 12, 25, 12, 00, 00),
                LocalDateTime.of(2031, 12, 26, 12, 00, 00));
        bookingService.create(bookingInputDto1, newUserDto.getId());
        List<BookingDto> listBookings = bookingService.getBookingsOwner("ALL", ownerDto.getId(),
                0, null);
        assertEquals(2, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetBookingsByOwnerAndSizeNotNull() {
        UserDto ownerDto = userService.create(userDto1);
        UserDto newUserDto = userService.create(userDto2);
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());
        BookingInputDto bookingInputDto = new BookingInputDto(
                newItemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 00, 00),
                LocalDateTime.of(2030, 12, 26, 12, 00, 00));
        bookingService.create(bookingInputDto, newUserDto.getId());
        BookingInputDto bookingInputDto1 = new BookingInputDto(
                newItemDto.getId(),
                LocalDateTime.of(2031, 12, 25, 12, 00, 00),
                LocalDateTime.of(2031, 12, 26, 12, 00, 00));
        bookingService.create(bookingInputDto1, newUserDto.getId());
        List<BookingDto> listBookings = bookingService.getBookingsOwner("ALL", ownerDto.getId(),
                0, 1);
        assertEquals(1, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetBookingsByOwnerAndStatusWaitingAndSizeIsNull() {
        UserDto ownerDto = userService.create(userDto1);
        UserDto newUserDto = userService.create(userDto2);
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());
        BookingInputDto bookingInputDto = new BookingInputDto(
                newItemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 00, 00),
                LocalDateTime.of(2030, 12, 26, 12, 00, 00));
        bookingService.create(bookingInputDto, newUserDto.getId());
        BookingInputDto bookingInputDto1 = new BookingInputDto(
                newItemDto.getId(),
                LocalDateTime.of(2031, 12, 25, 12, 00, 00),
                LocalDateTime.of(2031, 12, 26, 12, 00, 00));
        bookingService.create(bookingInputDto1, newUserDto.getId());
        List<BookingDto> listBookings = bookingService.getBookingsOwner("WAITING", ownerDto.getId(),
                0, null);
        List<BookingDto> listBookingsCurrent = bookingService.getBookingsOwner("CURRENT", newUserDto.getId(),
                0, null);
        List<BookingDto> listBookingsPast = bookingService.getBookingsOwner("PAST", newUserDto.getId(),
                0, null);
        List<BookingDto> listBookingsFuture = bookingService.getBookingsOwner("FUTURE", newUserDto.getId(),
                0, null);
        assertEquals(0, listBookingsCurrent.size());
        assertEquals(0, listBookingsPast.size());
        assertEquals(0, listBookingsFuture.size());
        assertEquals(2, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetBookingsByOwnerAndStatusWaitingAndSizeNotNull() {
        UserDto ownerDto = userService.create(userDto1);
        UserDto newUserDto = userService.create(userDto2);
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());
        BookingInputDto bookingInputDto = new BookingInputDto(
                newItemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 00, 00),
                LocalDateTime.of(2030, 12, 26, 12, 00, 00));
        bookingService.create(bookingInputDto, newUserDto.getId());
        BookingInputDto bookingInputDto1 = new BookingInputDto(
                newItemDto.getId(),
                LocalDateTime.of(2031, 12, 25, 12, 00, 00),
                LocalDateTime.of(2031, 12, 26, 12, 00, 00));
        bookingService.create(bookingInputDto1, newUserDto.getId());
        List<BookingDto> listBookings = bookingService.getBookingsOwner("WAITING", ownerDto.getId(),
                0, 1);
        assertEquals(1, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetBookingsByOwnerAndStatusRejectedAndSizeIsNull() {
        UserDto ownerDto = userService.create(userDto1);
        UserDto newUserDto = userService.create(userDto2);
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());
        BookingInputDto bookingInputDto = new BookingInputDto(
                newItemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 00, 00),
                LocalDateTime.of(2030, 12, 26, 12, 00, 00));
        bookingService.create(bookingInputDto, newUserDto.getId());
        BookingInputDto bookingInputDto1 = new BookingInputDto(
                newItemDto.getId(),
                LocalDateTime.of(2031, 12, 25, 12, 00, 00),
                LocalDateTime.of(2031, 12, 26, 12, 00, 00));
        bookingService.create(bookingInputDto1, newUserDto.getId());
        List<BookingDto> listBookings = bookingService.getBookingsOwner("REJECTED", ownerDto.getId(),
                0, null);
        assertEquals(0, listBookings.size());
    }

    @Test
    void shouldReturnBookingsWhenGetBookingsByOwnerAndStatusRejectedAndSizeNotNull() {
        UserDto ownerDto = userService.create(userDto1);
        UserDto newUserDto = userService.create(userDto2);
        ItemDto newItemDto = itemService.create(itemDto1, ownerDto.getId());
        BookingInputDto bookingInputDto = new BookingInputDto(
                newItemDto.getId(),
                LocalDateTime.of(2030, 12, 25, 12, 00, 00),
                LocalDateTime.of(2030, 12, 26, 12, 00, 00));
        bookingService.create(bookingInputDto, newUserDto.getId());
        BookingInputDto bookingInputDto1 = new BookingInputDto(
                newItemDto.getId(),
                LocalDateTime.of(2031, 12, 25, 12, 00, 00),
                LocalDateTime.of(2031, 12, 26, 12, 00, 00));
        bookingService.create(bookingInputDto1, newUserDto.getId());
        List<BookingDto> listBookings = bookingService.getBookingsOwner("REJECTED", ownerDto.getId(),
                0, 1);
        assertEquals(0, listBookings.size());
    }
}