package miniproject.fintech.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.dto.AccountDto;
import miniproject.fintech.dto.BankMemberDto;

import miniproject.fintech.dto.CreateAccountRequest;
import miniproject.fintech.error.CustomError;
import miniproject.fintech.service.AccountServiceImpl;
import miniproject.fintech.service.MemoryMemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static miniproject.fintech.type.ErrorType.*;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class AccountController {

    private final AccountServiceImpl accountService;
    private final MemoryMemberService memberService;

    // ID로 계좌 조회
    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> getAccountById(@PathVariable Long id) {
        AccountDto accountDto = accountService.findById(id)
                .orElseThrow(() -> new CustomError(ACCOUNT_NOT_FOUND));
        return ResponseEntity.ok(accountDto);
    }

    // 계좌 생성
    @PostMapping("/create")
    public ResponseEntity<AccountDto> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        // Request 객체에서 필요한 정보 추출
        BankMemberDto bankMemberDto = request.getBankMemberDto();
        AccountDto accountDto = request.getAccountDto();

        // 유효성 검사
        if (bankMemberDto == null) {
            throw new IllegalArgumentException("BankMemberDto cannot be null");
        }
        if (accountDto == null) {
            throw new IllegalArgumentException("AccountDto cannot be null");
        }

        // BankMember 존재 확인 및 가져오기
        memberService.findById(bankMemberDto.getId())
                .orElseThrow(() -> new CustomError(MEMBER_NOT_FOUND));

        // 계좌 생성
        AccountDto createdAccountDto = accountService.createAccountForMember(accountDto, bankMemberDto.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAccountDto);
    }

    // 계좌 업데이트
    @PostMapping("/update/{id}")
    public ResponseEntity<AccountDto> updateAccount(@PathVariable Long id, @Valid @RequestBody AccountDto accountDto) {
        validation(id);  // 계좌 유효성 검증
        AccountDto updatedAccountDto = accountService.updateAccount(id, accountDto);
        return ResponseEntity.ok(updatedAccountDto);
    }

    // 계좌 삭제
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteAccount(@PathVariable Long id) {
        validation(id);  // 계좌 유효성 검증
        accountService.delete(id);
        return ResponseEntity.ok("계좌가 성공적으로 삭제되었습니다.");
    }

    // 총 계좌 잔액 조회
    @GetMapping("/total-balance")
    public ResponseEntity<Long> getTotalAccountBalance() {
        long totalAccountBalance = accountService.getTotalAccountBalance();
        return ResponseEntity.ok(totalAccountBalance);
    }

    // 계좌 유효성 검증
    private AccountDto validation(Long id) {
        return accountService.findById(id)
                .orElseThrow(() -> new CustomError(ACCOUNT_NOT_FOUND));
    }
}
