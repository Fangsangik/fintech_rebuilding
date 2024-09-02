package miniproject.fintech.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import miniproject.fintech.domain.Transaction;
import miniproject.fintech.dto.BankMemberDto;
import miniproject.fintech.dto.CreateTransactionRequest;
import miniproject.fintech.dto.TransactionDto;
import miniproject.fintech.error.CustomError;
import miniproject.fintech.service.MemoryMemberService;
import miniproject.fintech.service.TransactionServiceImpl;
import miniproject.fintech.type.ErrorType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")

public class TransactionController {

    private final TransactionServiceImpl transactionService;
    private final MemoryMemberService memberService;

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDto> getTransactionId
            (@PathVariable Long id,
             @RequestParam BankMemberDto bankMemberDto) {

        TransactionDto transactionById = transactionService.getTransactionById(id, bankMemberDto)
                .orElseThrow(() -> new CustomError(ErrorType.TRANSACTION_NOT_FOUND));


        return ResponseEntity.status(HttpStatus.OK).body(transactionById);
    }

    @PostMapping("/create")
    public ResponseEntity<TransactionDto> createTransaction
            (@Valid @RequestBody TransactionDto transactionDto) {
        TransactionDto transaction = transactionService.createTransaction(transactionDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<TransactionDto> updateTransaction(
            @PathVariable Long id,
            @Valid
            @RequestBody CreateTransactionRequest request) {

        TransactionDto transactionDto = request.getTransactionDto();

        // 거래를 업데이트
        TransactionDto updatedTransaction = transactionService.updateTransaction(id, transactionDto, request.getBankMemberDto());
        return ResponseEntity.ok(updatedTransaction);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteTransaction(
            @Valid
            @RequestBody TransactionDto transactionDto,
            @RequestBody BankMemberDto bankMemberDto
    ) {
        transactionService.deleteTransaction(transactionDto, bankMemberDto);

        return ResponseEntity.ok("Transaction deleted successfully");
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/member/{memberId}")
    @Cacheable(value = "transactionsCache", key = "'member_' + #memberId")
    public ResponseEntity<List<TransactionDto>> getAllTransactionsByMemberId(@PathVariable Long memberId) {
        log.info("특정 회원의 모든 거래 조회 요청 수신: 회원 ID={}", memberId);

        List<TransactionDto> memberTransactions = transactionService.getAllTransactionsByMemberId(memberId);
        log.info("특정 회원의 모든 거래 조회 성공, 거래 수: {}", memberTransactions.size());

        return ResponseEntity.ok(memberTransactions);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/all")
    @Cacheable(value = "transactionsCache", key = "'allTransactions'")
    public ResponseEntity<List<TransactionDto>> getAllTransactions() {
        log.info("모든 거래 조회 요청 수신");

        List<TransactionDto> allTransactions = transactionService.getAllTransactions();
        log.info("모든 거래 조회 성공, 거래 수: {}", allTransactions.size());

        return ResponseEntity.ok(allTransactions);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/account/{accountId}")
    @Cacheable(value = "transactionsCache", key = "'account_' + #accountId")
    public ResponseEntity<Page<TransactionDto>> getTransactionsByAccountId(
            @PathVariable Long accountId,
            Pageable pageable) {
        log.info("계좌별 거래 조회 요청 수신: 계좌 ID={}", accountId);

        Page<TransactionDto> transactions = transactionService.getTransactionsByAccountId(accountId, pageable);
        log.info("계좌별 거래 조회 성공, 거래 수: {}", transactions.getTotalElements());

        return ResponseEntity.ok(transactions);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/date-range")
    @Cacheable(value = "transactionsCache", key = "'dateRange_' + #startDate + '_' + #endDate")
    public ResponseEntity<Page<TransactionDto>> getTransactionsByDateRange(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate,
            Pageable pageable) {
        log.info("기간별 거래 조회 요청 수신: 시작일 = {}, 종료일 = {}", startDate, endDate);

        Page<TransactionDto> transactions = transactionService.getTransactionsByDateRange(startDate, endDate, pageable);
        log.info("기간별 거래 조회 성공, 거래 수: {}", transactions.getTotalElements());

    }
}