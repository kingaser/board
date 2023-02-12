package com.example.board.service;

import com.example.board.dto.LoginRequest;
import com.example.board.dto.MessageResponse;
import com.example.board.dto.SignupRequest;
import com.example.board.entity.User;
import com.example.board.entity.UserRoleEnum;
import com.example.board.jwt.JwtUtil;
import com.example.board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    private static final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";

    @Transactional
    public ResponseEntity<MessageResponse> signup(SignupRequest signupRequest, BindingResult bindingResult) {
        String username = signupRequest.getUsername();
        String password = signupRequest.getPassword();
        String email = signupRequest.getEmail();

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(MessageResponse.builder()
                            .strStatus(HttpStatus.BAD_REQUEST.toString())
                            .message(bindingResult.getAllErrors().get(0).getDefaultMessage())
                            .build());
        }
        Optional<User> found = userRepository.findByUsername(username);
        if (found.isPresent()) {
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
        }

        // 사용자 ROLE 확인
        UserRoleEnum role = UserRoleEnum.USER;
        if (signupRequest.isAdmin()) {
            if (!signupRequest.getAdminToken().equals(ADMIN_TOKEN)) {
                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
            }
            role = UserRoleEnum.ADMIN;
        }

        User user = new User(username, password, email, role);
        userRepository.save(user);

        return ResponseEntity.ok(MessageResponse.builder()
                .intStatus(HttpStatus.OK.value())
                .strStatus(HttpStatus.OK.toString())
                .message("회원가입 성공")
                .build());
    }

    @Transactional(readOnly = true)
    public ResponseEntity<MessageResponse> login(LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        // 사용자 확인
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(MessageResponse.builder()
                            .intStatus(HttpStatus.OK.value())
                            .strStatus(HttpStatus.OK.toString())
                            .message("등록된 사용자가 존재하지 않습니다.")
                            .build());
        }
        // 비밀번호 확인
        if(!user.get().getPassword().equals(password)){
            return ResponseEntity.badRequest()
                    .body(MessageResponse.builder()
                            .intStatus(HttpStatus.OK.value())
                            .strStatus(HttpStatus.OK.toString())
                            .message("비밀번호가 일치하지 않습니다.")
                            .build());
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createToken(user.get().getUsername()));

        return ResponseEntity.ok()
                .headers(httpHeaders)
                .body(MessageResponse.builder()
                        .intStatus(HttpStatus.OK.value())
                        .strStatus(HttpStatus.OK.toString())
                        .message("로그인 성공")
                        .build());
    }
}
