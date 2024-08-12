package miniproject.fintech.service.accountservice;

import miniproject.fintech.domain.Account;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.dto.AccountDto;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface AccountService {

    Account save(Account account);

    Optional<Account> findById(Long id);

    List<Account> findAll();

    Account createAccountForMember(AccountDto accountDto, Long memberId);

    void delete(Long accountId);

    Account updateAccount(Long accountId, AccountDto updatedAccount);

    long getAccountBalance(Long id);

    long getTotalAccountBalance();

    boolean existsById(Long id);
}