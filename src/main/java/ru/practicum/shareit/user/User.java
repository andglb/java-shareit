package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class User {
    private Long id;
    @NotBlank(message = "Имя не может быть пустым!")
    private String name;
    @Email(message = "Почта указана некорректно!")
    @NotBlank(message = "Почта не может быть пустой!")
    private String email;
}