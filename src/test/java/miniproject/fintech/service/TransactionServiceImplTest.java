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
        transactionRepository.deleteAll();
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
    void createTransaction() {
        Transaction transaction = transactionService.createTransaction(transactionDto);
        assertNotNull(transaction);
        assertEquals(transactionDto.getTransactionAmount(), transaction.getTransactionAmount(), "The transaction amounts should match");
        assertEquals(transactionDto.getTransactionType(), transaction.getTransactionType(), "The transaction types should match");
        assertEquals(transactionDto.getTransactionStatus(), transaction.getTransactionStatus(), "The transaction statuses should match");
        assertEquals(bankMemberDto.getId(), transaction.getBankMember().getId(), "The bank member IDs should match");

    }

    @Transactional
    @Test
    void getTransactionById() {
        transactionRepository.deleteAll();  // 트랜잭션 데이터 삭제

        // 트랜잭션 생성 후 트랜잭션 ID를 올바르게 설정
        Transaction createdTransaction = transactionService.createTransaction(transactionDto);
        transactionDto.setId(createdTransaction.getId());

        // 트랜잭션 ID로 검색 시도
        Optional<Transaction> findTransaction = transactionService.getTransactionById(transactionDto.getId(), bankMemberDto);

        // 검색된 트랜잭션이 존재하는지 확인
        assertTrue(findTransaction.isPresent());
        assertEquals(transactionDto.getId(), findTransaction.get().getId());
        assertEquals(transactionDto.getTransactionAmount(), findTransaction.get().getTransactionAmount());
    }

    @Transactional
    @Test
    void getAllTransaction() {
//        Transaction savedTransaction = transactionRepository.save(transaction); BeforeaEach에서 이미 저장

        List<TransactionDto> allTransaction = transactionService.getAllTransactions();
        assertNotNull(allTransaction, "Transaction List Must not be null");
        assertTrue(!allTransaction.isEmpty(), "Transaction list should not be empty");

        boolean containsTransaction = allTransaction.stream()
                .anyMatch(t -> t.getId().equals(this.transactionDto.getId()));

        assertTrue(containsTransaction, "Transaction list should contain transaction");
    }

    @Test
    @Transactional
    void updateTransaction() {

        transactionDto.setBankMemberId(bankMemberDto.getId());
        Transaction transaction = transactionService.createTransaction(transactionDto);
        // When: 거래 업데이트 수행
        Transaction updatedTransaction = transactionService.updateTransaction(transaction.getId(), transactionDto, bankMemberDto);

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
        transactionDto.setBankMemberId(bankMemberDto.getId());
        Transaction transaction = transactionService.createTransaction(transactionDto);

        transactionDto.setId(transaction.getId());
        transactionService.deleteTransaction(transactionDto, bankMemberDto);

        boolean exists = transactionRepository.existsById(transactionDto.getId());
        assertFalse(exists, "Transaction should be deleted");
    }
}