package miniproject.fintech.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.dto.BankMemberDto;
import miniproject.fintech.error.CustomError;
import miniproject.fintech.service.MemoryMemberService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import static miniproject.fintech.type.ErrorType.*;


@Slf4j
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemoryMemberService memberService;

    // ID로 회원 조회
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    @Cacheable(value = "fintechCache", key = "#id")
    public ResponseEntity<BankMemberDto> getMemberById(@PathVariable Long id) {
        log.info("회원 정보 요청 수신: ID={}", id);

        BankMemberDto findMemberDto = memberService.findById(id)
                .orElseThrow(() -> new CustomError(MEMBER_NOT_FOUND));
        log.info("회원 정보 조회 성공: ID={}", id);

        return ResponseEntity.ok(findMemberDto);
    }

    // 회원 정보 업데이트
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/update/{id}")
    public ResponseEntity<BankMemberDto> updateBankMember(
            @PathVariable Long id,
            @Valid @RequestBody BankMemberDto bankMemberDto) {
        log.info("회원 정보 업데이트 요청 수신: ID={}, 요청 데이터={}", id, bankMemberDto);

        BankMemberDto updatedMemberDto = memberService.updateMember(id, bankMemberDto);
        log.info("회원 정보 업데이트 성공: ID={}", id);

        return ResponseEntity.accepted().body(updatedMemberDto);
    }

    // 회원 삭제
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteBankMember(
            @PathVariable Long id,
            @RequestParam String password) {
        log.info("회원 삭제 요청 수신: ID={}", id);

        memberService.deleteById(id, password);
        log.info("회원 삭제 성공: ID={}", id);

        return ResponseEntity.ok("Member deleted successfully");
    }
}
