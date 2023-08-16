package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.service.CheckConsistencyService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.util.Pagination;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final CommentRepository commentRepository;
    private final CheckConsistencyService checker;
    private final ItemMapper mapper;
    private static final String ITEM_NOT_FOUND = "Вещь с ID = %d не найдена!";

    @Autowired
    @Lazy
    public ItemServiceImpl(ItemRepository repository, CommentRepository commentRepository,
                           CheckConsistencyService checkConsistencyService, ItemMapper itemMapper) {
        this.repository = repository;
        this.commentRepository = commentRepository;
        this.checker = checkConsistencyService;
        this.mapper = itemMapper;
    }

    @Override
    public ItemDto getItemById(Long id, Long userId) {
        ItemDto itemDto;
        Item item = repository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(String.format(ITEM_NOT_FOUND, id)));
        List<CommentDto> comments = checker.getCommentsByItemId(item.getId());
        BookingShortDto lastBooking = checker.getLastBooking(item.getId());
        BookingShortDto nextBooking = checker.getNextBooking(item.getId());
        if (userId.equals(item.getOwner().getId())) {
            itemDto = mapper.toItemExtDto(item, lastBooking, nextBooking, comments);
        } else {
            itemDto = mapper.toItemDto(item, comments);
        }
        return itemDto;
    }

    @Override
    public Item findItemById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(String.format(ITEM_NOT_FOUND, id)));
    }

    @Override
    public ItemDto create(ItemDto itemDto, Long ownerId) {
        checker.isExistUser(ownerId);
        User user = checker.findUserById(ownerId);
        List<CommentDto> comments = checker.getCommentsByItemId(itemDto.getId());
        return mapper.toItemDto(repository.save(mapper.toItem(itemDto, user)), comments);
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long ownerId, Integer from, Integer size) {
        checker.isExistUser(ownerId);
        List<ItemDto> listItemExtDto = new ArrayList<>();
        Pageable pageable;
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Page<Item> page;
        Pagination pager = new Pagination(from, size);

        if (size == null) {
            pageable =
                    PageRequest.of(pager.getIndex(), pager.getPageSize(), sort);
            do {
                page = repository.findByOwnerId(ownerId, pageable);
                listItemExtDto.addAll(page.stream().map(item -> mapper.toItemExtDto(item,
                        checker.getLastBooking(item.getId()),
                        checker.getNextBooking(item.getId()),
                        checker.getCommentsByItemId(item.getId()))).collect(toList()));
                pageable = pageable.next();
            } while (page.hasNext());

        } else {
            for (int i = pager.getIndex(); i < pager.getTotalPages(); i++) {
                pageable =
                        PageRequest.of(i, pager.getPageSize(), sort);
                page = repository.findByOwnerId(ownerId, pageable);
                listItemExtDto.addAll(page.stream().map(item -> mapper.toItemExtDto(item,
                        checker.getLastBooking(item.getId()),
                        checker.getNextBooking(item.getId()),
                        checker.getCommentsByItemId(item.getId()))).collect(toList()));
                if (!page.hasNext()) {
                    break;
                }
            }
            listItemExtDto = listItemExtDto.stream().limit(size).collect(toList());
        }
        return listItemExtDto;
    }

    @Override
    public void delete(Long itemId, Long ownerId) {
        try {
            Item item = repository.findById(itemId)
                    .orElseThrow(() -> new ItemNotFoundException(String.format(ITEM_NOT_FOUND, itemId)));
            if (!item.getOwner().getId().equals(ownerId)) {
                throw new ItemNotFoundException("У пользователя нет такой вещи!");
            }
            repository.deleteById(itemId);
        } catch (EmptyResultDataAccessException e) {
            throw new ItemNotFoundException(String.format(ITEM_NOT_FOUND, itemId));
        }
    }

    @Override
    public List<ItemDto> getItemsBySearchQuery(String text, Integer from, Integer size) {
        List<ItemDto> listItemDto = new ArrayList<>();
        if ((text != null) && (!text.isEmpty()) && (!text.isBlank())) {
            text = text.toLowerCase();
            Pageable pageable;
            Sort sort = Sort.by(Sort.Direction.ASC, "name");
            Page<Item> page;
            Pagination pager = new Pagination(from, size);

            if (size == null) {
                pageable =
                        PageRequest.of(pager.getIndex(), pager.getPageSize(), sort);
                do {
                    page = repository.getItemsBySearchQuery(text, pageable);
                    listItemDto.addAll(page.stream().map(item -> mapper.toItemDto(item, checker.getCommentsByItemId(item.getId()))).collect(toList()));
                    pageable = pageable.next();
                } while (page.hasNext());

            } else {
                for (int i = pager.getIndex(); i < pager.getTotalPages(); i++) {
                    pageable =
                            PageRequest.of(i, pager.getPageSize(), sort);
                    page = repository.getItemsBySearchQuery(text, pageable);
                    listItemDto.addAll(page.stream().map(item -> mapper.toItemDto(item, checker.getCommentsByItemId(item.getId()))).collect(toList()));
                    if (!page.hasNext()) {
                        break;
                    }
                }
                listItemDto = listItemDto.stream().limit(size).collect(toList());
            }
        }
        return listItemDto;
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long ownerId, Long itemId) {
        checker.isExistUser(ownerId);
        Item item = repository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format(ITEM_NOT_FOUND, itemId)));
        List<CommentDto> comments = checker.getCommentsByItemId(item.getId());
        if (!item.getOwner().getId().equals(ownerId)) {
            throw new ItemNotFoundException("У пользователя нет такой вещи!");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return mapper.toItemDto(repository.save(item), comments);
    }

    @Override
    public CommentDto createComment(CommentDto commentDto, Long itemId, Long userId) {
        checker.isExistUser(userId);
        Comment comment = new Comment();
        Booking booking = checker.getBookingWithUserBookedItem(itemId, userId);
        if (booking != null) {
            comment.setCreated(LocalDateTime.now());
            comment.setItem(booking.getItem());
            comment.setAuthor(booking.getBooker());
            comment.setText(commentDto.getText());
        } else {
            throw new ValidationException("Данный пользователь вещь не бронировал!");
        }
        return mapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public List<CommentDto> getCommentsByItemId(Long itemId) {
        return commentRepository.findAllByItem_Id(itemId,
                        Sort.by(Sort.Direction.DESC, "created")).stream()
                .map(mapper::toCommentDto)
                .collect(toList());
    }

    @Override
    public List<ItemDto> getItemsByRequestId(Long requestId) {
        return repository.findAllByRequestId(requestId,
                        Sort.by(Sort.Direction.DESC, "id")).stream()
                .map(item -> mapper.toItemDto(item, checker.getCommentsByItemId(item.getId())))
                .collect(toList());
    }
}