package miniproject.fintech.service.accountservice;

import miniproject.fintech.domain.Account;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.domain.Deposit;
import miniproject.fintech.dto.AccountDto;
import miniproject.fintech.dto.DepositDto;
import miniproject.fintech.repository.AccountRepository;
import miniproject.fintech.repository.MemberRepository;

import miniproject.fintech.type.AccountStatus;
import miniproject.fintech.type.DepositStatus;
import miniproject.fintech.type.Grade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static miniproject.fintech.type.AccountStatus.UNACITVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class AccountServiceImplTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AccountService accountService;

    private static final String ACCOUNT_NUMBER = String.valueOf(UUID.randomUUID());

    private BankMember bankMember;
    private Account account;

    @BeforeEach
    void setUp() {
        // BankMember 객체 생성
        bankMember = BankMember.builder()
                .name("메시")
                .age(38)
                .accountNumber(ACCOUNT_NUMBER)
                .createdAt(LocalDateTime.now())
                .birth(LocalDate.of(1995, 05, 18))
                .email("messi@gmail.com")
                .grade(Grade.VIP)
                .curAmount(10000)
                .accounts(new ArrayList<>())
                .password("MessiGiMoZI")
                .build();

        // BankMember 저장
        this.bankMember = memberRepository.save(bankMember);

        // Account 객체 생성
        Account messiAccount = Account.builder()
                .accountNumber(ACCOUNT_NUMBER)
                .accountStatus(AccountStatus.REGISTER)
                .createdAt(LocalDateTime.now())
                .bankMember(bankMember)  // BankMember와 연관 설정
                .build();

        // Account 저장
        this.account = accountRepository.save(messiAccount);

    }

    @Test
    void save() {
        Account saveAccount = accountRepository.save(account);
        assertThat(saveAccount).isNotNull();
        assertThat(saveAccount.getAccountNumber()).isEqualTo(ACCOUNT_NUMBER);
    }

    @Test
    void findById() {
        Optional<Account> accountId = accountService.findById(account.getId());
        assertThat(accountId).isPresent();
        assertThat(accountId.get().getId()).isEqualTo(account.getId());
    }

    @Test
    void findAll() {
        Account account1 = Account.builder()
                .accountNumber(ACCOUNT_NUMBER)
                .createdAt(LocalDateTime.now())
                .accountStatus(AccountStatus.REGISTER)
                .build();

        Account account2 = Account.builder()
                .accountNumber(ACCOUNT_NUMBER)
                .createdAt(LocalDateTime.now())
                .accountStatus(AccountStatus.REGISTER)
                .build();

        accountRepository.save(account1);
        accountRepository.save(account2);

        List<Account> findAllAccount = accountRepository.findAll();

        assertThat(findAllAccount).hasSize(3);
        assertThat(findAllAccount).contains(account1, account2);
    }

    @Test
    void create() {
        DepositDto depositDto = DepositDto.builder()
                .depositAmount(10000)
                .depositAt(LocalDateTime.now())
                .depositStatus(DepositStatus.COMPLETED)
                .message("deposit received")
                .build();

        Account account = Account.builder()
                .accountStatus(AccountStatus.REGISTER)
                .amount(10000)
                .accountNumber("1234567890")
                .deposits(Set.of(
                        Deposit.builder()
                                .depositAmount(depositDto.getDepositAmount())
                                .depositAt(depositDto.getDepositAt())
                                .depositStatus(depositDto.getDepositStatus())
                                .message(depositDto.getMessage())
                                .build()
                ))
                .build();

        Account savedAccount = accountService.save(account);
        assertNotNull(savedAccount, "saved account should not be null");
        assertEquals("1234567890", savedAccount.getAccountNumber());
        assertEquals(1, savedAccount.getDeposits().size());

        Deposit savedDeposit = savedAccount.getDeposits().iterator().next();
        assertEquals(depositDto.getDepositAmount(), savedDeposit.getDepositAmount());
        assertEquals(depositDto.getDepositStatus(), savedDeposit.getDepositStatus());
        assertEquals(depositDto.getMessage(), savedDeposit.getMessage(), "Deposit message should match");
    }

    @Test
    @Transactional
    void delete() {
        assertThat(accountRepository.findById(account.getId())).isPresent();
        accountService.delete(account.getId());

        // Then: 계좌가 삭제되었는지 확인
        Optional<Account> deletedAccount = accountRepository.findById(account.getId());
        assertThat(deletedAccount).isNotPresent();  // 계좌가 존재하지 않아야 함

        // BankMember의 계좌 목록에서 삭제된 계좌가 제거되었는지 확인
        BankMember updatedBankMember = memberRepository.findById(bankMember.getId())
                .orElseThrow(() -> new IllegalArgumentException("BankMember does not exist"));

        // 계좌 목록이 초기화되지 않았는지 확인
        assertThat(updatedBankMember.getAccounts()).isNotNull();

        boolean isAccountPresent = updatedBankMember.getAccounts().stream()
                .anyMatch(account -> account.getAccountNumber().equals(ACCOUNT_NUMBER));
        assertThat(isAccountPresent).isFalse();
    }

    @Test
    void updateAccount() {
        //given
        Account existingAccount = Account.builder()
                .accountNumber(ACCOUNT_NUMBER)
                .deposits(new HashSet<>())
                .transactions(new HashSet<>())
                .receivedTransfers(new HashSet<>())
                .accountStatus(AccountStatus.REGISTER)
                .amount(10000)
                .createdAt(LocalDateTime.now())
                .build();
        Account savedAccount = accountRepository.save(existingAccount);

        //when
        AccountDto accountDto = AccountDto.builder()
                .id(savedAccount.getId())
                .amount(20000)
                .accountStatus(UNACITVE)
                .transactions(new ArrayList<>())
                .deposits(new ArrayList<>())
                .receivedTransfers(new ArrayList<>())
                .build();

        accountService.updateAccount(savedAccount.getId(), accountDto);

        Account updatedAccount = accountRepository.findById(savedAccount.getId())
                .orElseThrow(() -> new IllegalArgumentException("Account Not Found"));

        assertThat(updatedAccount.getAmount()).isEqualTo(20000);
        assertThat(updatedAccount.getAccountStatus()).isEqualTo(UNACITVE);

        assertThat(updatedAccount.getTransactions()).isNotNull();
        assertThat(updatedAccount.getDeposits()).isEmpty();
        assertThat(updatedAccount.getReceivedTransfers()).isEmpty();
    }
}