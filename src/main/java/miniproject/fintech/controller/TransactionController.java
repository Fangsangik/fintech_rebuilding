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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionServiceImpl transactionService;
    private final MemoryMemberService memberService;

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionId
            (@PathVariable Long id,
             @RequestParam BankMemberDto bankMemberDto) {

        Transaction transactionById = transactionService.getTransactionById(id, bankMemberDto)
                .orElseThrow(() -> new CustomError(ErrorType.TRANSACTION_NOT_FOUND));


        return ResponseEntity.status(HttpStatus.OK).body(transactionById);
    }

    @PostMapping("/create")
    public ResponseEntity<Transaction> createTransaction
            (@Valid @RequestBody TransactionDto transactionDto) {
        Transaction transaction = transactionService.createTransaction(transactionDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<Transaction> updateTransaction(
            @PathVariable Long id,
            @Valid
            @RequestBody CreateTransactionRequest request) {

        TransactionDto transactionDto = request.getTransactionDto();

        // 거래를 업데이트
        Transaction updatedTransaction = transactionService.updateTransaction(id, transactionDto, request.getBankMemberDto());
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
}