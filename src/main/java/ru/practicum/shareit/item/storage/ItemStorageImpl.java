package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Component("ItemStorageImpl")
@Slf4j
public class ItemStorageImpl implements ItemStorage {

    private final Map<Integer, Item> itemMap = new HashMap<>();
    private int currentId = 0;

    @Override
    public Item addItem(Item newItem) {
        log.info("ItemStorageImpl:addItem(): запрос на создание нового предмета {}", newItem);
        int id = getNextId();
        newItem.setId(id);
        itemMap.put(id, newItem);
        log.info("ItemStorageImpl:addItem(): создан новый предмет {}", newItem);
        return newItem;
    }

    @Override
    public Item getItemById(int itemId) {
        log.info("ItemStorageImpl:getItemById(): запрос на получение предмета с id {}", itemId);
        if (!itemMap.containsKey(itemId)) {
            throw new NoSuchElementException("Предмета с ID " + itemId + " не существует");
        }
        log.info("ItemStorageImpl:getItemById(): Предмет с id {} найден", itemId);
        return itemMap.get(itemId);
    }

    @Override
    public Item updateItem(Item updatedItem) {
        log.info("ItemStorageImpl:updateItem(): запрос на обновление предмета {}", updatedItem);
        itemMap.put(updatedItem.getId(), updatedItem);
        log.info("ItemStorageImpl:updateItem(): предмет {} обновлен", updatedItem);
        return updatedItem;
    }

    @Override
    public List<Item> getAllItemsFromUser(int sharerUserId) {
        log.info("ItemStorageImpl:getAllItemsFromUser(): запрос на получение всех предметов пользователя с id {}", sharerUserId);
        return itemMap.values().stream()
                .filter(item -> item.getOwner() == sharerUserId)
                .toList();
    }

    @Override
    public List<Item> searchAvailableItems(String searchString) {
        log.info("ItemStorageImpl:searchAvailableItems(): запрос на поиск доступных предметов по запросу {}", searchString);
        if (searchString == null || searchString.isEmpty()) {
            return new ArrayList<>();
        }
        return itemMap.values().stream()
                .filter(Item::isAvailable)
                .filter(item -> {
                    return item.getName().toLowerCase().contains(searchString.toLowerCase())
                            || item.getDescription().toLowerCase().contains(searchString.toLowerCase());
                })
                .toList();
    }

    private int getNextId() {
        return currentId++;
    }
}
