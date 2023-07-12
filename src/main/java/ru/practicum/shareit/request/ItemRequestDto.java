package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ItemRequestDto {
    private String description;
    private String requestorName;
    private LocalDateTime created;
}
