package com.example.board.controller;

import com.example.board.dto.LoginRequest;
import com.example.board.dto.MessageResponse;
import com.example.board.dto.SignupRequest;
import com.example.board.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class UserController {

    private final UserService userService;

    @GetMapping("/signup")
    public String signup() {
        return "회원 가입 페이지";
    }

    @GetMapping("/login")
    public String login() {
        return "로그인 페이지";
    }

    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> signup(@Valid @RequestBody SignupRequest signupRequest, BindingResult bindingResult) {
        return userService.signup(signupRequest, bindingResult);
    }

    @PostMapping("/login")
    public ResponseEntity<MessageResponse> login(@RequestBody LoginRequest loginRequest) {
        return  userService.login(loginRequest);
    }
}
