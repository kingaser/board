package com.example.board.entity;

import com.example.board.dto.BoardRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Board extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

//    @Column(nullable = false)
//    private String username;

    @Column(nullable = false)
    private String contents;


    @ManyToOne
    @JoinColumn(name = "User_Id", nullable = false)
    private User user;

    @Builder
    public Board(BoardRequest request, User user) {
        this.title = request.getTitle();
//        this.username = request.getUsername();
        this.contents = request.getContents();
//        this.password = request.getPassword();
        this.user = user;
    }

    public void update(BoardRequest requestDto, User user) {
        this.title = requestDto.getTitle();
        this.contents = requestDto.getContents();
        this.user = user;
    }
}
