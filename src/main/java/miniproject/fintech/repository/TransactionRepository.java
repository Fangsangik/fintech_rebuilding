package miniproject.fintech.repository;

import miniproject.fintech.domain.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findByTransactedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    List<Transaction> findByAccount_Id(Long accountId);
    Page<Transaction> findByAccount_Id(Long accountId, Pageable pageable);

    boolean existsById(Long transactionId);

    Page<Transaction> findAll(Pageable pageable);

    List<Transaction> findByBankMemberId(Long memberId);
    Page<Transaction> findByBankMemberId(Long memberId, Pageable pageable);

}
