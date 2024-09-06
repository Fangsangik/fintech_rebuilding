package miniproject.fintech.service;

import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.Account;
import miniproject.fintech.domain.Deposit;
import miniproject.fintech.dto.DepositDto;
import miniproject.fintech.repository.AccountRepository;
import miniproject.fintech.repository.DepositRepository;
import miniproject.fintech.type.AccountStatus;
import miniproject.fintech.type.DepositStatus;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;


@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DepositServiceImplTest {

    @Autowired
    private DepositServiceImpl depositService;

    @Autowired
    private DepositRepository depositRepository;

    @Autowired
    private AccountRepository accountRepository;

    private Account sourceAccount;
    private Account destinationAccount;

    @BeforeEach
    void setUp() {
        // 출금 계좌 생성
        sourceAccount = Account.builder()
                .accountNumber("source123")
                .amount(10000L)
                .accountStatus(AccountStatus.REGISTER)
                .createdAt(LocalDateTime.now())
                .build();

        // 입금 계좌 생성
        destinationAccount = Account.builder()
                .accountNumber("destination123")
                .amount(5000L)
                .accountStatus(AccountStatus.REGISTER)
                .createdAt(LocalDateTime.now())
                .build();

        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);
    }

    @Test
    @Order(1)
    void depositTest() {
        // Given: 입금 설정
        long depositAmount = 2000L;
        DepositDto depositDto = DepositDto.builder()
                .sourceAccountNumber(sourceAccount.getAccountNumber())
                .destinationAccountNumber(destinationAccount.getAccountNumber())
                .depositAmount(depositAmount)
                .depositAt(LocalDateTime.now())
                .depositStatus(DepositStatus.COMPLETED)
                .build();

        log.info("입금 처리 시작: 입금액 {} 출금 계좌: {}, 입금 계좌: {}", depositAmount, sourceAccount.getAccountNumber(), destinationAccount.getAccountNumber());

        // When: 입금 처리
        DepositDto savedDeposit = depositService.processDeposit(depositDto);

        log.info("입금 처리 완료. 저장된 입금 ID: {}", savedDeposit.getId());

        // Then: 입금 및 계좌 업데이트 확인
        Deposit receivedDeposit = depositRepository.findById(savedDeposit.getId())
                .orElseThrow(() -> new IllegalArgumentException("입금 정보가 없습니다."));

        log.info("조회된 입금 정보: {}", receivedDeposit);

        // Assert 입금 정보 검증
        assertThat(receivedDeposit.getDepositAmount()).isEqualTo(depositAmount);
        assertThat(receivedDeposit.getDepositStatus()).isEqualTo(DepositStatus.COMPLETED);
        assertThat(receivedDeposit.getMessage()).isEqualTo("입금 확인");

        // Assert 출금 계좌 검증
        Account updatedSourceAccount = accountRepository.findById(sourceAccount.getId())
                .orElseThrow(() -> new IllegalArgumentException("출금 계좌를 찾을 수 없습니다."));
        long expectedSourceAmount = sourceAccount.getAmount() - depositAmount;
        assertThat(updatedSourceAccount.getAmount()).isEqualTo(expectedSourceAmount);

        // Assert 입금 계좌 검증
        Account updatedDestinationAccount = accountRepository.findById(destinationAccount.getId())
                .orElseThrow(() -> new IllegalArgumentException("입금 계좌를 찾을 수 없습니다."));
        long expectedDestinationAmount = destinationAccount.getAmount() + depositAmount;
        assertThat(updatedDestinationAccount.getAmount()).isEqualTo(expectedDestinationAmount);

        log.info("입금 후 출금 계좌 잔액: {}", updatedSourceAccount.getAmount());
        log.info("입금 후 입금 계좌 잔액: {}", updatedDestinationAccount.getAmount());
    }
}
