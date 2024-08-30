package miniproject.fintech.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.dto.BankMemberDto;
import miniproject.fintech.error.CustomError;
import miniproject.fintech.service.MemoryMemberService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

import static miniproject.fintech.type.ErrorType.MEMBER_NOT_FOUND;

@RestController
@RequestMapping("/register")
@RequiredArgsConstructor
public class MemberRegisterController {

    private final MemoryMemberService memberService;

    @PostMapping("/create")
    public ResponseEntity<BankMember> createMember(@Valid @RequestBody BankMemberDto bankMemberDto) throws CustomError {
        BankMember newMember = memberService.createBankMember(bankMemberDto, new HashSet<>(Set.of("USER"))); // 기본 역할로 USER 추가
        return ResponseEntity.status(HttpStatus.CREATED).body(newMember);
    }
}
