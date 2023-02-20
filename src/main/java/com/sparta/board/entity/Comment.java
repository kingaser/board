package com.sparta.board.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sparta.board.dto.request.CommentRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Comment extends TimeStamped{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String comments;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    @Builder
    public Comment(CommentRequest commentRequest, Board board, User user) {
        comments = commentRequest.getComments();
        this.board = board;
        this.user = user;
    }

    public void update(CommentRequest commentRequest, User user) {
        comments = commentRequest.getComments();
        this.user = user;
    }
}
