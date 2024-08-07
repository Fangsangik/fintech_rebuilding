package miniproject.fintech.repository.accountrepository;

import miniproject.fintech.domain.Account;
import miniproject.fintech.domain.BankMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaAccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByBankMemberId(BankMember bankMember);

}
