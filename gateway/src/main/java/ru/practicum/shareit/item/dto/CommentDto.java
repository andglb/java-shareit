package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentDto {
    private Long id;                // уникальный идентификатор комментария;
    @NotEmpty(message = "must not be empty")
    @NotBlank(message = "must not be null")
    private String text;            // содержимое комментария;
    private String authorName;      // имя автора комментария;
    private LocalDateTime created;  // дата создания комментария.
}
