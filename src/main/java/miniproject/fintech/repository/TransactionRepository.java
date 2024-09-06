package miniproject.fintech.repository;

import miniproject.fintech.domain.BankMember;
import miniproject.fintech.domain.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByTransactedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    boolean existsById(Long transactionId);

    Page<Transaction> findAll(Pageable pageable);

    @Query("select t from Transaction t where t.sourceAccountNumber = :accountNumber or t.account.accountNumber = :accountNumber")
    List<Transaction> findByAccountNumber(String accountNumber);

    List<Transaction> findByBankMember(BankMember bankMember);
}
