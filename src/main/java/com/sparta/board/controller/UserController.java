package com.sparta.board.controller;

import com.sparta.board.dto.request.LoginRequest;
import com.sparta.board.dto.response.MessageResponse;
import com.sparta.board.dto.request.SignupRequest;
import com.sparta.board.service.UserService;
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
    public ResponseEntity<Object> signup(@Valid @RequestBody SignupRequest signupRequest, BindingResult bindingResult) {
        return userService.signup(signupRequest, bindingResult);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        return userService.login(loginRequest, response);
    }


}
