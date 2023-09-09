package ru.practicum.shareit.item.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
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

import javax.validation.ValidationException;
import java.awt.print.Book;
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
        Item receivedItem = itemRepository.findById(itemId).orElseThrow(() -> new ValidationException("item not exist"));
        validated(commentDtoIn, receivedUser, receivedItem);

        return CommentDtoMapper.toCommentDto(commentRepository.save(new Comment(null,
                commentDtoIn.getText(),
                receivedItem,
                receivedUser,
                LocalDateTime.now())));
    }

    private void validated(CommentDtoIn commentDtoIn, User user, Item item) {
        List<Booking> lastBooking = bookingRepository.findAllBookingByUserId(user.getId());
        if(lastBooking == null || lastBooking.isEmpty()) {
            throw new ValidationException();
        }
        List<Booking> bookingList = lastBooking.stream()
                    .filter(x -> x.getItem().getId().equals(item.getId()))
                .filter(x -> x.getEnd().isBefore(LocalDateTime.now()))
                    .collect(Collectors.toList());
        if (bookingList.isEmpty()) {
            throw new ValidationException();
        }
//        if (lastBooking == null || lastBooking.size() == 0) {
//            throw new ValidationException();
//        }
//        List<Long> lastBookingIds = lastBooking.stream()
//                .map(b -> b.getBooker().getId())
//                .collect(Collectors.toList());
//
        if (commentDtoIn == null || commentDtoIn.getText().isEmpty()) {
            throw new ValidationException();
        }
//        if (!lastBookingIds.contains(user.getId())) {
//            throw new ValidationException();
//        } else {
//            bookingList.sort((x1, x2) -> x1.getEnd().compareTo(x2.getEnd()));
//            if (LocalDateTime.now().isBefore(bookingList.get(0).getEnd())) {
//                throw new ValidationException();
//            }
//        }
    }
}
