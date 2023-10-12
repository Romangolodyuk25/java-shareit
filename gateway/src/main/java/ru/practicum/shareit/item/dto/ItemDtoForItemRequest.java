package ru.practicum.shareit.item.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDtoForItemRequest {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
}
