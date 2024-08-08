package miniproject.fintech.repository.transactionrepository;

import miniproject.fintech.domain.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findByTransactedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    List<Transaction> findBySourceAccountId(Long accountId);
}
