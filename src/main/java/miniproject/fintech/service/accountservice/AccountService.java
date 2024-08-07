package miniproject.fintech.service.accountservice;

import miniproject.fintech.domain.Account;
import miniproject.fintech.domain.BankMember;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface AccountService {

    Account save (Account account);

    Optional<Account> findById(Long id);

    List<Account> findAll();

    Account create (BankMember bankMember, Account accountNumber);

    void delete(BankMember bankMember, Long accountNumber);
}
