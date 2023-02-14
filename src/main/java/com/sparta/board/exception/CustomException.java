package com.sparta.board.exception;

import com.sparta.board.dto.response.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomException {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MessageResponse> methodValidException(MethodArgumentNotValidException e) {
        MessageResponse messageResponse = ErrorResponse(e.getMessage());
        return ResponseEntity.badRequest().body(messageResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<MessageResponse> commonException(RuntimeException e) {
        MessageResponse messageResponse = ErrorResponse(e.getMessage());
        return ResponseEntity.badRequest().body(messageResponse);
    }

    private MessageResponse ErrorResponse(BindingResult bindingResult) {
        String message = "";

        if (bindingResult.hasErrors()) {
            message = bindingResult.getAllErrors().get(0).getDefaultMessage();
        }

        return MessageResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(message)
                .build();
    }

    private MessageResponse ErrorResponse(String message) {
        return MessageResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(message)
                .build();
    }
}
