package miniproject.fintech.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.dto.AccountDto;
import miniproject.fintech.dto.BankMemberDto;

import miniproject.fintech.dto.CreateAccountRequest;
import miniproject.fintech.dto.DtoConverter;
import miniproject.fintech.error.CustomError;
import miniproject.fintech.service.AccountServiceImpl;
import miniproject.fintech.service.MemoryMemberService;
import miniproject.fintech.type.ErrorType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static miniproject.fintech.type.ErrorType.*;

@Slf4j
@RestController
@RequestMapping("/account")
public class AccountController {

    private final AccountServiceImpl accountService;
    private final MemoryMemberService memberService;
    private final DtoConverter dtoConverter;

    @Autowired
    public AccountController(AccountServiceImpl accountService, MemoryMemberService memberService, DtoConverter dtoConverter) {
        this.accountService = accountService;
        this.memberService = memberService;
        this.dtoConverter = dtoConverter;
    }

    //accountNumber로 수정
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{accountNumber}/balance")
    @Cacheable(value = "accountsCache", key = "#accountNumber + '_balance'")
    public ResponseEntity<Long> getBalance(@PathVariable String accountNumber) {
        log.info("계좌 잔액 조회 요청 수신: ID={}", accountNumber);
        long accountBalance = accountService.getAccountBalance(accountNumber);
        log.info("계좌 잔액 조회 성공: ID={}, 잔액={}", accountNumber, accountBalance);
        return ResponseEntity.ok(accountBalance);
    }

    //accountNumber로 수정
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/exists/{accountNumber}")
    @Cacheable(value = "accountsCache", key = "#accountNumber + '_exists'")
    public ResponseEntity<Boolean> exists(@PathVariable String accountNumber) {
        log.info("계좌 존재 여부 확인 요청 수신: ID={}", accountNumber);

        boolean exists = accountService.existsByAccountNumber(accountNumber);
        log.info("계좌 존재 여부 확인 결과: ID={}, 존재 여부: {}", accountNumber, exists);

        return ResponseEntity.ok(exists);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{accountNumber}")
    @Cacheable(value = "accountsCache", key = "#accountNumber")
    public ResponseEntity<AccountDto> getAccountByAccountNumber(@PathVariable String accountNumber) {
        AccountDto accountDto = accountService.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new CustomError(ACCOUNT_NOT_FOUND));
        return ResponseEntity.ok(accountDto);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/create")
    @CacheEvict(value = "accountsCache", allEntries = true)
    public ResponseEntity<AccountDto> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        BankMemberDto bankMemberDto = request.getBankMemberDto();
        AccountDto accountDto = request.getAccountDto();

        // userId 유효성 검사 추가
        if (bankMemberDto == null || bankMemberDto.getUserId() == null || bankMemberDto.getUserId().isEmpty()) {
            log.error("BankMemberDto의 userId가 null이거나 빈 값입니다.");
            throw new CustomError(ErrorType.MEMBER_NOT_FOUND);
        }

        log.info("userId {}에 대한 계좌 생성 요청 중...", bankMemberDto.getUserId());

        try {
            // 회원 생성 또는 업데이트
            memberService.createOrUpdateBankMember(bankMemberDto);
        } catch (Exception e) {
            log.error("회원 생성 또는 업데이트 중 오류 발생", e);
            throw new CustomError(ErrorType.MEMBER_EXIST);
        }

        // 계좌 생성
        AccountDto createdAccountDto = accountService.createAccountForMember(accountDto, bankMemberDto.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAccountDto);
    }





    @PreAuthorize("hasRole('USER')")
    @PostMapping("/update/{accountNumber}")
    @CacheEvict(value = "accountsCache", key = "#accountNumber") // 계좌 업데이트 시 해당 계좌 캐시 무효화
    public ResponseEntity<AccountDto> updateAccount(@Valid @PathVariable String accountNumber, @RequestBody AccountDto accountDto) {
        if (!accountDto.getAccountNumber().equals(accountNumber)) {
            throw new CustomError(ErrorType.INVALID_ACCOUNT_NUMBER);
        }

        validation(accountNumber);  // 계좌 유효성 검증
        AccountDto updatedAccountDto = accountService.updateAccount(accountNumber, accountDto);
        return ResponseEntity.ok(updatedAccountDto);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/delete/{accountNumber}")
    @CacheEvict(value = "accountsCache", key = "#accountNumber") // 계좌 삭제 시 해당 계좌 캐시 무효화
    public ResponseEntity<String> deleteAccount(@PathVariable String accountNumber) {

        if (accountNumber == null) {
            throw new CustomError(ACCOUNT_NOT_FOUND);
        }

        accountService.delete(accountNumber);
        return ResponseEntity.ok("계좌가 성공적으로 삭제되었습니다.");
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/total-balance")
    @Cacheable(value = "accountsCache", key = "'totalBalance'")
    public ResponseEntity<Long> getTotalAccountBalance() {
        long totalAccountBalance = accountService.getTotalAccountBalance();
        return ResponseEntity.ok(totalAccountBalance);
    }

    // 계좌 유효성 검증
    private AccountDto validation(String accountNumber) {
        return accountService.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new CustomError(ACCOUNT_NOT_FOUND));
    }
}

