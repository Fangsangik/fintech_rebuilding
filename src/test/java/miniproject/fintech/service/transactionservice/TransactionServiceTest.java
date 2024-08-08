package miniproject.fintech.service.transactionservice;

import miniproject.fintech.domain.Transaction;
import miniproject.fintech.repository.transactionrepository.TransactionRepository;
import miniproject.fintech.type.Grade;
import miniproject.fintech.type.TransactionStatus;
import miniproject.fintech.type.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TransactionServiceTest {

    @Autowired
    private TransactionServiceImpl transactionService;
    @Autowired
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
    }


    @Test
    void saveTransaction() {
        // Given: 테스트할 트랜잭션 객체 생성
        Transaction transaction = Transaction.builder()
                .transactionAmount(1000L)
                .transactionType(TransactionType.DEPOSIT)
                .transactionStatus(TransactionStatus.SUCCESS)
                .transactedAt(LocalDateTime.now())
                .curAmount(5000L)
                .currency("USD")
                .grade(Grade.NORMAL)
                .fee(5.0)
                .message("Deposit successful")
                .counterpartyInfo("John Doe")
                .build();

        // When: 트랜잭션을 저장
        Transaction savedTransaction = transactionRepository.save(transaction);

        // Then: 저장된 트랜잭션이 올바르게 저장되었는지 검증
        Transaction foundTransaction = transactionRepository.findById(savedTransaction.getId()).orElse(null);

        assertEquals(transaction.getTransactionAmount(), foundTransaction.getTransactionAmount());
        assertEquals(transaction.getTransactionType(), foundTransaction.getTransactionType());
        assertEquals(transaction.getTransactionStatus(), foundTransaction.getTransactionStatus());
    }
}