package miniproject.fintech.service.transactionservice.depositService;

import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.Account;
import miniproject.fintech.domain.Deposit;
import miniproject.fintech.dto.DepositDto;
import miniproject.fintech.repository.AccountRepository;
import miniproject.fintech.repository.DepositRepository;
import miniproject.fintech.service.accountservice.AccountService;
import miniproject.fintech.type.AccountStatus;
import miniproject.fintech.type.DepositStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static miniproject.fintech.type.DepositStatus.COMPLETED;
import static org.assertj.core.api.Assertions.*;


@Slf4j
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) //인메모리 사용
class DepositServiceImplTest {
    @Autowired
    private DepositService depositService;

    @Autowired
    private DepositRepository depositRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    private Account depositAccount;

    @BeforeEach
    void setUp() {
        depositAccount = Account.builder()
                .accountNumber("source123")
                .amount(10000)
                .accountStatus(AccountStatus.REGISTER)
                .createdAt(LocalDateTime.now())
                .build();

        accountRepository.save(depositAccount);
    }

    @Test
    void depositTest() {
        // Given: 입금 설정
        long depositAmount = 20000;
        DepositDto depositDto = DepositDto.builder()
                .depositAmount(depositAmount)
                .depositAt(LocalDateTime.now())
                .depositStatus(DepositStatus.COMPLETED)  // 수정된 열거형 값
                .accountId(depositAccount.getId())
                .build();

        log.info("입금 처리 시작: 입금액 {} 계좌 ID: {}", depositAmount, depositDto.getAccountId());

        // When: 입금 처리
        Deposit savedDeposit = depositService.processDeposit(depositDto);

        log.info("입금 처리 완료. 저장된 입금 ID: {}", savedDeposit.getId());

        // Then: 입금 및 계좌 업데이트 확인
        Deposit receivedDeposit = depositRepository.findById(savedDeposit.getId())
                .orElseThrow(() -> new IllegalArgumentException("입금 정보가 없습니다."));

        log.info("조회된 입금 정보: {}", receivedDeposit);

        assertThat(receivedDeposit.getDepositAmount()).isEqualTo(depositAmount);
        assertThat(receivedDeposit.getDepositStatus()).isEqualTo(DepositStatus.COMPLETED);
        assertThat(receivedDeposit.getMessage()).isEqualTo("입금 확인");

        Account updatedDepositAccount = accountRepository.findById(depositAccount.getId())
                .orElseThrow(() -> new IllegalArgumentException("입금 계좌를 찾을 수 없습니다."));

        log.info("업데이트된 입금 계좌: {}", updatedDepositAccount);

        long expectedDepositAmount = depositAccount.getAmount() + depositAmount;

        assertThat(updatedDepositAccount.getAmount()).isEqualTo(expectedDepositAmount);

        // 총 잔액 확인
        long totalAmount = accountRepository.findById(depositAccount.getId())
                .orElseThrow(() -> new IllegalArgumentException("입금 계좌를 찾을 수 없습니다.")).getAmount();

        log.info("입금 후 계좌 총 잔액: {}", totalAmount);
    }
}