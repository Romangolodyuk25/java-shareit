package ru.practicum.shareit.item.comment.service;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentDtoIn;

public interface CommentService {
    CommentDto createComment(long itemId, CommentDtoIn commentDtoIn, long userId);
}
