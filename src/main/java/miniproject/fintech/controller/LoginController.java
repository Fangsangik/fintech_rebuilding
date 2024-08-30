package miniproject.fintech.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import miniproject.fintech.dto.BankMemberDto;
import miniproject.fintech.service.LoginService;
import miniproject.fintech.service.MemoryMemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/login")
public class LoginController {

    private final LoginService loginService;
    private final MemoryMemberService memberService;

    @PostMapping
    public ResponseEntity<?> login(@RequestParam Long id,
                                   @RequestParam String password) {
        // ID와 패스워드 값이 제공되었는지 확인
        if (id == null || password == null || password.trim().isEmpty()) {
            log.warn("로그인 요청 실패: id 또는 password가 제공되지 않음. ID: {}", id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("로그인 요청 실패: id 또는 password가 제공되지 않음.");
        }

        // 로그인 요청 DTO 생성
        BankMemberDto loginRequest = new BankMemberDto();
        loginRequest.setId(id);
        loginRequest.setPassword(password);

        // 로그인 검증
        Optional<BankMemberDto> rst = loginService.loginCheck(loginRequest);

        if (rst.isPresent()) {
            // 로그인 성공 응답
            return ResponseEntity.ok(rst.get());
        } else {
            // 로그인 실패 응답
            log.warn("로그인 실패: 사용자 ID {}의 인증에 실패했습니다.", id);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인 실패: 사용자 ID 인증에 실패했습니다.");
        }
    }
}
