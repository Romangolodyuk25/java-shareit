package ru.practicum.shareit.item.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ItemRequestNotExist;
import ru.practicum.shareit.exception.LastBookingsNotExistException;
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
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    public CommentDto createComment(long itemId, CommentDtoIn commentDtoIn, long userId) {
        User receivedUser = userRepository.findById(userId).orElseThrow(() -> new UserNotExistObject("user not exist"));
        Item receivedItem = itemRepository.findById(itemId).orElseThrow(() -> new ItemRequestNotExist("item not exist"));

        List<Booking> lastBooking = bookingRepository.findAllBookingByUserId(receivedUser.getId());

        if (lastBooking == null || lastBooking.isEmpty()) {
            throw new LastBookingsNotExistException("last booking not exist");
        }
        List<Booking> bookingList = lastBooking.stream()
                .filter(x -> x.getItem().getId().equals(receivedItem.getId()))
                .filter(x -> x.getEnd().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());
        if (bookingList.isEmpty()) {
            throw new LastBookingsNotExistException("last booking not exist");
        }

        return CommentDtoMapper.toCommentDto(commentRepository.save(Comment.builder()
                .text(commentDtoIn.getText())
                .item(receivedItem)
                .author(receivedUser)
                .created(LocalDateTime.now())
                .build()
        ));
    }
}
