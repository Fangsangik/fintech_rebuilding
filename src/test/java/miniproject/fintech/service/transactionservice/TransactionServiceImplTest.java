package miniproject.fintech.service.transactionservice;

import miniproject.fintech.domain.BankMember;
import miniproject.fintech.domain.Transaction;
import miniproject.fintech.dto.TransactionDto;
import miniproject.fintech.repository.MemberRepository;
import miniproject.fintech.repository.TransactionRepository;
import miniproject.fintech.type.Grade;
import miniproject.fintech.type.TransactionStatus;
import miniproject.fintech.type.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) //인메모리 사용
class TransactionServiceImplTest {

    private final Logger logger = LoggerFactory.getLogger(Transaction.class);

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionServiceImpl transactionService;

    @Autowired
    private MemberRepository memberRepository;

    @PersistenceContext
    private EntityManager em;

    private BankMember bankMember;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        BankMember bankMember = BankMember.builder()
                .name("말디니")
                .email("maldini@gamil.com")
                .build();
        this.bankMember = memberRepository.save(bankMember);

        Transaction transaction = Transaction.builder()
                .transactionAmount(10000)
                .transactionType(TransactionType.DEPOSIT)
                .transactionStatus(TransactionStatus.SUCCESS)
                .bankMember(bankMember)
                .build();

        this.transaction = transactionRepository.save(transaction);
    }

    @Test
    void getTransactionById() {
        Transaction savedTransaction = transactionRepository.save(transaction);

        Optional<Transaction> rstTransaction = transactionService.getTransactionById
                (transaction.getId(), bankMember);

        assertTrue(rstTransaction.isPresent(), "Transaction이 존재해야합니다");
        Transaction rst = rstTransaction.get();
        assertEquals(savedTransaction.getId(), rst.getId(), "아이디가 동일해야 합니다");
        assertEquals(savedTransaction.getTransactionAmount(), rst.getTransactionAmount());
    }

    @Test
    void getAllTransaction() {
//        Transaction savedTransaction = transactionRepository.save(transaction); BeforeaEach에서 이미 저장

        List<Transaction> allTransaction = transactionService.getAllTransaction();
        assertNotNull(allTransaction, "Transaction List Must not be null");
        assertTrue(allTransaction.size() > 0, "Transaction list should not be empty");

        boolean containsTransaction = allTransaction.stream()
                .anyMatch(t -> t.getId().equals(this.transaction.getId()));

        assertTrue(containsTransaction, "Transaction list should contain transaction");
    }

    @Test
    @Transactional
    void updateTransaction() {
        TransactionDto transactionDto = TransactionDto.builder()
                .transactedAt(LocalDateTime.now())
                .transactionAmount(20000)
                .transactionStatus(TransactionStatus.FAIL)
                .curAmount(30000)
                .referenceNumber("newRef")
                .grade(Grade.VIP)
                .build();

        Transaction updatedTransaction = transactionService.updateTransaction(transaction.getId(), transactionDto, bankMember);

        assertNotNull(updatedTransaction, "updated transaction should not be null");
        assertEquals(transactionDto.getTransactionAmount(), updatedTransaction.getTransactionAmount(), "Transaction amount should be updated");
        assertEquals(transactionDto.getTransactionStatus(), updatedTransaction.getTransactionStatus(), "Transaction status should be updated");
        assertEquals(transactionDto.getCurAmount(), updatedTransaction.getCurAmount(), "Transaction amount should be updated");
        assertEquals(transactionDto.getGrade(), updatedTransaction.getGrade(), "Transaction grade should be updated");
    }

    @Test
    @Transactional
    void deleteTransaction() {
        Transaction savedTransaction = transactionRepository.save(transaction);
        transactionService.deleteTransaction(savedTransaction.getId(), bankMember);

        boolean exists = transactionRepository.existsById(savedTransaction.getId());
        assertFalse(exists, "Transaction should be deleted");
    }
}