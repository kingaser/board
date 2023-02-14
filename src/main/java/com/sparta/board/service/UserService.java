package com.sparta.board.service;

import com.sparta.board.dto.request.LoginRequest;
import com.sparta.board.dto.request.SignupRequest;
import com.sparta.board.dto.response.MessageResponse;
import com.sparta.board.entity.User;
import com.sparta.board.entity.enumtype.ExceptionType;
import com.sparta.board.entity.enumtype.UserRoleEnum;
import com.sparta.board.jwt.JwtUtil;
import com.sparta.board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static com.sparta.board.exception.ExceptionHandling.responseException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    private static final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";

    @Transactional
    public ResponseEntity<Object> signup(SignupRequest signupRequest, BindingResult bindingResult) {
        String username = signupRequest.getUsername();
        String password = signupRequest.getPassword();
        String email = signupRequest.getEmail();

        if (bindingResult.hasErrors()) {
            return responseException(HttpStatus.BAD_REQUEST, bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        Optional<User> found = userRepository.findByUsername(username);
        if (found.isPresent()) {
            return responseException(ExceptionType.DUPLICATED_USERNAME);
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
                .status(HttpStatus.OK.value())
                .message("회원가입 성공")
                .build());
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Object> login(LoginRequest loginRequest, HttpServletResponse response) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        // 사용자 확인, 비밀번호 확인
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty() || !(user.get().getPassword().equals(password))) {
            return responseException(ExceptionType.NOT_MATCH_INFO);
        }

        //header에 들어갈 JWT 세팅
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createToken(user.get().getUsername()));

        return ResponseEntity.ok()
                .headers(httpHeaders)
                .body(MessageResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("로그인 성공")
                        .build());
    }
}
