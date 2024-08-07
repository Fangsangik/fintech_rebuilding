package miniproject.fintech.service.accountservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.Account;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.repository.accountrepository.JpaAccountRepository;
import miniproject.fintech.repository.memberrepository.JpaMemberRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService{

    private final JpaMemberRepository memberRepository;
    private final JpaAccountRepository accountRepository;

    @Override
    public Account save(Account account) {
        return accountRepository.save(account);
    }

    @Override
    public Optional<Account> findById(Long id) {
        Optional<Account> accountId = accountRepository.findById(id);

        validationFindById(id, accountId);

        //get 메서드를 사용시 안정성 우려
        //return Optional.of(accountId.get());

        return accountId;
    }

    private static void validationFindById(Long id, Optional<Account> accountId) {
        if (accountId.isPresent()) {
            log.info("아이디가 존재합니다: {}", id);
        } else {
            log.warn("아이디가 존재하지 않습니다: {}", id);
            throw new IllegalArgumentException("잘못된 접근입니다");
        }
    }

    @Override
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override
    public Account create(BankMember bankMember, Account account) {
        Optional<BankMember> member = memberRepository.findById(bankMember.getId());


        if (member.isPresent()) {
            BankMember existingMember = member.get();
            if (existingMember.getAccounts() == null) {
                existingMember.setAccounts(new ArrayList<>());
            }
            account.setBankMember(existingMember);
            existingMember.getAccounts().add(account);
            return accountRepository.save(account);
        }

        // BankMember가 존재하지 않으면 null 반환
        return null;
    }

    @Override
    public void delete(BankMember bankMember, Long accountNumber) {
        Optional<BankMember> memberOpt = memberRepository.findById(bankMember.getId());

        validationDeleteAccount(accountNumber, memberOpt);
    }

    private void validationDeleteAccount(Long accountNumber, Optional<BankMember> memberOpt) {
        if (memberOpt.isPresent()) {
            BankMember member = memberOpt.get();

            if (member.getAccounts() == null) {
                throw new IllegalArgumentException("BankMember's accounts list is null");
            }

            // 계좌가 존재하는지 확인합니다.
            Account accountToDelete = member.getAccounts().stream()
                    .filter(account -> account.getAccountNumber() == accountNumber)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Account not found"));

            // 계좌 삭제
            member.getAccounts().remove(accountToDelete);
            accountRepository.delete(accountToDelete);
        } else {
            throw new IllegalArgumentException("BankMember does not exist");
        }
    }
}
