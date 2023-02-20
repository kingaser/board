package com.sparta.board.dto.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.sparta.board.entity.Board;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter // jaxson에서 reponse의 getter 를 사용
public class BoardResponse {

    private Long id;
    private String title;
    private String username;
    private String contents;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime modifiedAt;
    private List<CommentResponse> commentList;

    @Builder
    public BoardResponse(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.username = board.getUser().getUsername();
        this.contents = board.getContents();
        this.createdAt = board.getCreatedAt();
        this.modifiedAt = board.getModifiedAt();
        this.commentList = board.getCommentList().stream().map(CommentResponse::new).collect(Collectors.toList());
    }
}
