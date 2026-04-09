package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.NewCommentRequestDto;
import ru.practicum.shareit.item.dto.NewItemRequestDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    ItemClient(@Nullable RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> addItem(long userId, NewItemRequestDto newItemRequestDto) {
        return post("", userId, newItemRequestDto);
    }

    public ResponseEntity<Object> updateItem(long userId, int itemId, UpdateItemRequestDto updateItemRequestDto) {
        return patch("/" + itemId, userId, updateItemRequestDto);
    }

    public ResponseEntity<Object> getItemById(long userId, int itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getAllItemsFromUser(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> searchAvailableItems(long userId, String text) {
        return get("/search?text=" + text, userId);
    }

    public ResponseEntity<Object> addComment(long userId, int itemId, NewCommentRequestDto newCommentRequestDto) {
        return post("/" + itemId + "/comment", userId, newCommentRequestDto);
    }
}
