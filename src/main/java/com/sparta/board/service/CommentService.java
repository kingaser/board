package com.sparta.board.service;

import com.sparta.board.dto.request.CommentRequest;
import com.sparta.board.dto.response.CommentResponse;
import com.sparta.board.dto.response.MessageResponse;
import com.sparta.board.entity.Board;
import com.sparta.board.entity.Comment;
import com.sparta.board.entity.User;
import com.sparta.board.entity.enumtype.ExceptionType;
import com.sparta.board.jwt.JwtUtil;
import com.sparta.board.repository.BoardRepository;
import com.sparta.board.repository.CommentRepository;
import com.sparta.board.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public ResponseEntity<Object> createComment(Long id, CommentRequest commentRequest, HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request);
        Claims claims;
        if (token == null || !(jwtUtil.validateToken(token))) {
            throw new RuntimeException(ExceptionType.NOT_VALID_TOKEN.getMessage());
        }

        claims = jwtUtil.getUserInfoFromToken(token);
        Optional<User> user = userRepository.findByUsername(claims.getSubject());
        if (user.isEmpty()) {
            throw new RuntimeException(ExceptionType.NOT_FOUND_USER.getMessage());
        }

        Optional<Board> board = boardRepository.findById(id);
        if (board.isEmpty()) {
            throw new RuntimeException(ExceptionType.NOT_FOUND_WRITING.getMessage());
        }
        Comment comment = commentRepository.save(Comment.builder()
                .commentRequest(commentRequest)
                .board(board.get())
                .user(user.get())
                .build());

        return ResponseEntity.ok(CommentResponse.builder().comment(comment).build());
    }

    @Transactional
    public ResponseEntity<Object> updateComment(Long id, CommentRequest commentRequest, HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request);
        Claims claims;
        if (token == null || !(jwtUtil.validateToken(token))) {
            throw new RuntimeException(ExceptionType.NOT_VALID_TOKEN.getMessage());
        }

        claims = jwtUtil.getUserInfoFromToken(token);
        Optional<User> user = userRepository.findByUsername(claims.getSubject());
        if (user.isEmpty()) {
            throw new RuntimeException(ExceptionType.NOT_FOUND_USER.getMessage());
        }

        Optional<Comment> comment = commentRepository.findById(id);
        if (comment.isEmpty()) {
            throw new RuntimeException(ExceptionType.NOT_FOUND_WRITING.getMessage());
        }

        Optional<Comment> found = commentRepository.findByUser(user.get());
        if (found.isEmpty()) {
            throw new RuntimeException(ExceptionType.NOT_AUTHOR.getMessage());
        }

        comment.get().update(commentRequest, user.get());
        commentRepository.saveAndFlush(comment.get());
        return ResponseEntity.ok(CommentResponse.builder()
                .comment(comment.get())
                .build()
        );

    }

    @Transactional
    public ResponseEntity<Object> deleteComment(Long id, HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request);
        Claims claims;
        if (token == null || !(jwtUtil.validateToken(token))) {
            throw new RuntimeException(ExceptionType.NOT_VALID_TOKEN.getMessage());
        }

        claims = jwtUtil.getUserInfoFromToken(token);
        Optional<User> user = userRepository.findByUsername(claims.getSubject());
        if (user.isEmpty()) {
            throw new RuntimeException(ExceptionType.NOT_FOUND_USER.getMessage());
        }

        Optional<Comment> comment = commentRepository.findById(id);
        if (comment.isEmpty()) {
            throw new RuntimeException(ExceptionType.NOT_FOUND_WRITING.getMessage());
        }

        commentRepository.deleteById(id);

        return ResponseEntity.ok(MessageResponse.builder()
                .status(HttpStatus.OK.value())
                .message("댓글 삭제 완료")
                .build()
        );
    }
}
