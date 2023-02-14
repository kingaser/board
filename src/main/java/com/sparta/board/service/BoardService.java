package com.sparta.board.service;


import com.sparta.board.dto.request.BoardRequest;
import com.sparta.board.dto.response.BoardResponse;
import com.sparta.board.dto.response.MessageResponse;
import com.sparta.board.entity.Board;
import com.sparta.board.entity.Comment;
import com.sparta.board.entity.User;
import com.sparta.board.entity.enumtype.ExceptionType;
import com.sparta.board.entity.enumtype.UserRoleEnum;
import com.sparta.board.jwt.JwtUtil;
import com.sparta.board.repository.BoardRepository;
import com.sparta.board.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Comparator;
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
        if (token == null || !(jwtUtil.validateToken(token))) {// 토큰이 없거나 유요하지지 않으면 게시글 작성 불가
            throw new RuntimeException(ExceptionType.NOT_VALID_TOKEN.getMessage());
        }

        claims = jwtUtil.getUserInfoFromToken(token);
        Optional<User> user = userRepository.findByUsername(claims.getSubject());
        if (user.isEmpty()) {// 토큰에서 가져온 사용자 정보가 DB에 없는 경우
            throw new RuntimeException(ExceptionType.NOT_FOUND_USER.getMessage());
        }

        Board board = boardRepository.save(Board.builder()
                        .request(boardRequest)
                        .user(user.get())
                        .build());

        return ResponseEntity.ok(new BoardResponse(board));
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Object> getBoards(Long id) {
        Optional<Board> board = boardRepository.findById(id);
        if (board.isEmpty()) {
            throw new RuntimeException(ExceptionType.NOT_FOUND_WRITING.getMessage());
        }
        // 댓글리스트 작성일자 기준 내림차순 정렬
        board.get().getCommentList().sort(Comparator.comparing(Comment::getModifiedAt).reversed());

        // 해당 게시글이 있다면 게시글 객체를 Dto 로 변환  후 ResponseEntity body 에 담아서 리턴한다
        return ResponseEntity.ok(new BoardResponse(board.get()));
    }

    @Transactional
    public ResponseEntity<Object> update(Long id, BoardRequest requestDto, HttpServletRequest request) {
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

        Optional<Board> found = boardRepository.findByIdAndUser(id, user.get());
        if (found.isEmpty() && user.get().getRole() == UserRoleEnum.USER) {
            throw new RuntimeException(ExceptionType.NOT_AUTHOR.getMessage());
        }

        board.get().update(requestDto, user.get());
        boardRepository.saveAndFlush(board.get()); // modifiedAt 업데이트를 위해 saveAndFlush 사용
        return ResponseEntity.ok(BoardResponse.builder()
                .board(board.get())
                .build());
    }

    @Transactional
    public ResponseEntity<Object> delete(Long id, HttpServletRequest request) {
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

        Optional<Board> found = boardRepository.findByIdAndUser(id, user.get());
        if (found.isEmpty() && user.get().getRole() == UserRoleEnum.USER) {
            throw new RuntimeException(ExceptionType.NOT_AUTHOR.getMessage());
        }

        Optional<Board> board = boardRepository.findById(id);
        if (board.isEmpty()) {
            throw new RuntimeException(ExceptionType.NOT_FOUND_WRITING.getMessage());
        }

        boardRepository.deleteById(id);
        return ResponseEntity.ok().body(MessageResponse.builder()
                    .status(HttpStatus.OK.value())
                    .message("삭제 완료")
                    .build()
        );

    }
}
