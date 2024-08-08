package miniproject.fintech.service.transactionservice.depositService;

import miniproject.fintech.domain.Deposit;

import java.time.LocalDateTime;
import java.util.List;

public interface DepositService {
    Deposit processDeposit(Deposit deposit);

    List<Deposit> findDepositsByDateRange(LocalDateTime startDate, LocalDateTime endDate, int page, int size);

    List<Deposit> findDepositsByAccountId(Long accountId);
}
