package ru.practicum.shareit.item.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotExistException;
import ru.practicum.shareit.exception.UserNotExistObject;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentDtoIn;
import ru.practicum.shareit.item.comment.dto.CommentDtoMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Override
    public CommentDto createComment(long itemId, CommentDtoIn commentDtoIn, long userId) {
        User receivedUser = userRepository.findById(userId).orElseThrow(() -> new UserNotExistObject("user not exist"));
        Item receivedItem = itemRepository.findById(userId).orElseThrow(() -> new ItemNotExistException("item not exist"));

        return CommentDtoMapper.toCommentDto(commentRepository.save(new Comment(null,
                commentDtoIn.getText(),
                receivedItem,
                receivedUser,
                LocalDateTime.now())));
    }
}
