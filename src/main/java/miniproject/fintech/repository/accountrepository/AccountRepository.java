package miniproject.fintech.repository.accountrepository;

import miniproject.fintech.domain.Account;
import miniproject.fintech.domain.BankMember;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository {

    Account save (Account account);

    Optional<Account> findById(Long id);

    List<Account> findAll();

    Account createdByBankMemberAccount(BankMember bankMember, Account account);

    void deletedByBankMember(BankMember bankMember, Long accountNumber);
}
