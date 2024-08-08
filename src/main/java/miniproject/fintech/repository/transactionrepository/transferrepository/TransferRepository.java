package miniproject.fintech.repository.transactionrepository.transferrepository;

import miniproject.fintech.domain.Transfer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
    List<Transfer> findByTransferAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    List<Transfer> findBySourceAccountId(Long accountId);
}
