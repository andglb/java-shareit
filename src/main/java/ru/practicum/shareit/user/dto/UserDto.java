package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank(message = "must not be blank")
    private String name;
    @Email(message = "must be a well-formed email address")
    @NotBlank(message = "must not be blank")
    private String email;
}
