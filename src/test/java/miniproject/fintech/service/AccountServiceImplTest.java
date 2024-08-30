package miniproject.fintech.service;

import miniproject.fintech.domain.Account;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.dto.*;
import miniproject.fintech.repository.AccountRepository;
import miniproject.fintech.repository.MemberRepository;

import miniproject.fintech.type.AccountStatus;
import miniproject.fintech.type.Grade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static miniproject.fintech.type.AccountStatus.UN_ACTIVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
//@Transactional
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class AccountServiceImplTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemoryMemberService memberService;

    @Autowired
    private AccountServiceImpl accountService;

    @Autowired
    private DtoConverter dtoConverter;


    private static final String ACCOUNT_NUMBER = String.valueOf(UUID.randomUUID());

    private BankMemberDto bankMemberDto;
    private AccountDto accountDto;

    @BeforeEach
    void setUp() {
        // BankMember 객체 생성
        BankMember bankMember = memberRepository.save(BankMember.builder()
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
                .build());

        bankMemberDto = dtoConverter.convertToBankMemberDto(bankMember);

        // Account 객체 생성
        Account messiAccount = accountRepository.save(Account.builder()
                .accountNumber(ACCOUNT_NUMBER)
                .accountStatus(AccountStatus.REGISTER)
                .createdAt(LocalDateTime.now())
                .bankMember(bankMember)  // BankMember와 연관 설정
                .build());

        // Account 저장
        accountDto = dtoConverter.convertToAccountDto(accountRepository.save(messiAccount));

    }

    @Test
    @Transactional
    void findById() {
        Optional<AccountDto> accountId = accountService.findById(accountDto.getId());
        assertThat(accountId).isPresent();
        assertThat(accountId.get().getId()).isEqualTo(accountDto.getId());
    }

    @Test
    @Transactional
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
    @Transactional
    void createAccountTest() {

        accountRepository.deleteAll();
        // When: 계좌를 생성
        AccountDto createdAccount = accountService.createAccountForMember(accountDto, bankMemberDto.getId());

        // Then: 계좌가 성공적으로 생성되었는지 확인
        assertNotNull(createdAccount.getId());
        assertEquals(bankMemberDto.getAccountNumber(), createdAccount.getAccountNumber());
    }

    @Test
    @Transactional
    void delete() {
        assertThat(accountRepository.findById(accountDto.getId())).isPresent();
        accountService.delete(accountDto.getId());

        // Then: 계좌가 삭제되었는지 확인
        Optional<Account> deletedAccount = accountRepository.findById(accountDto.getId());
        assertThat(deletedAccount).isNotPresent();  // 계좌가 존재하지 않아야 함

        // BankMember의 계좌 목록에서 삭제된 계좌가 제거되었는지 확인
        BankMember updatedBankMember = memberRepository.findById(bankMemberDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("BankMember does not exist"));

        // 계좌 목록이 초기화되지 않았는지 확인
        assertThat(updatedBankMember.getAccounts()).isNotNull();

        boolean isAccountPresent = updatedBankMember.getAccounts().stream()
                .anyMatch(account -> account.getAccountNumber().equals(ACCOUNT_NUMBER));
        assertThat(isAccountPresent).isFalse();
    }

    @Test
    @Transactional
    void updateAccount() {
        //given
        Account existingAccount = Account.builder()
                .accountNumber(ACCOUNT_NUMBER)
                .deposits(new ArrayList<>())
                .transactions(new ArrayList<>())
                .receivedTransfers(new ArrayList<>())
                .accountStatus(AccountStatus.REGISTER)
                .amount(10000)
                .createdAt(LocalDateTime.now())
                .build();
        Account savedAccount = accountRepository.save(existingAccount);

        //when
        AccountDto accountDto = AccountDto.builder()
                .id(savedAccount.getId())
                .amount(20000)
                .accountStatus(UN_ACTIVE)
                .transactions(new ArrayList<>())
                .deposits(new ArrayList<>())
                .receivedTransfers(new ArrayList<>())
                .build();

        accountService.updateAccount(savedAccount.getId(), accountDto);

        Account updatedAccount = accountRepository.findById(savedAccount.getId())
                .orElseThrow(() -> new IllegalArgumentException("Account Not Found"));

        assertThat(updatedAccount.getAmount()).isEqualTo(20000);
        assertThat(updatedAccount.getAccountStatus()).isEqualTo(UN_ACTIVE);

        assertThat(updatedAccount.getTransactions()).isNotNull();
        assertThat(updatedAccount.getDeposits()).isEmpty();
        assertThat(updatedAccount.getReceivedTransfers()).isEmpty();
    }
}