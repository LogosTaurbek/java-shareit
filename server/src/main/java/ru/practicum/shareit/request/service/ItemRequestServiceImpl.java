package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemForRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto addItemRequest(int requesterId, NewItemRequestDto newItemRequestDto) {
        log.info("ItemRequestServiceImpl:addItemRequest(): запрос на создание нового запроса вещи {}", newItemRequestDto);
        ItemRequest newItemRequest = ItemRequestMapper.newItemRequestDtoToItemRequest(newItemRequestDto);
        newItemRequest.setRequesterId(requesterId);
        newItemRequest.setCreated(LocalDateTime.now());
        ItemRequest createdItemRequest = itemRequestRepository.save(newItemRequest);
        log.info("ItemRequestServiceImpl:addItemRequest(): создан новый запрос вещи {}", createdItemRequest);
        return ItemRequestMapper.itemRequestToItemRequestDto(createdItemRequest);
    }

    @Override
    public List<ItemRequestDto> getRequestsOfUser(int requesterId) {
        log.info("ItemRequestServiceImpl:getRequestsOfUser(): запрос на получение списка запросов пользователя с id={}", requesterId);
        List<ItemRequest> requests = itemRequestRepository.findByRequesterIdOrderByCreatedDesc(requesterId);
        List<ItemRequestDto> requestDtos = enrichRequestsWithItems(requests);
        log.info("ItemRequestServiceImpl:getRequestsOfUser(): получено {} запросов", requestDtos.size());
        return requestDtos;
    }

    @Override
    public List<ItemRequestDto> getAllRequests(int requesterId) {
        log.info("ItemRequestServiceImpl:getAllRequests(): запрос на получение всех запросов других пользователей");
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(requesterId);
        List<ItemRequestDto> requestDtos = enrichRequestsWithItems(requests);
        log.info("ItemRequestServiceImpl:getAllRequests(): получено {} запросов", requestDtos.size());
        return requestDtos;
    }

    @Override
    public ItemRequestDto getRequestById(int requestId) {
        log.info("ItemRequestServiceImpl:getRequestById(): запрос на получение запроса с id={}", requestId);
        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new java.util.NoSuchElementException("Запрос с ID " + requestId + " не существует"));
        ItemRequestDto dto = convertToDtoWithItems(request);
        log.info("ItemRequestServiceImpl:getRequestById(): получен запрос с id={}", requestId);
        return dto;
    }

    private List<ItemRequestDto> enrichRequestsWithItems(List<ItemRequest> requests) {
        if (requests.isEmpty()) {
            return Collections.emptyList();
        }
        List<Integer> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .toList();
        List<Item> items = itemRepository.findAllByRequestIdIn(requestIds);
        Map<Integer, List<ItemForRequestDto>> itemsByRequestId = items.stream()
                .collect(Collectors.groupingBy(
                        Item::getRequestId,
                        Collectors.mapping(this::convertItemToItemForRequestDto, Collectors.toList())
                ));
        return requests.stream()
                .map(request -> {
                    ItemRequestDto dto = ItemRequestMapper.itemRequestToItemRequestDto(request);
                    dto.setItems(itemsByRequestId.getOrDefault(request.getId(), Collections.emptyList()));
                    return dto;
                })
                .toList();
    }

    private ItemRequestDto convertToDtoWithItems(ItemRequest request) {
        ItemRequestDto dto = ItemRequestMapper.itemRequestToItemRequestDto(request);
        List<Item> items = itemRepository.findAllByRequestId(request.getId());
        List<ItemForRequestDto> itemDtos = items.stream()
                .map(this::convertItemToItemForRequestDto)
                .toList();
        dto.setItems(itemDtos);
        return dto;
    }

    private ItemForRequestDto convertItemToItemForRequestDto(Item item) {
        ItemForRequestDto dto = new ItemForRequestDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setOwner(item.getOwner().getId());
        return dto;
    }
}