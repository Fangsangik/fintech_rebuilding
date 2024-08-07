package miniproject.fintech.service.accountservice;

import miniproject.fintech.config.AccountConfig;
import miniproject.fintech.domain.Account;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.repository.accountrepository.JpaAccountRepository;
import miniproject.fintech.repository.memberrepository.JpaMemberRepository;
import miniproject.fintech.type.AccountStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AccountServiceImplTest {

    @Autowired
    private JpaAccountRepository accountRepository;

    @Autowired
    private JpaMemberRepository memberRepository;

    @Autowired
    private AccountService accountService;

    @Test
    void save() {
        long accountNumber = System.currentTimeMillis();
        Account account = Account.builder()
                .accountNumber(accountNumber)
                .bankName("신한은행")
                .createdAt(LocalDateTime.now())
                .password("123456789")
                .accountStatus(AccountStatus.RESISTER)
                .build();

        Account saveAccount = accountRepository.save(account);
        assertThat(saveAccount).isNotNull();
        assertThat(saveAccount.getBankName()).isEqualTo("신한은행");
        assertThat(saveAccount.getAccountNumber()).isEqualTo(accountNumber);
        assertThat(saveAccount.getPassword()).isEqualTo("123456789");
    }

    @Test
    void findById() {
        long accountNumber = System.currentTimeMillis();
        Account account = Account.builder()
                .accountNumber(accountNumber)
                .bankName("신한은행")
                .createdAt(LocalDateTime.now())
                .password("123456789")
                .accountStatus(AccountStatus.RESISTER)
                .build();

        Account saveAccount = accountRepository.save(account);
        Optional<Account> findAccountId = accountRepository.findById(saveAccount.getId());

        assertThat(findAccountId).isPresent();
        assertThat(findAccountId.get()).isEqualTo(saveAccount);
    }

    @Test
    void findAll() {
        long accountNumber1 = System.currentTimeMillis();
        Account account1 = Account.builder()
                .accountNumber(accountNumber1)
                .bankName("신한은행")
                .createdAt(LocalDateTime.now())
                .password("123456789")
                .accountStatus(AccountStatus.RESISTER)
                .build();

        long accountNumber2 = System.currentTimeMillis();
        Account account2 = Account.builder()
                .accountNumber(accountNumber2)
                .bankName("국민은행")
                .createdAt(LocalDateTime.now())
                .password("5555555")
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

        long accountNumber = System.currentTimeMillis();
        Account account = Account.builder()
                .accountNumber(accountNumber)
                .bankName("신한은행")
                .createdAt(LocalDateTime.now())
                .password("123456789")
                .accountStatus(AccountStatus.RESISTER)
                .build();

        accountService.create(bankMember, account);

        BankMember updatedBankMember = memberRepository.findById(bankMember.getId())
                .orElseThrow(() -> new IllegalArgumentException("BankMember does not exist"));

        assertThat(updatedBankMember.getAccounts())
                .extracting(Account::getAccountNumber)
                .contains(accountNumber);

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
        long accountNumber = System.currentTimeMillis();
        Account account = Account.builder()
                .accountNumber(accountNumber)
                .bankName("신한은행")
                .createdAt(LocalDateTime.now())
                .password("123456789")
                .accountStatus(AccountStatus.RESISTER)
                .bankMember(bankMember)  // BankMember와 연결합니다.
                .build();

        // 계좌를 저장하고, BankMember의 계좌 목록에 추가합니다.
        accountService.save(account);

        // BankMember를 다시 조회합니다.
        BankMember member = memberRepository.findById(bankMember.getId())
                .orElseThrow(() -> new IllegalArgumentException("BankMember does not exist"));

        // Account 삭제
        accountService.delete(bankMember, accountNumber);

        // Account가 삭제되었는지 확인합니다.
        Optional<Account> deletedAccount = accountRepository.findById(account.getId());
        assertThat(deletedAccount).isNotPresent();  // Account가 존재하지 않아야 합니다.

        // BankMember의 계좌 목록에서 Account가 제거되었는지 확인합니다.
        BankMember updatedBankMember = memberRepository.findById(bankMember.getId())
                .orElseThrow(() -> new IllegalArgumentException("BankMember does not exist"));

        assertThat(updatedBankMember.getAccounts())
                .extracting(Account::getAccountNumber)
                .doesNotContain(accountNumber);  // 계좌 목록에 삭제된 계좌가 없어야 합니다.
    }
}