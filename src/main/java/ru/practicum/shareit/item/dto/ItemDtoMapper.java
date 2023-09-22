package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.item.comment.dto.CommentDtoMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemDtoMapper {
    public static ItemDto toItemDto(Item item, List<Comment> comments) {
        Long itemRequestId = null;
        if (item.getItemRequest() != null) {
            itemRequestId = item.getItemRequest().getId();
        }
        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null,
                comments.stream().map(CommentDtoMapper::toCommentDto).collect(Collectors.toList()),
                itemRequestId
        );
    }

    public static ItemDto toItemDtoWithBooking(Item item, BookingDtoForItem lastBooking, BookingDtoForItem nextBooking, List<Comment> comments) {
        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBooking,
                nextBooking,
                comments.stream().map(CommentDtoMapper::toCommentDto).collect(Collectors.toList()),
                null
        );
    }

    public static Item toItem(ItemDto itemDto, User owner, ItemRequest itemRequest) {
        return new Item(itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                owner,
                itemRequest
        );
    }

    public static ItemDtoForItemRequest toItemDtoFroRequest(ItemDto itemDto) {
        return ItemDtoForItemRequest.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .requestId(itemDto.getRequestId())
                .build();
    }

}
