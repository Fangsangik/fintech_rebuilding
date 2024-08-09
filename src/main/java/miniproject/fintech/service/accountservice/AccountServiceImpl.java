package miniproject.fintech.service.accountservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.Account;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.dto.AccountDto;
import miniproject.fintech.repository.AccountRepository;
import miniproject.fintech.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final MemberRepository memberRepository;
    private final AccountRepository accountRepository;


    @Override
    public Account save(Account account) {
        return accountRepository.save(account);
    }

    @Override
    public Optional<Account> findById(Long id) {
        return accountRepository.findById(id);
    }

    @Override
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override
    @Transactional
    public Account createdByBankMember(BankMember bankMember, AccountDto accountDto) {
        validationCheckMember(bankMember, accountDto);

        Account account = Account.builder()
                .accountNumber(accountDto.getAccountNumber())
                .amount(accountDto.getAmount())
                .accountStatus(accountDto.getAccountStatus())
                .build();

        return accountRepository.save(account);
    }

    private BankMember validationCheckMember(BankMember bankMember, AccountDto accountDto) {
        if (accountDto == null || bankMember == null) {
            throw new IllegalArgumentException("잘못된 값");
        }

        BankMember existingMember = memberRepository.findById(bankMember.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (accountRepository.existsByAccountNumber(accountDto.getAccountNumber())) {
            throw new IllegalArgumentException("이미 존재하는 계좌입니다.");
        }

        return existingMember;
    }
    @Override
    @Transactional
    public void delete(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계좌입니다."));
        accountRepository.delete(account);

        if (accountRepository.existsById(accountId)) {
            throw new IllegalArgumentException("계좌 삭제에 실패했습니다.");
        }
    }

    @Override
    @Transactional
    public Account updateAccount(Long accountId, AccountDto updatedAccountDto) {
        Account exsitAccount = validationOfId(accountId);

        Account updatedAccount = exsitAccount.toBuilder()
                .accountNumber(updatedAccountDto.getAccountNumber())
                .amount(updatedAccountDto.getAmount())
                .accountStatus(updatedAccountDto.getAccountStatus())
                .build();

        return accountRepository.save(updatedAccount);
    }

    private Account validationOfId(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계좌입니다."));
    }

    @Override
    public long getAccountBalance(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계좌입니다."));
        return account.getAmount();
    }
}