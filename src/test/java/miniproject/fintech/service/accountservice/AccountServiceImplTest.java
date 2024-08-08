package miniproject.fintech.service.accountservice;

import miniproject.fintech.domain.Account;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.repository.accountrepository.AccountRepository;
import miniproject.fintech.repository.memberrepository.MemberRepository;
import miniproject.fintech.type.AccountStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class AccountServiceImplTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AccountService accountService;

    private static final String ACCOUNT_NUMBER = String.valueOf(UUID.randomUUID());

    @Test
    void save() {
        Account account = Account.builder()
                .accountNumber(ACCOUNT_NUMBER)
                .createdAt(LocalDateTime.now())
                .accountStatus(AccountStatus.RESISTER)
                .build();

        Account saveAccount = accountRepository.save(account);
        assertThat(saveAccount).isNotNull();
        assertThat(saveAccount.getAccountNumber()).isEqualTo(ACCOUNT_NUMBER);
    }

    @Test
    void findById() {
        Account account = Account.builder()
                .accountNumber(ACCOUNT_NUMBER)
                .createdAt(LocalDateTime.now())
                .accountStatus(AccountStatus.RESISTER)
                .build();

        Account saveAccount = accountRepository.save(account);
        Optional<Account> findAccountId = accountRepository.findById(saveAccount.getId());

        assertThat(findAccountId).isPresent();
        assertThat(findAccountId.get()).isEqualTo(saveAccount);
    }

    @Test
    void findAll() {
        Account account1 = Account.builder()
                .accountNumber(ACCOUNT_NUMBER)
                .createdAt(LocalDateTime.now())
                .accountStatus(AccountStatus.RESISTER)
                .build();

        Account account2 = Account.builder()
                .accountNumber(ACCOUNT_NUMBER)
                .createdAt(LocalDateTime.now())
                .accountStatus(AccountStatus.RESISTER)
                .build();

        accountRepository.save(account1);
        accountRepository.save(account2);

        List<Account> findAllAccount = accountRepository.findAll();

        assertThat(findAllAccount).hasSize(2);
        assertThat(findAllAccount).contains(account1, account2);
    }

    @Test
    void create() {
        BankMember bankMember = BankMember.builder()
                .name("벨링엄")
                .build();
        memberRepository.save(bankMember);

        Account account = Account.builder()
                .accountNumber(ACCOUNT_NUMBER)
                .createdAt(LocalDateTime.now())
                .accountStatus(AccountStatus.RESISTER)
                .build();

        accountService.create(bankMember, account);

        BankMember updatedBankMember = memberRepository.findById(bankMember.getId())
                .orElseThrow(() -> new IllegalArgumentException("BankMember does not exist"));

        assertThat(updatedBankMember.getAccounts())
                .extracting(Account::getAccountNumber)
                .contains(ACCOUNT_NUMBER);

        Optional<Account> savedAccount = accountRepository.findById(account.getId());
        assertThat(savedAccount).isPresent();
        assertThat(savedAccount.get()).isEqualTo(account);
    }

    @Test
    void delete() {
        // BankMember를 생성하고 저장합니다.
        BankMember bankMember = BankMember.builder()
                .name("벨링엄")
                .build();
        memberRepository.save(bankMember);

        // Account를 생성하고 저장합니다.
        Account account = Account.builder()
                .createdAt(LocalDateTime.now())
                .accountStatus(AccountStatus.RESISTER)
                .bankMember(bankMember)  // BankMember와 연결합니다.
                .build();

        // 계좌를 저장하고, BankMember의 계좌 목록에 추가합니다.
        accountService.save(account);

        // BankMember를 다시 조회합니다.
        BankMember member = memberRepository.findById(bankMember.getId())
                .orElseThrow(() -> new IllegalArgumentException("BankMember does not exist"));

        // Account 삭제
        accountService.delete(bankMember.getId());

        // Account가 삭제되었는지 확인합니다.
        Optional<Account> deletedAccount = accountRepository.findById(account.getId());
        assertThat(deletedAccount).isNotPresent();  // Account가 존재하지 않아야 합니다.

        // BankMember의 계좌 목록에서 Account가 제거되었는지 확인합니다.
        BankMember updatedBankMember = memberRepository.findById(bankMember.getId())
                .orElseThrow(() -> new IllegalArgumentException("BankMember does not exist"));

        assertThat(updatedBankMember.getAccounts())
                .extracting(Account::getAccountNumber)
                .doesNotContain(ACCOUNT_NUMBER);  // 계좌 목록에 삭제된 계좌가 없어야 합니다.
    }
}