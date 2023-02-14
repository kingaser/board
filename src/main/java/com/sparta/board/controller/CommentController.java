package com.sparta.board.controller;

import com.sparta.board.dto.request.CommentRequest;
import com.sparta.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/board/comment/{id}")
    public ResponseEntity<Object> createComment(@PathVariable Long id, @RequestBody CommentRequest commentRequest, HttpServletRequest request) {
        return commentService.createComment(id, commentRequest, request);
    }

    @PutMapping("/board/comment/{id}")
    public ResponseEntity<Object> updateComment(@PathVariable Long id, @RequestBody CommentRequest commentRequest, HttpServletRequest request) {
        return commentService.updateComment(id, commentRequest, request);
    }

    @DeleteMapping("/board/comment/{id}")
    public ResponseEntity<Object> deleteComment(@PathVariable Long id, HttpServletRequest request) {
        return commentService.deleteComment(id, request);
    }

}
