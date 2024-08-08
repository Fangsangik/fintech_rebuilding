package miniproject.fintech.service.accountservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.Account;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.repository.accountrepository.AccountRepository;
import miniproject.fintech.repository.memberrepository.MemberRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static miniproject.fintech.type.AccountStatus.*;

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
    public Account create(BankMember bankMember, Account account) {
        if (account == null || bankMember == null) {
            throw new IllegalArgumentException("잘못된 값");
        }

        BankMember existingMember = memberRepository.findById(bankMember.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (accountRepository.existsByAccountNumber(account.getAccountNumber())) {
            throw new IllegalArgumentException("이미 존재하는 계좌입니다.");
        }

        account.setBankMember(existingMember);
        Account savedAccount = accountRepository.save(account);

        existingMember.getAccounts().add(account);
        memberRepository.save(existingMember);

        return savedAccount;
    }

    @Override
    @Transactional
    public void delete(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계좌입니다."));
        accountRepository.delete(account);
    }

    @Override
    @Transactional
    public Account updateAccount(Long id, Account updatedAccount) {
        Account existingAccount = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계좌입니다."));

        if (updatedAccount.getAccountNumber() != null) {
            existingAccount.setAccountNumber(updatedAccount.getAccountNumber());
        }
        if (updatedAccount.getAmount() >= 0) {
            existingAccount.setAmount(updatedAccount.getAmount());
        }

        return accountRepository.save(existingAccount);
    }

    @Override
    public long getAccountBalance(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계좌입니다."));
        return account.getAmount();
    }
}