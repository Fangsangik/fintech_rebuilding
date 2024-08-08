package miniproject.fintech.service.accountservice;

import miniproject.fintech.domain.Account;
import miniproject.fintech.domain.BankMember;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface AccountService {

    Account save(Account account);

    Optional<Account> findById(Long id);

    List<Account> findAll();

    Account create(BankMember bankMember, Account account);

    void delete(Long accountId);

    Account updateAccount(Long id, Account updatedAccount);

    long getAccountBalance(Long id);
}