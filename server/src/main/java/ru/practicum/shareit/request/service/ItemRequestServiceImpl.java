package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemRequestNotExist;
import ru.practicum.shareit.exception.UserNotExistObject;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    public Sort sort = Sort.by(Sort.Direction.DESC, "created");

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemRequestDto createRequest(ItemRequestDtoIn itemRequestDtoIn, long userId) {
        User user = userRepository.findById(userId).orElseThrow((() -> new UserNotExistObject("Юзера с айди " + userId + " не существует")));

        ItemRequest itemRequest = ItemRequestMapper.toItemRequestForItemRequestDtoIn(itemRequestDtoIn, user);

        itemRequest = itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getAllRequestsForOwner(long userId) {
        userRepository.findById(userId).orElseThrow((() -> new UserNotExistObject("Юзера с айди " + userId + " не существует")));
        List<ItemRequestDto> itemsRequestDto = itemRequestRepository.findByRequestor_Id(userId, sort).stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        List<ItemDto> itemsDto = itemRepository.findAll().stream()
                .map(i -> ItemDtoMapper.toItemDto(i, commentRepository.findAllByItem(i)))
                .collect(Collectors.toList());

        updateForItemsInItemRequest(itemsRequestDto, itemsDto);
        return itemsRequestDto;
    }

    @Override
    public List<ItemRequestDto> getAllRequestsForOtherUser(long userId, Integer from, Integer size) throws ItemRequestNotExist {
        userRepository.findById(userId).orElseThrow((() -> new UserNotExistObject("Юзера с айди " + userId + " не существует")));
        Pageable page = PageRequest.of(from, size, sort);
        Page<ItemRequest> itemRequestPage = itemRequestRepository.findByRequestor_IdNot(userId, page);

        List<ItemRequestDto> itemRequestDtos = itemRequestPage.getContent().stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());

        List<ItemDto> itemsDto = itemRepository.findAll().stream()
                .map(i -> ItemDtoMapper.toItemDto(i, commentRepository.findAllByItem(i)))
                .collect(Collectors.toList());

        updateForItemsInItemRequest(itemRequestDtos, itemsDto);

        return itemRequestDtos;
    }

    @Override
    public ItemRequestDto getRequestById(long userId, long requestId) {
        userRepository.findById(userId).orElseThrow((() -> new UserNotExistObject("Юзера с айди " + userId + " не существует")));
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequestRepository.findById(requestId).orElseThrow((() -> new ItemRequestNotExist("Запрос с айди " + requestId + " не существует"))));

        List<ItemDto> itemsDto = itemRepository.findAll().stream()
                .map(i -> ItemDtoMapper.toItemDto(i, commentRepository.findAllByItem(i)))
                .collect(Collectors.toList());

        for (ItemDto i : itemsDto) {
            if (i.getRequestId() != null) {
                if (i.getRequestId().equals(itemRequestDto.getId())) {
                    itemRequestDto.getItems().add(ItemDtoMapper.toItemDtoFroRequest(i));
                }
            }
        }
        return itemRequestDto;
    }

    private void updateForItemsInItemRequest(List<ItemRequestDto> itemsRequestDto, List<ItemDto> itemsDto) {
        for (ItemRequestDto ir : itemsRequestDto) {
            for (ItemDto i : itemsDto) {
                if (i.getRequestId() != null) {
                    if (i.getRequestId().equals(ir.getId())) {
                        ir.getItems().add(ItemDtoMapper.toItemDtoFroRequest(i));
                    }
                }
            }
        }
    }

}
