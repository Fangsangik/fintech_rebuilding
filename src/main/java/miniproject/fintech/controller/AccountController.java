package miniproject.fintech.controller;

import lombok.RequiredArgsConstructor;
import miniproject.fintech.domain.Account;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.dto.AccountDto;
import miniproject.fintech.dto.BankMemberDto;

import miniproject.fintech.dto.CreateAccountRequest;
import miniproject.fintech.error.CustomError;
import miniproject.fintech.service.accountservice.AccountService;
import miniproject.fintech.service.memberservice.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;

import java.util.List;

import static miniproject.fintech.type.ErrorType.*;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final MemberService memberService;

    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> getAccountId(@PathVariable Long id) {
        AccountDto findAccountId = accountService.findById(id)
                .orElseThrow(() -> new CustomError(MEMBER_NOT_FOUND));
        return ResponseEntity.ok(findAccountId);
    }

    @PostMapping("/create")
    public ResponseEntity<AccountDto> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        BankMemberDto bankMemberDto = request.getBankMemberDto();
        AccountDto accountDto = request.getAccountDto();

        if (bankMemberDto == null) {
            throw new IllegalArgumentException("BankMemberDto cannot be null");
        }
        if (accountDto == null) {
            throw new IllegalArgumentException("AccountDto cannot be null");
        }

        // BankMember를 찾습니다.
        BankMember bankMember = memberService.findById(bankMemberDto.getId())
                .orElseThrow(() -> new CustomError(MEMBER_NOT_FOUND));

        // 계좌를 생성합니다.
        AccountDto createAccountDto = accountService.createAccountForMember(accountDto, bankMember.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createAccountDto);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<AccountDto> updateAccount(@PathVariable Long id, @Valid @RequestBody AccountDto accountDto) {
        validation(id);
        AccountDto updatedAccountDto = accountService.updateAccount(id, accountDto);
        return ResponseEntity.ok().body(updatedAccountDto);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteAccount(@PathVariable Long id) {
        validation(id); // 멤버 존재 확인
        accountService.delete(id);
        return ResponseEntity.ok("계좌가 성공적으로 삭제되었습니다.");
    }

    @GetMapping("/total-balance")
    public ResponseEntity<Long> getTotalAccountBalance() {
        long totalAccountBalance = accountService.getTotalAccountBalance();
        return ResponseEntity.ok(totalAccountBalance);
    }

    private BankMember validation(Long id) {
        return memberService.findById(id)
                .orElseThrow(() -> new CustomError(MEMBER_NOT_FOUND));
    }
}