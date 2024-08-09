package miniproject.fintech.service.transactionservice.transferservice;


import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.Account;
import miniproject.fintech.domain.Transfer;
import miniproject.fintech.dto.TransferDto;
import miniproject.fintech.repository.AccountRepository;
import miniproject.fintech.repository.TransferRepository;
import miniproject.fintech.type.AccountStatus;
import miniproject.fintech.type.TransferStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;


import static org.assertj.core.api.Assertions.*;

@Slf4j
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class TransferServiceImplTest {


    @Autowired
    private TransferService transferService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransferRepository transferRepository;

    private Account sourceAccount;
    private Account destinationAccount;

    @BeforeEach
    void setUp() {
        sourceAccount = Account.builder()
                .accountNumber("source123")
                .amount(10000)
                .accountStatus(AccountStatus.REGISTER)
                .createdAt(LocalDateTime.now())
                .build();
        destinationAccount = Account.builder()
                .accountNumber("dest123")
                .amount(5000)
                .accountStatus(AccountStatus.REGISTER)
                .createdAt(LocalDateTime.now())
                .build();

        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);

        log.info("설정 완료: 출금 계좌 ID: {}, 잔액: {}", sourceAccount.getId(), sourceAccount.getAmount());
        log.info("설정 완료: 입금 계좌 ID: {}, 잔액: {}", destinationAccount.getId(), destinationAccount.getAmount());
    }

    @Test
    void transferTest() {
        // Given: 송금 설정
        long transferAmount = 2000;
        TransferDto transferDto = TransferDto.builder()
                .transferAmount(transferAmount)
                .transferAt(LocalDateTime.now())
                .sourceAccountId(sourceAccount.getId())
                .destinationAccountId(destinationAccount.getId())
                .build();

        log.info("송금 처리 시작: 송금액 {} 출금 계좌 ID: {} 입금 계좌 ID: {}",
                transferAmount, sourceAccount.getId(), destinationAccount.getId());

        // When: 송금 처리
        Transfer savedTransfer = transferService.processTransfer(transferDto);

        log.info("송금 처리 완료. 저장된 송금 ID: {}", savedTransfer.getId());

        // Then: 송금 및 계좌 업데이트 확인
        Transfer receivedTransfer = transferRepository.findById(savedTransfer.getId())
                .orElseThrow(() -> new IllegalArgumentException("송금 정보가 없습니다."));

        log.info("조회된 송금 정보: {}", receivedTransfer);

        assertThat(receivedTransfer.getTransferAmount()).isEqualTo(transferAmount);
        assertThat(receivedTransfer.getTransferStatus()).isEqualTo(TransferStatus.COMPLETED);
        assertThat(receivedTransfer.getMessage()).isEqualTo("송금이 완료되었습니다.");

        Account updatedSourceAccount = accountRepository.findById(sourceAccount.getId())
                .orElseThrow(() -> new IllegalArgumentException("출금 계좌를 찾을 수 없습니다."));
        Account updatedDestinationAccount = accountRepository.findById(destinationAccount.getId())
                .orElseThrow(() -> new IllegalArgumentException("입금 계좌를 찾을 수 없습니다."));

        log.info("업데이트된 출금 계좌: {}", updatedSourceAccount);
        log.info("업데이트된 입금 계좌: {}", updatedDestinationAccount);

        long expectedSourceAmount = sourceAccount.getAmount() - transferAmount;
        long expectedDestinationAmount = destinationAccount.getAmount() + transferAmount;

        assertThat(updatedSourceAccount.getAmount()).isEqualTo(expectedSourceAmount);
        assertThat(updatedDestinationAccount.getAmount()).isEqualTo(expectedDestinationAmount);

        // 총 잔액 확인
        long totalSourceAmount = accountRepository.findById(sourceAccount.getId())
                .orElseThrow(() -> new IllegalArgumentException("출금 계좌를 찾을 수 없습니다.")).getAmount();
        long totalDestinationAmount = accountRepository.findById(destinationAccount.getId())
                .orElseThrow(() -> new IllegalArgumentException("입금 계좌를 찾을 수 없습니다.")).getAmount();

        log.info("송금 후 출금 계좌 총 잔액: {}", totalSourceAmount);
        log.info("송금 후 입금 계좌 총 잔액: {}", totalDestinationAmount);
    }
}