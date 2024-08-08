package miniproject.fintech.repository;

import miniproject.fintech.domain.Deposit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DepositRepository extends JpaRepository<Deposit, Long> {

    List<Deposit> findByDepositAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    List<Deposit> findByAccountId(Long accountId);
}
