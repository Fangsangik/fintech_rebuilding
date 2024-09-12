package miniproject.fintech.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.dto.AccountDto;
import miniproject.fintech.dto.BankMemberDto;
import miniproject.fintech.service.MemoryMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@Slf4j
@RestController
@RequestMapping("/member")
public class MemberController {

    private final MemoryMemberService memberService;

    @Autowired
    public MemberController(MemoryMemberService memberService) {
        this.memberService = memberService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    @Cacheable(value = "MemberCache") // 모든 회원 목록을 캐시
    public ResponseEntity<List<BankMemberDto>> getAllMembers() {
        log.info("모든 회원 조회 요청");

        List<BankMemberDto> all = memberService.findAll();
        return ResponseEntity.ok(all);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/exists/{userId}")
    public ResponseEntity<Boolean> checkMemberExists(@PathVariable String userId) {
        log.info("회원 존재 여부 확인 요청 수신: ID={}", userId);

        boolean exists = memberService.existsById(userId);
        log.info("회원 존재 여부 확인 결과: ID={}, 존재 여부: {}", userId, exists);

        return ResponseEntity.ok(exists);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/change-password/{userId}")
    @CacheEvict(value = "MemberCache", key = "#userId") // 비밀번호 변경 시 캐시 무효화
    public ResponseEntity<String> changePassword(@PathVariable String userId,
                                                 @RequestParam String oldPassword,
                                                 @RequestParam String newPassword) {
        log.info("회원 비밀번호 변경 요청 수신 : ID = {}", userId);

        memberService.userChangePassword(userId, oldPassword, newPassword);
        log.info("회원 비밀번호 변경 성공 ID = {}", userId);

        return ResponseEntity.ok("Password changed successfully");
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{userId}")
    @Cacheable(value = "MemberCache", key = "#userId")
    public ResponseEntity<Optional<BankMember>> getMemberById(@PathVariable String userId) {
        log.info("회원 정보 요청 수신: ID={}", userId);

        Optional<BankMember> findMemberDto = memberService.findByUserId(userId);
        log.info("회원 정보 조회 성공: ID={}, Member: {}", userId, findMemberDto);

        if (findMemberDto.isEmpty()) {
            log.warn("회원 정보를 찾을 수 없습니다: ID={}", userId);
            return ResponseEntity.badRequest().build(); // BAD_REQUEST 명시적으로 반환
        }

        return ResponseEntity.ok(findMemberDto);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{userId}/accounts")
    @Cacheable(value = "accountsCache", key = "#userId") // 회원 계좌 정보 조회 시 캐싱
    public ResponseEntity<List<AccountDto>> getAccountsByMemberId(@PathVariable String userId) {
        log.info("회원 계좌 조회 요청 수신: ID={}", userId);

        List<AccountDto> accounts = memberService.findAccountByMemberId(userId);
        log.info("회원 계좌 조회 성공: ID={}, 계좌 수: {}", userId, accounts.size());

        return ResponseEntity.ok(accounts);
    }

    // 회원 정보 업데이트
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/update/{userId}")
    @CacheEvict(value = "MemberCache", key = "#userId") // 회원 정보 업데이트 시 캐시 무효화
    public ResponseEntity<BankMemberDto> updateBankMember(@PathVariable String userId,
                                                          @Valid @RequestBody BankMemberDto bankMemberDto) {
        log.info("회원 정보 업데이트 요청 수신: ID={}, 요청 데이터={}", userId, bankMemberDto);

        BankMemberDto updatedMemberDto = memberService.updateMember(userId, bankMemberDto);
        log.info("회원 정보 업데이트 성공: ID={}", userId);

        return ResponseEntity.accepted().body(updatedMemberDto);
    }

    // 회원 삭제
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/delete/{userId}")
    @CacheEvict(value = "MemberCache", key = "#userId") // 회원 삭제 시 캐시 무효화
    public ResponseEntity<String> deleteBankMember(@PathVariable String userId,
                                                   @RequestParam String password) {
        log.info("회원 삭제 요청 수신: ID={}", userId);

        memberService.deleteById(userId, password);
        log.info("회원 삭제 성공: ID={}", userId);

        return ResponseEntity.ok("Member deleted successfully");
    }
}