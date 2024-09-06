package miniproject.fintech.repository;

import miniproject.fintech.domain.Account;
import miniproject.fintech.domain.Transfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {
    List<Transfer> findByTransferAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    Page<Transfer> findAll(Pageable pageable);

    List<Transfer> findBySourceAccountNumberOrDestinationAccountNumber(String accountNumber1, String accountNumber2);

}
