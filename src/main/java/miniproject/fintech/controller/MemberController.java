package miniproject.fintech.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.Account;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.dto.BankMemberDto;
import miniproject.fintech.service.MemoryMemberService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemoryMemberService memberService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    @Cacheable(value = "MemberCache") // 모든 회원 목록을 캐시
    public ResponseEntity<List<BankMember>> getAllMembers() {
        log.info("모든 회원 조회 요청");

        List<BankMember> all = memberService.findAll();
        return ResponseEntity.ok(all);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> checkMemberExists(@PathVariable Long id) {
        log.info("회원 존재 여부 확인 요청 수신: ID={}", id);

        boolean exists = memberService.existsById(id);
        log.info("회원 존재 여부 확인 결과: ID={}, 존재 여부: {}", id, exists);

        return ResponseEntity.ok(exists);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/change-password/{id}")
    @CacheEvict(value = "MemberCache", key = "#id") // 비밀번호 변경 시 캐시 무효화
    public ResponseEntity<String> changePassword(@PathVariable("id") Long id, @RequestParam String oldPassword, @RequestParam String newPassword) {
        log.info("회원 비밀번호 변경 요청 수신 : ID = {}", id);

        memberService.userChangePassword(id, oldPassword, newPassword);
        log.info("회원 비밀번호 변경 성공 ID = {}", id);

        return ResponseEntity.ok("Password changed successfully");
    }

    // ID로 회원 조회
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    @Cacheable(value = "MemberCache", key = "#id") // ID로 회원 조회 시 캐싱
    public ResponseEntity<BankMember> getMemberById(@PathVariable Long id) {
        log.info("회원 정보 요청 수신: ID={}", id);

        BankMember findMemberDto = memberService.findById(id);
        log.info("회원 정보 조회 성공: ID={}", id);

        return ResponseEntity.ok(findMemberDto);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}/accounts")
    @Cacheable(value = "accountsCache", key = "#id") // 회원 계좌 정보 조회 시 캐싱
    public ResponseEntity<List<Account>> getAccountsByMemberId(@PathVariable Long id) {
        log.info("회원 계좌 조회 요청 수신: ID={}", id);

        List<Account> accounts = memberService.findAccountByMemberId(id);
        log.info("회원 계좌 조회 성공: ID={}, 계좌 수: {}", id, accounts.size());

        return ResponseEntity.ok(accounts);
    }

    // 회원 정보 업데이트
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/update/{id}")
    @CacheEvict(value = "MemberCache", key = "#id") // 회원 정보 업데이트 시 캐시 무효화
    public ResponseEntity<BankMember> updateBankMember(
            @PathVariable Long id,
            @Valid @RequestBody BankMemberDto bankMemberDto) {
        log.info("회원 정보 업데이트 요청 수신: ID={}, 요청 데이터={}", id, bankMemberDto);

        BankMember updatedMemberDto = memberService.updateMember(id, bankMemberDto);
        log.info("회원 정보 업데이트 성공: ID={}", id);

        return ResponseEntity.accepted().body(updatedMemberDto);
    }

    // 회원 삭제
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/delete/{id}")
    @CacheEvict(value = "MemberCache", key = "#id") // 회원 삭제 시 캐시 무효화
    public ResponseEntity<String> deleteBankMember(
            @PathVariable Long id,
            @RequestParam String password) {
        log.info("회원 삭제 요청 수신: ID={}", id);

        memberService.deleteById(id, password);
        log.info("회원 삭제 성공: ID={}", id);

        return ResponseEntity.ok("Member deleted successfully");
    }
}
