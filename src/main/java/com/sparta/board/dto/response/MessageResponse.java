package com.sparta.board.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MessageResponse {

    private int status;
    private String message;

    @Builder
    public MessageResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
