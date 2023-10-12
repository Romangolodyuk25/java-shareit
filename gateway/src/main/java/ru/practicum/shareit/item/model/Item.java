package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Item {

    @NotNull
    private Long id;

    @NotNull
    @Length(min = 1, max = 100)
    private String name;

    @NotNull
    private String description;

    @NotNull
    private Boolean available;

    @NotNull
    private User owner;
}
