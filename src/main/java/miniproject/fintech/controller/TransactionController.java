package miniproject.fintech.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.dto.BankMemberDto;
import miniproject.fintech.dto.CreateTransactionRequest;
import miniproject.fintech.dto.DeleteTransactionRequest;
import miniproject.fintech.dto.TransactionDto;
import miniproject.fintech.error.CustomError;
import miniproject.fintech.service.MemoryMemberService;
import miniproject.fintech.service.TransactionServiceImpl;
import miniproject.fintech.type.ErrorType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    private final TransactionServiceImpl transactionService;

    @Autowired
    public TransactionController(TransactionServiceImpl transactionService) {
        this.transactionService = transactionService;
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    // 거래 ID로 거래 조회
    @GetMapping("/{id}")
    @Cacheable(value = "transactionCache", key = "#id")
    public ResponseEntity<TransactionDto> getTransactionById(@PathVariable Long id) {
        TransactionDto transactionDto = transactionService.getTransactionById(id);
        return ResponseEntity.ok(transactionDto);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    // 모든 거래 조회
    @GetMapping
    @Cacheable(value = "transactionCache")
    public ResponseEntity<List<TransactionDto>> getAllTransactions() {
        List<TransactionDto> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/member/{memberId}")
    @Cacheable(value = "transactionCache", key = "#userId")
    public ResponseEntity<List<TransactionDto>> getTransactionsByMember(@PathVariable String userId) {
        List<TransactionDto> transactions = transactionService.getTransactionsByBankMember(userId);
        return ResponseEntity.ok(transactions);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    // 계좌 번호로 거래 조회
    @GetMapping("/account/{accountNumber}")
    @Cacheable(value = "transactionCache", key = "#accountNumber")
    public ResponseEntity<List<TransactionDto>> getTransactionsByAccountNumber(@PathVariable String accountNumber) {
        List<TransactionDto> transactions = transactionService.getTransactionsByAccountNumber(accountNumber);
        return ResponseEntity.ok(transactions);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    // 날짜 범위로 거래 조회
    @GetMapping("/date-range")
    @Cacheable(value = "transactionCache", key = "{#startDate, #endDate}")
    public ResponseEntity<List<TransactionDto>> getTransactionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<TransactionDto> transactions = transactionService.getTransactionsByDateRange(startDate, endDate);
        return ResponseEntity.ok(transactions);
    }
}
