package ru.practicum.shareit.item.comment.dto;

import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class CommentDtoMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
                );
    }

    public static Comment toComment(CommentDto commentDto, Item item, User user) {
        return new Comment(commentDto.getId(),
                commentDto.getText(),
                item,
                user,
                commentDto.getCreated()
                );
    }
}
