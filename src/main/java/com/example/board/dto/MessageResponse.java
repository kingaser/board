package com.example.board.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
public class MessageResponse {

    private int intStatus = HttpStatus.OK.value();
    private String strStatus = HttpStatus.OK.toString();
    private String message;

    @Builder
    public MessageResponse(int intStatus, String strStatus, String message) {
        this.intStatus = intStatus;
        this.strStatus = strStatus;
        this.message = message;
    }
}
