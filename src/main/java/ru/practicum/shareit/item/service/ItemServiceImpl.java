package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ItemDoesNotBelongToUserException;
import ru.practicum.shareit.exceptions.ItemNotValidException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequestDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    public ItemServiceImpl(
            @Qualifier("ItemStorageImpl") ItemStorage itemStorage,
            @Qualifier("UserStorageImpl") UserStorage userStorage
    ) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }


    @Override
    public ItemDto addItem(int sharerUserId, NewItemRequestDto newItemRequestDto) {
        log.info(
                "ItemServiceImpl:addItem(): запрос на создание нового предмета {} от пользователя с id={}",
                newItemRequestDto,
                sharerUserId
        );
        validateNewItemRequestDto(newItemRequestDto);
        Item newItem = ItemMapper.newItemRequestDtoToItem(newItemRequestDto);
        if (userStorage.getUserById(sharerUserId) != null) {
            newItem.setOwner(sharerUserId);
        }
        Item createdItem = itemStorage.addItem(newItem);
        log.info("ItemServiceImpl:addItem(): создан новый предмет {}", createdItem);
        return ItemMapper.itemToItemDto(createdItem);
    }

    @Override
    public void validateNewItemRequestDto(NewItemRequestDto newItemRequestDto) {
        if (newItemRequestDto.getName() == null || newItemRequestDto.getName().isBlank()) {
            throw new ItemNotValidException("Имя вещи не может быть пустым или null");
        }
        if (newItemRequestDto.getDescription() == null || newItemRequestDto.getDescription().isBlank()) {
            throw new ItemNotValidException("Описание вещи не может быть пустым или null");
        }
        if (newItemRequestDto.getAvailable() == null) {
            throw new ItemNotValidException("У вещи отсутствует информация о доступности для аренды");
        }
    }

    @Override
    public ItemDto updateItem(int sharerUserId, int itemId, UpdateItemRequestDto updateItemRequestDto) {
        log.info(
                "ItemServiceImpl:updateItem(): запрос на обновление предмета id={} от пользователя id={}, новые данные {}",
                itemId,
                sharerUserId,
                updateItemRequestDto
        );
        checkIfItemBelongsToUser(itemId, sharerUserId);
        Item itemToUpdate = itemStorage.getItemById(itemId);
        Item updatedItem = ItemMapper.updateItemFields(itemToUpdate, updateItemRequestDto);
        updatedItem = itemStorage.updateItem(updatedItem);
        log.info("ItemServiceImpl:updateItem(): предмет id={} отредактирован, новые данные: {}", itemId, updatedItem);
        return ItemMapper.itemToItemDto(updatedItem);
    }

    @Override
    public ItemDto getItemById(int itemId) {
        log.info("ItemServiceImpl:getItemById(): запрос на получение предмета с id {}", itemId);
        return ItemMapper.itemToItemDto(itemStorage.getItemById(itemId));
    }

    @Override
    public List<ItemDto> getAllItemsFromUser(int sharerUserId) {
        log.info("ItemServiceImpl:getAllItemsFromUser(): запрос на получение всех предметов пользователя с id {}", sharerUserId);
        List<Item> itemsOfUser = itemStorage.getAllItemsFromUser(sharerUserId);
        return itemsOfUser.stream()
                .map(ItemMapper::itemToItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> searchAvailableItems(String searchString) {
        log.info("ItemServiceImpl:searchAvailableItems(): запрос на поиск доступных предметов по запросу {}", searchString);
        List<Item> itemSearchResults = itemStorage.searchAvailableItems(searchString);
        return itemSearchResults.stream()
                .map(ItemMapper::itemToItemDto)
                .toList();
    }

    private void checkIfItemBelongsToUser(int itemId, int userId) {
        int ownerId = itemStorage.getItemById(itemId).getOwner();
        if (userId != ownerId) {
            throw new ItemDoesNotBelongToUserException(
                    "Предмет ID=%s не принадлежит пользователю ID=%s".formatted(itemId, userId)
            );
        }
    }
}
