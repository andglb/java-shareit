package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingInputDto {
    @NotNull(message = "must not be null")
    private Long itemId;
    @FutureOrPresent(message = "must be a date in the present or in the future")
    @NotNull(message = "must not be null")
    private LocalDateTime start;
    @Future(message = "must be a future date")
    @NotNull(message = "must not be null")
    private LocalDateTime end;
}
