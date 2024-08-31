package miniproject.fintech.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import miniproject.fintech.config.JwtTokenUtil;
import miniproject.fintech.dto.BankMemberDto;
import miniproject.fintech.service.LoginService;
import miniproject.fintech.service.MemoryMemberService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody BankMemberDto loginRequest) {
        log.info("로그인 요청 수신: ID={}", loginRequest.getId());

        // ID와 패스워드 값이 제공되었는지 확인
        if (loginRequest.getId() == null || loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
            log.warn("로그인 요청 실패: ID 또는 비밀번호가 제공되지 않음. ID: {}", loginRequest.getId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("로그인 요청 실패: ID 또는 비밀번호가 제공되지 않음.");
        }

        log.info("로그인 검증 시작: ID={}", loginRequest.getId());

        // 로그인 검증 및 JWT 토큰 생성
        Optional<String> jwtToken = loginService.loginCheck(loginRequest);

        if (jwtToken.isPresent()) {
            log.info("로그인 성공: ID={}", loginRequest.getId());
            // JWT 토큰을 JSON 형식으로 반환
            Map<String, String> response = new HashMap<>();
            response.put("token", jwtToken.get());
            return ResponseEntity.ok(response);
        } else {
            log.warn("로그인 실패: 사용자 ID {}의 인증에 실패했습니다.", loginRequest.getId());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인 실패: 사용자 ID 인증에 실패했습니다.");
        }
    }
}