package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item addItem(Item newItem);

    Item getItemById(int itemId);

    Item updateItem(Item updatedItem);

    List<Item> getAllItemsFromUser(int sharerUserId);

    List<Item> searchAvailableItems(String searchString);
}
