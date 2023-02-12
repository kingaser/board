package com.example.board.controller;

import com.example.board.dto.BoardRequest;
import com.example.board.dto.BoardResponse;
import com.example.board.dto.MessageResponse;
import com.example.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/boards")
    public ResponseEntity<List<BoardResponse>> getBoards() {
        return boardService.getBoards();
    }

    @PostMapping("/board/post")
    public ResponseEntity<Object> createBoard(@RequestBody BoardRequest boardRequest, HttpServletRequest request) {
        return boardService.createBoard(boardRequest, request);
    }

    @GetMapping("/boards/{id}")
    public ResponseEntity<Object> getBoards(@PathVariable Long id) {
        return boardService.getBoards(id);
    }

    @PutMapping("/board/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id, @RequestBody BoardRequest boardRequest, HttpServletRequest request) {
        return boardService.update(id, boardRequest, request);
    }

    @DeleteMapping("/board/{id}")
    public ResponseEntity<MessageResponse> delete(@PathVariable Long id, HttpServletRequest request) {
        return boardService.delete(id, request);
    }
}
