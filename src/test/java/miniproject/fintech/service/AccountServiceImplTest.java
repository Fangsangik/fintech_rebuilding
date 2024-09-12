package miniproject.fintech.service;

import miniproject.fintech.controller.AccountController;
import miniproject.fintech.domain.Account;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.dto.*;
import miniproject.fintech.error.CustomError;
import miniproject.fintech.repository.AccountRepository;
import miniproject.fintech.repository.MemberRepository;

import miniproject.fintech.type.AccountStatus;
import miniproject.fintech.type.Grade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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

    @Autowired
    private EntityConverter entityConverter;

    @Autowired
    private AccountController accountController;

    private static final String ACCOUNT_NUMBER = String.valueOf(UUID.randomUUID());
    private BankMemberDto bankMemberDto;
    private AccountDto accountDto;

    @BeforeEach
    void setUp() {
        memberRepository.deleteAll();
        accountRepository.deleteAll();

        // BankMember 객체 생성
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

        bankMemberDto = dtoConverter.convertToBankMemberDto(bankMember);

        // Account 객체 생성
        Account messiAccount = Account.builder()
                .accountNumber(ACCOUNT_NUMBER)
                .accountStatus(AccountStatus.REGISTER)
                .createdAt(LocalDateTime.now())
                .bankMember(bankMember)  // BankMember와 연관 설정
                .build();

        // Account 저장
        accountDto = dtoConverter.convertToAccountDto(accountRepository.save(messiAccount));
    }

    @Test
    void findByAccountNumber() {
        Optional<AccountDto> accountId = accountService.findByAccountNumber(accountDto.getAccountNumber());
        assertThat(accountId).isPresent();
        assertThat(accountId.get().getId()).isEqualTo(accountDto.getId());
    }

    @Test
    void findAll() {
        Account account1 = Account.builder()
                .accountNumber(UUID.randomUUID().toString()) // 고유한 accountNumber 생성
                .createdAt(LocalDateTime.now())
                .accountStatus(AccountStatus.REGISTER)
                .bankMember(memberRepository.findByUserId(bankMemberDto.getUserId()).orElseThrow())
                .build();

        Account account2 = Account.builder()
                .accountNumber(UUID.randomUUID().toString()) // 고유한 accountNumber 생성
                .createdAt(LocalDateTime.now())
                .accountStatus(AccountStatus.REGISTER)
                .bankMember(memberRepository.findByUserId(bankMemberDto.getUserId()).orElseThrow())
                .build();

        accountRepository.save(account1);
        accountRepository.save(account2);

        List<Account> findAllAccount = accountRepository.findAll();

        assertThat(findAllAccount).hasSize(3);
        assertThat(findAllAccount).contains(account1, account2);
    }

    @Test
    void createAccountTest() {
        // 동일한 userId를 사용하지 않도록 유니크한 userId 생성
        String uniqueUserId = "test" + UUID.randomUUID().toString().substring(0, 5);

        BankMember bankMember = memberRepository.save(BankMember.builder()
                .userId(uniqueUserId)
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

        bankMemberDto = dtoConverter.convertToBankMemberDto(bankMember);

        // Account 객체 생성
        Account messiAccount = Account.builder()
                .accountNumber(ACCOUNT_NUMBER)
                .accountStatus(AccountStatus.REGISTER)
                .createdAt(LocalDateTime.now())
                .bankMember(bankMember)
                .build();

        // Account 저장
        accountDto = dtoConverter.convertToAccountDto(accountRepository.save(messiAccount));
        accountRepository.deleteAll();

        // When: 계좌를 생성
        AccountDto createdAccount = accountService.createAccountForMember(accountDto, bankMemberDto.getUserId());

        // Then: 계좌가 성공적으로 생성되었는지 확인
        assertNotNull(createdAccount.getId());
        assertEquals(createdAccount.getAccountNumber(), accountDto.getAccountNumber());
    }

    @Test
    void delete() {
        assertThat(accountRepository.findByAccountNumber(accountDto.getAccountNumber())).isPresent();
        accountService.delete(accountDto.getAccountNumber());

        // Then: 계좌가 삭제되었는지 확인
        Optional<Account> deletedAccount = accountRepository.findById(accountDto.getId());
        assertThat(deletedAccount).isNotPresent();

        // BankMember의 계좌 목록에서 삭제된 계좌가 제거되었는지 확인
        BankMember updatedBankMember = memberRepository.findById(bankMemberDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("BankMember does not exist"));

        boolean isAccountPresent = updatedBankMember.getAccounts().stream()
                .anyMatch(account -> account.getAccountNumber().equals(ACCOUNT_NUMBER));
        assertThat(isAccountPresent).isFalse();
    }

    @Test
    void updateAccount() {
        //given
        Account existingAccount = Account.builder()
                .accountNumber(UUID.randomUUID().toString()) // 고유한 accountNumber 생성
                .deposits(new ArrayList<>())
                .transactions(new ArrayList<>())
                .accountStatus(AccountStatus.REGISTER)
                .amount(10000)
                .createdAt(LocalDateTime.now())
                .bankMember(memberRepository.findByUserId(bankMemberDto.getUserId()).orElseThrow()) // 연관 설정
                .build();
        Account savedAccount = accountRepository.save(existingAccount);

        //when
        AccountDto accountDto = AccountDto.builder()
                .id(savedAccount.getId())
                .amount(20000)
                .accountStatus(AccountStatus.UN_ACTIVE)
                .build();

        accountService.updateAccount(savedAccount.getAccountNumber(), accountDto);

        Account updatedAccount = accountRepository.findById(savedAccount.getId())
                .orElseThrow(() -> new IllegalArgumentException("Account Not Found"));

        assertThat(updatedAccount.getAmount()).isEqualTo(20000);
        assertThat(updatedAccount.getAccountStatus()).isEqualTo(AccountStatus.UN_ACTIVE);
        assertThat(updatedAccount.getTransactions()).isNotNull();
        assertThat(updatedAccount.getDeposits()).isEmpty();
    }

    @Test
    void createAccount_NullAccountDto_ShouldThrowCustomError() {
        // Given: null AccountDto
        CreateAccountRequest request = new CreateAccountRequest();
        request.setAccountDto(null);
        request.setBankMemberDto(request.getBankMemberDto());

        // When & Then: 예외 발생 확인
        CustomError exception = assertThrows(CustomError.class, () -> {
            accountController.createAccount(request);
        });

        assertEquals("존재하지 않는 계좌입니다.", exception.getMessage());
    }

    @Test
    void createAccount_NullBankMemberDto_ShouldThrowCustomError() {
        // Given: null BankMemberDto
        CreateAccountRequest request = new CreateAccountRequest();
        request.setAccountDto(request.getAccountDto());
        request.setBankMemberDto(null);

        // When & Then: 예외 발생 확인
        CustomError exception = assertThrows(CustomError.class, () -> {
            accountController.createAccount(request);
        });

        assertEquals("존재하지 않는 회원입니다.", exception.getMessage());
    }

}
