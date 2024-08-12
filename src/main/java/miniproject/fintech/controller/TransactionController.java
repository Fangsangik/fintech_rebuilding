package miniproject.fintech.controller;

import lombok.RequiredArgsConstructor;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.domain.Transaction;
import miniproject.fintech.dto.BankMemberDto;
import miniproject.fintech.dto.CreateTransactionRequest;
import miniproject.fintech.dto.TransactionDto;
import miniproject.fintech.error.CustomError;
import miniproject.fintech.service.memberservice.MemoryMemberService;
import miniproject.fintech.service.transactionservice.TransactionService;
import miniproject.fintech.type.ErrorType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static miniproject.fintech.type.ErrorType.MEMBER_NOT_FOUND;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final MemoryMemberService memberService;

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionId
            (@PathVariable Long id,
             @RequestParam long bankMemberId) {
        BankMember bankMember = memberService.getBankMemberById(bankMemberId);

        Transaction transactionById = transactionService.getTransactionById(id, bankMember)
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
        // BankMemberDto를 사용하여 BankMember를 데이터베이스에서 가져오기
        BankMember bankMember = memberService.findById(request.getBankMemberDto().getId())
                .orElseThrow(() -> new CustomError(MEMBER_NOT_FOUND));

        TransactionDto transactionDto = request.getTransactionDto();

        // 거래를 업데이트
        Transaction updatedTransaction = transactionService.updateTransaction(id, transactionDto, bankMember);
        return ResponseEntity.ok(updatedTransaction);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteTransaction(
            @PathVariable Long id,
            @Valid
            @RequestBody BankMemberDto bankMemberDto
    ) {
        BankMember member = memberService.findById(bankMemberDto.getId())
                .orElseThrow(() -> new CustomError(MEMBER_NOT_FOUND));
        transactionService.deleteTransaction(id, member);

        return ResponseEntity.ok("Transaction deleted successfully");

    }
}