package miniproject.fintech.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.dto.BankMemberDto;
import miniproject.fintech.error.CustomError;
import miniproject.fintech.service.MemoryMemberService;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/register")
@RequiredArgsConstructor
public class MemberRegisterController {

    private final MemoryMemberService memberService;

    @PostMapping("/create")
    @CacheEvict(value = "register")
    public ResponseEntity<BankMember> createMember(@Valid @RequestBody BankMemberDto bankMemberDto) throws CustomError {
            log.info("회원 생성 요청 수신: {}", bankMemberDto);

        try {
            BankMember newMember = memberService.createBankMember(bankMemberDto, "USER"); // 기본 역할로 USER 추가
            log.info("회원 생성 성공: {}", newMember);
            return ResponseEntity.ok(newMember);
        } catch (CustomError e) {
            log.error("회원 생성 실패: {}", e.getMessage(), e);
            throw e; // 예외를 다시 던져서 상위 레벨에서 처리하도록 할 수 있습니다.
        }
    }
}
