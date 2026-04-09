package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    Item addItem(Item newItem);

    Optional<Item> getItemById(int itemId);

    Item updateItem(Item updatedItem);

    List<Item> getAllItemsFromUser(int sharerUserId);

    List<Item> searchAvailableItems(String searchString);
}
