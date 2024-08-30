package miniproject.fintech.service;

import miniproject.fintech.domain.BankMember;
import miniproject.fintech.domain.Transaction;
import miniproject.fintech.dto.BankMemberDto;
import miniproject.fintech.dto.DtoConverter;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) //인메모리 사용
class TransactionServiceImplTest {

    private final Logger logger = LoggerFactory.getLogger(Transaction.class);

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionServiceImpl transactionService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private DtoConverter converter;

    private BankMemberDto bankMemberDto;
    private TransactionDto transactionDto;

    @BeforeEach
    void setUp() {
        memberRepository.deleteAll();

        BankMember bankMember = memberRepository.save(BankMember.builder()
                .name("Messi")
                .accountNumber("1234")
                .accounts(new ArrayList<>())
                .address("seoul")
                .age(20)
                .build());

        bankMemberDto = converter.convertToBankMemberDto(bankMember);

        Transaction transaction = transactionRepository.save(Transaction.builder()
                .transactionAmount(10000)
                .transactionType(TransactionType.DEPOSIT)
                .transactionStatus(TransactionStatus.SUCCESS)
                .bankMember(bankMember)
                .build());

        transactionDto = converter.convertToTransactionDto(transaction);
    }

    @Transactional
    @Test
    void getTransactionById() {

        Optional<Transaction> findTransaction = transactionService.getTransactionById(transactionDto.getId(), bankMemberDto);
        assertTrue(findTransaction.isPresent());
        assertEquals(transactionDto.getId(), findTransaction.get().getId());
        assertEquals(transactionDto.getTransactionAmount(), findTransaction.get().getTransactionAmount());
    }

    @Transactional
    @Test
    void getAllTransaction() {
//        Transaction savedTransaction = transactionRepository.save(transaction); BeforeaEach에서 이미 저장

        List<Transaction> allTransaction = transactionService.getAllTransaction();
        assertNotNull(allTransaction, "Transaction List Must not be null");
        assertTrue(allTransaction.size() > 0, "Transaction list should not be empty");

        boolean containsTransaction = allTransaction.stream()
                .anyMatch(t -> t.getId().equals(this.transactionDto.getId()));

        assertTrue(containsTransaction, "Transaction list should contain transaction");
    }

    @Test
    @Transactional
    void updateTransaction() {
        // Given: 기존 거래 데이터 설정
        TransactionDto transactionDto = TransactionDto.builder()
                .id(this.transactionDto.getId())
                .transactedAt(LocalDateTime.now())
                .transactionAmount(20000)
                .transactionStatus(TransactionStatus.FAIL)
                .curAmount(30000)
                .referenceNumber("newRef")
                .grade(Grade.VIP)
                .build();

        // When: 거래 업데이트 수행
        Transaction updatedTransaction = transactionService.updateTransaction(transactionDto.getId(), transactionDto, bankMemberDto);

        // Then: 업데이트 결과 검증
        assertNotNull(updatedTransaction, "updated transaction should not be null");
        assertEquals(transactionDto.getTransactionAmount(), updatedTransaction.getTransactionAmount(), "Transaction amount should be updated");
        assertEquals(transactionDto.getTransactionStatus(), updatedTransaction.getTransactionStatus(), "Transaction status should be updated");
        assertEquals(transactionDto.getCurAmount(), updatedTransaction.getCurAmount(), "Transaction amount should be updated");
        assertEquals(transactionDto.getGrade(), updatedTransaction.getGrade(), "Transaction grade should be updated");
    }

    @Test
    @Transactional
    void deleteTransaction() {
        transactionService.deleteTransaction(transactionDto, bankMemberDto);

        boolean exists = transactionRepository.existsById(transactionDto.getId());
        assertFalse(exists, "Transaction should be deleted");
    }
}