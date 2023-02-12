package com.example.board.service;


import com.example.board.dto.BoardRequest;
import com.example.board.dto.BoardResponse;
import com.example.board.dto.MessageResponse;
import com.example.board.entity.Board;
import com.example.board.entity.User;
import com.example.board.jwt.JwtUtil;
import com.example.board.repository.BoardRepository;
import com.example.board.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Transactional(readOnly = true)
    public ResponseEntity<List<BoardResponse>> getBoards() {
        List<Board> boards = boardRepository.findAllByOrderByModifiedAtDesc();
        List<BoardResponse> boardResponseList = new ArrayList<>();
        for (Board board : boards) {
            BoardResponse boardResponse = new BoardResponse(board);
            boardResponseList.add(boardResponse);
        }
        return ResponseEntity.ok(boardResponseList);
    }

    @Transactional
    public ResponseEntity<Object> createBoard(BoardRequest boardRequest, HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request);
        Claims claims;
        if (token != null) {
            if (jwtUtil.validateToken(token)) {
                claims = jwtUtil.getUserInfoFromToken(token); // 토큰에서 사용자 정보 가져오기
            } else {
                throw new IllegalArgumentException("Token Error");
            }

            Optional<User> user = userRepository.findByUsername(claims.getSubject());
            if (user.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(MessageResponse.builder()
                                .intStatus(HttpStatus.OK.value())
                                .strStatus(HttpStatus.OK.toString())
                                .message("사용자가 존재하지 않습니다.")
                                .build());
            }

            return ResponseEntity.ok(new BoardResponse(boardRepository.save(
                    Board.builder()
                            .request(boardRequest)
                            .user(user.get())
                            .build())));
        }
        return ResponseEntity.badRequest().body(MessageResponse
                .builder()
                        .intStatus(HttpStatus.OK.value())
                        .strStatus(HttpStatus.OK.toString())
                        .message("토큰이 없습니다.")
                .build());
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Object> getBoards(Long id) {
        Optional<Board> board = boardRepository.findById(id);
        if (board.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(MessageResponse.builder()
                            .intStatus(HttpStatus.OK.value())
                            .strStatus(HttpStatus.OK.toString())
                            .message("게시글이 존재하지 않습니다.")
                            .build());
        }
        return ResponseEntity.ok(BoardResponse.builder()
                        .board(board.get())
                        .build());
    }

    @Transactional
    public ResponseEntity<Object> update(Long id, BoardRequest requestDto, HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request);
        Claims claims;
        if (token != null) {
            if (jwtUtil.validateToken(token)) {
                claims = jwtUtil.getUserInfoFromToken(token); // 토큰에서 사용자 정보 가져오기
            } else {
                throw new IllegalArgumentException("Token Error");
            }

            Optional<User> user = userRepository.findByUsername(claims.getSubject());
            if (user.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(MessageResponse.builder()
                                .intStatus(HttpStatus.OK.value())
                                .strStatus(HttpStatus.OK.toString())
                                .message("사용자가 존재하지 않습니다.")
                                .build());
            }

            Optional<Board> board = boardRepository.findById(id);
            if (board.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(MessageResponse.builder()
                                .intStatus(HttpStatus.OK.value())
                                .strStatus(HttpStatus.OK.toString())
                                .message("자신이 작성한 게시글만 수정이 가능합니다.")
                                .build());
            }

            board.get().update(requestDto, user.get());
            return ResponseEntity.ok(BoardResponse.builder()
                    .board(board.get())
                    .build());
        }
        return ResponseEntity.badRequest()
                .body(MessageResponse.builder()
                        .message("토큰이 없습니다.")
                        .build());
    }

    @Transactional
    public ResponseEntity<MessageResponse> delete(Long id, HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request);
        Claims claims;
        if (token != null) {
            if (jwtUtil.validateToken(token)) {
                claims = jwtUtil.getUserInfoFromToken(token); // 토큰에서 사용자 정보 가져오기
            } else {
                throw new IllegalArgumentException("Token Error");
            }

            userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new IllegalArgumentException("사용자가 존재하지 않습니다.")
            );

            boardRepository.findById(id).orElseThrow(
                    () -> new IllegalArgumentException("아이디가 존재하지 않습니다.")
            );

            boardRepository.deleteById(id);
            return ResponseEntity.ok()
                    .body(MessageResponse.builder()
                        .message("삭제 완료")
                        .intStatus(HttpStatus.OK.value())
                        .strStatus(HttpStatus.OK.toString())
                        .build());
        } else {
            return ResponseEntity.badRequest()
                    .body(MessageResponse.builder()
                        .message("삭제 실패")
                        .intStatus(HttpStatus.OK.value())
                        .strStatus(HttpStatus.OK.toString())
                        .build());
        }
    }

}
