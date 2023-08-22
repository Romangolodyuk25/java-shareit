package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class User {
    @NotNull
    private Long id;
    @NotNull
    private String name;
    @Email
    private String email;
}
