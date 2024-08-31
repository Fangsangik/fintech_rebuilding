package miniproject.fintech.controller;

import ch.qos.logback.core.model.Model;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.dto.BankMemberDto;
import miniproject.fintech.error.CustomError;
import miniproject.fintech.service.MemoryMemberService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

import static miniproject.fintech.type.ErrorType.MEMBER_NOT_FOUND;

@Slf4j
@Controller
@RequestMapping("/register")
@RequiredArgsConstructor
public class MemberRegisterController {

    private final MemoryMemberService memberService;

    @GetMapping("/create")
    public String create() {
        return "register/create";
    }

    @PostMapping("/create")
    public ResponseEntity<BankMemberDto> createMember(@Valid @RequestBody BankMemberDto bankMemberDto) throws CustomError {
            log.info("회원 생성 요청 수신: {}", bankMemberDto);

        try {
            BankMemberDto newMember = memberService.createBankMember(bankMemberDto, new HashSet<>(Set.of("USER"))); // 기본 역할로 USER 추가
            log.info("회원 생성 성공: {}", newMember);
            return ResponseEntity.ok(newMember);
        } catch (CustomError e) {
            log.error("회원 생성 실패: {}", e.getMessage(), e);
            throw e; // 예외를 다시 던져서 상위 레벨에서 처리하도록 할 수 있습니다.
        }
    }
}
