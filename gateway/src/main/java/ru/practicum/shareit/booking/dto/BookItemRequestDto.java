package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {
	@NotNull(message = "must not be null")
	private long itemId;
	@FutureOrPresent(message = "must be a date in the present or in the future")
	@NotNull(message = "must not be null")
	private LocalDateTime start;
	@Future(message = "must be a future date")
	@NotNull(message = "must not be null")
	private LocalDateTime end;
}
