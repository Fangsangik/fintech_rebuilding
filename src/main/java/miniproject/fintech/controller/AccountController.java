package miniproject.fintech.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.Account;
import miniproject.fintech.dto.AccountDto;
import miniproject.fintech.dto.BankMemberDto;

import miniproject.fintech.dto.CreateAccountRequest;
import miniproject.fintech.error.CustomError;
import miniproject.fintech.service.AccountServiceImpl;
import miniproject.fintech.service.MemoryMemberService;
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
@RequiredArgsConstructor
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
    @GetMapping("/{id}/balance")
    @Cacheable(value = "accountsCache", key = "#id + '_balance'")
    public ResponseEntity<Long> getBalance(@PathVariable("id") Long id) {
        log.info("계좌 잔액 조회 요청 수신: ID={}", id);
        long accountBalance = accountService.getAccountBalance(id);
        log.info("계좌 잔액 조회 성공: ID={}, 잔액={}", id, accountBalance);
        return ResponseEntity.ok(accountBalance);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/exists/{id}")
    @Cacheable(value = "accountsCache", key = "#id + '_exists'")
    public ResponseEntity<Boolean> exists(@PathVariable("id") Long id) {
        log.info("계좌 존재 여부 확인 요청 수신: ID={}", id);

        boolean exists = accountService.existsById(id);
        log.info("계좌 존재 여부 확인 결과: ID={}, 존재 여부: {}", id, exists);

        return ResponseEntity.ok(exists);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    @Cacheable(value = "accountsCache", key = "#id")
    public ResponseEntity<AccountDto> getAccountById(@PathVariable Long id) {
        AccountDto accountDto = accountService.findById(id)
                .orElseThrow(() -> new CustomError(ACCOUNT_NOT_FOUND));
        return ResponseEntity.ok(accountDto);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/create")
    @CacheEvict(value = "accountsCache", allEntries = true) // 계좌 생성 시 캐시 무효화
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
        memberService.findById(bankMemberDto.getId());

        // 계좌 생성
        AccountDto createdAccountDto = accountService.createAccountForMember(accountDto, bankMemberDto.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAccountDto);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/update/{id}")
    @CacheEvict(value = "accountsCache", key = "#id") // 계좌 업데이트 시 해당 계좌 캐시 무효화
    public ResponseEntity<Account> updateAccount(@PathVariable Long id, @Valid @RequestBody AccountDto accountDto) {
        validation(id);  // 계좌 유효성 검증
        Account updatedAccountDto = accountService.updateAccount(id, accountDto);
        return ResponseEntity.ok(updatedAccountDto);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/delete/{id}")
    @CacheEvict(value = "accountsCache", key = "#id") // 계좌 삭제 시 해당 계좌 캐시 무효화
    public ResponseEntity<String> deleteAccount(@PathVariable Long id) {
        validation(id);  // 계좌 유효성 검증
        accountService.delete(id);
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
    private AccountDto validation(Long id) {
        return accountService.findById(id)
                .orElseThrow(() -> new CustomError(ACCOUNT_NOT_FOUND));
    }
}

