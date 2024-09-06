package miniproject.fintech.service;


import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.Account;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.domain.Transfer;
import miniproject.fintech.dto.*;
import miniproject.fintech.error.CustomError;
import miniproject.fintech.repository.AccountRepository;
import miniproject.fintech.repository.MemberRepository;
import miniproject.fintech.repository.TransactionRepository;
import miniproject.fintech.repository.TransferRepository;
import miniproject.fintech.type.AccountStatus;
import miniproject.fintech.type.ErrorType;
import miniproject.fintech.type.Grade;
import miniproject.fintech.type.TransferStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;


import static miniproject.fintech.type.ErrorType.*;
import static org.assertj.core.api.Assertions.*;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

@Slf4j
@SpringBootTest
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TransferServiceImplTest {

    @Autowired
    private TransferServiceImpl transferService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private DtoConverter dtoConverter;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EntityConverter entityConverter;

    @Autowired
    private TransferRepository transferRepository;

    private AccountDto sourceAccount;
    private AccountDto destinationAccount;
    private BankMemberDto bankMemberDto;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
        transferRepository.deleteAll();

        BankMember bankMember = memberRepository.save(BankMember.builder()
                .userId("test" + UUID.randomUUID().toString().substring(0, 5)) // 고유한 userId 생성
                .name("메시")
                .age(38)
                .createdAt(LocalDateTime.now())
                .birth(LocalDate.of(1995, 5, 18))
                .email("messi@gmail.com")
                .grade(Grade.VIP)
                .curAmount(10000)
                .accounts(new ArrayList<>())
                .password("MessiGiMoZI")
                .build());

        this.bankMemberDto = dtoConverter.convertToBankMemberDto(bankMember);


        // DTO -> 엔티티로 변환 후 데이터베이스에 저장
        Account sourceAccountEntity = accountRepository.save(Account.builder()
                .bankMember(bankMember)
                .accountNumber("source123")
                .amount(10000)
                .accountStatus(AccountStatus.REGISTER)
                .createdAt(LocalDateTime.now())
                .build());

        Account destinationAccountEntity = accountRepository.save(Account.builder()
                .bankMember(bankMember)
                .accountNumber("dest123")
                .amount(5000)
                .accountStatus(AccountStatus.REGISTER)
                .createdAt(LocalDateTime.now())
                .build());

        this.sourceAccount = dtoConverter.convertToAccountDto(sourceAccountEntity);
        this.destinationAccount = dtoConverter.convertToAccountDto(destinationAccountEntity);

        assertNotNull(sourceAccountEntity.getId(), "출금 계좌 ID가 생성되지 않았습니다.");
        assertNotNull(destinationAccountEntity.getId(), "입금 계좌 ID가 생성되지 않았습니다.");
    }

    @Test
    void transferTest() {
        // Given: 송금 설정
        long transferAmount = 2000;
        TransferDto transferDto = TransferDto.builder()
                .transferAmount(transferAmount)
                .transferAt(LocalDateTime.now())
                .sourceAccountNumber(sourceAccount.getAccountNumber())
                .destinationAccountNumber(destinationAccount.getAccountNumber())
                .build();

        // When: 송금 처리
        TransferDto savedTransfer = transferService.processTransfer(transferDto);

        // Then: 송금이 성공적으로 저장되었는지 검증
        assertNotNull(savedTransfer.getId(), "송금 ID가 생성되지 않았습니다.");
        assertThat(savedTransfer.getTransferAmount()).isEqualTo(transferAmount);
        assertThat(savedTransfer.getSourceAccountNumber()).isEqualTo(sourceAccount.getAccountNumber());
        assertThat(savedTransfer.getDestinationAccountNumber()).isEqualTo(destinationAccount.getAccountNumber());
        assertThat(savedTransfer.getTransferStatus()).isEqualTo(TransferStatus.COMPLETED);

        // 추가 검증: 송금 엔티티가 데이터베이스에 저장되었는지 확인
        Transfer savedTransferEntity = transferRepository.findById(savedTransfer.getId())
                .orElseThrow(() -> new CustomError(TRANSFER_NOT_FOUND));

        assertThat(savedTransferEntity.getTransferAmount()).isEqualTo(transferAmount);
    }
}
