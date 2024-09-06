package miniproject.fintech.service;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
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
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TransactionServiceImplTest {

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
        // 모든 이전 데이터 삭제
        memberRepository.deleteAll();
        transactionRepository.deleteAll();

        // 테스트용 BankMember 생성 및 저장
        BankMember bankMember = memberRepository.save(BankMember.builder()
                .userId("test") // 중복 방지를 위해 UUID 사용
                .name("Messi")
                .accounts(new ArrayList<>())
                .address("seoul")
                .age(20)
                .build());

        // DTO로 변환
        bankMemberDto = converter.convertToBankMemberDto(bankMember);

        // 테스트용 TransactionDto 생성
        transactionDto = TransactionDto.builder()
                .transactionAmount(10000)
                .transactionType(TransactionType.DEPOSIT)
                .transactionStatus(TransactionStatus.SUCCESS)
                .bankMemberId(bankMemberDto.getUserId())
                .build();
    }


    @Test
    void getTransactionById() {
        // 트랜잭션 생성

        // 생성된 트랜잭션 ID로 조회
        TransactionDto findTransactionId = transactionService.getTransactionById(transactionDto.getId());

        // 트랜잭션이 존재하는지 검증
        assertEquals(transactionDto.getId(), findTransactionId.getId());

        // 생성된 트랜잭션 ID와 조회한 트랜잭션 ID가 일치하는지 검증
        assertEquals(transactionDto.getId(), findTransactionId.getId(), "트랜잭션 ID가 일치해야 합니다.");

        // 생성된 트랜잭션의 금액과 조회한 트랜잭션의 금액이 일치하는지 검증
        assertEquals(transactionDto.getTransactionAmount(), findTransactionId.getTransactionAmount(), "트랜잭션 금액이 일치해야 합니다.");
    }

    @Test
    void getAllTransaction() {
        // 모든 트랜잭션 조회
        List<TransactionDto> allTransactions = transactionService.getAllTransactions();
        log.info("All transactions: {}", allTransactions);

        // 검증
        assertNotNull(allTransactions, "Transaction List Must not be null");
        assertFalse(allTransactions.isEmpty(), "Transaction list should not be empty");

        // 트랜잭션 목록에 생성한 트랜잭션이 포함되어 있는지 검증
        boolean containsTransaction = allTransactions.stream()
                .anyMatch(t -> t.getId().equals(transactionDto.getId())); // 생성된 트랜잭션의 ID로 확인

        assertTrue(containsTransaction, "Transaction list should contain transaction");
    }
}
