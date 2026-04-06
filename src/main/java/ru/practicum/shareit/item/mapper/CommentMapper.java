package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.NewCommentRequestDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class CommentMapper {
    public static CommentDto commentToCommentDto(Comment comment, String authorName) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setItem(comment.getItem().getId());
        commentDto.setAuthorName(authorName);
        commentDto.setCreated(comment.getCreated());
        return commentDto;
    }

    public static Comment newCommentRequestDtoToComment(NewCommentRequestDto dto, Item item, User author) {
        Comment comment = new Comment();
        String text = dto.getText().length() > 1000 ? dto.getText().substring(0, 1000) : dto.getText();
        comment.setText(text);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        return comment;
    }
}
