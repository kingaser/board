package com.sparta.board.exception;

import com.sparta.board.dto.response.MessageResponse;
import com.sparta.board.entity.enumtype.ExceptionType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ExceptionHandling {

    public static ResponseEntity<Object> responseException(ExceptionType exceptionType) {
        MessageResponse messageResponse = MessageResponse.builder()
                .status(exceptionType.getStatusCode())
                .message(exceptionType.getMessage())
                .build();
        return ResponseEntity.badRequest().body(messageResponse);
    }

    public static ResponseEntity<Object> responseException(HttpStatus status, String message) {
        MessageResponse messageResponse = MessageResponse.builder()
                .status(status.value())
                .message(message)
                .build();
        return ResponseEntity.badRequest().body(messageResponse);
    }
}
