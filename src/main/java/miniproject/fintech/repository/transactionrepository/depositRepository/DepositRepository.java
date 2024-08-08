package miniproject.fintech.repository.transactionrepository.depositRepository;

import miniproject.fintech.domain.Deposit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface DepositRepository extends JpaRepository<Deposit, Long> {

    List<Deposit> findByDepositAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    List<Deposit> findByAccountId(Long accountId);
}
