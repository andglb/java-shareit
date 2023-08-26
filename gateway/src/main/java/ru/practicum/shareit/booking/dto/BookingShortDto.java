package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingShortDto {
    private Long id;
    @NotNull(message = "must not be null")
    private Long bookerId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}

