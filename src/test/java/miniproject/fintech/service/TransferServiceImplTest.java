package miniproject.fintech.service;


import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.Account;
import miniproject.fintech.domain.Transfer;
import miniproject.fintech.dto.DtoConverter;
import miniproject.fintech.dto.EntityConverter;
import miniproject.fintech.dto.TransferDto;
import miniproject.fintech.error.CustomError;
import miniproject.fintech.repository.AccountRepository;
import miniproject.fintech.repository.MemberRepository;
import miniproject.fintech.repository.TransactionRepository;
import miniproject.fintech.repository.TransferRepository;
import miniproject.fintech.type.AccountStatus;
import miniproject.fintech.type.ErrorType;
import miniproject.fintech.type.TransferStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;


import static miniproject.fintech.type.ErrorType.*;
import static org.assertj.core.api.Assertions.*;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

@Slf4j
@SpringBootTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class TransferServiceImplTest {

    @Autowired
    private TransferServiceImpl transferService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransferRepository transferRepository;
    private Account sourceAccount;
    private Account destinationAccount;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
        transferRepository.deleteAll();

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

        // 데이터베이스에 저장 후 ID가 있는 엔티티로 설정
        sourceAccount = accountRepository.save(sourceAccount);
        destinationAccount = accountRepository.save(destinationAccount);

        assertNotNull(sourceAccount.getId(), "출금 계좌 ID가 생성되지 않았습니다.");
        assertNotNull(destinationAccount.getId(), "입금 계좌 ID가 생성되지 않았습니다.");
    }

    @Test
    @Transactional
    void transferTest() {
        // Given: 송금 설정
        long transferAmount = 2000;
        TransferDto transferDto = TransferDto.builder()
                .transferAmount(transferAmount)
                .transferAt(LocalDateTime.now())
                .sourceAccountId(sourceAccount.getId())
                .destinationAccountId(destinationAccount.getId())
                .build();

        // When: 송금 처리
        Transfer savedTransfer = transferService.processTransfer(transferDto);
        transferRepository.save(savedTransfer);

        // Then: 송금이 성공적으로 저장되었는지 검증
        assertNotNull(savedTransfer.getId(), "송금 ID가 생성되지 않았습니다.");
        assertThat(savedTransfer.getTransferAmount()).isEqualTo(transferAmount);
        assertThat(savedTransfer.getSourceAccount().getId()).isEqualTo(sourceAccount.getId());
        assertThat(savedTransfer.getDestinationAccount().getId()).isEqualTo(destinationAccount.getId());
        assertThat(savedTransfer.getTransferStatus()).isEqualTo(TransferStatus.COMPLETED);

        // 계좌 업데이트 확인
        Account updatedSourceAccount = accountRepository.findById(sourceAccount.getId())
                .orElseThrow(() -> new CustomError(SOURCE_ID_NOT_FOUND));
        Account updatedDestinationAccount = accountRepository.findById(destinationAccount.getId())
                .orElseThrow(() -> new CustomError(DESTINATION_ID_NOT_FOUND));

        assertThat(updatedSourceAccount.getAmount()).isEqualTo(8000); // 10000 - 2000
        assertThat(updatedDestinationAccount.getAmount()).isEqualTo(7000); // 5000 + 2000
    }
}