package miniproject.fintech.service.transactionservice.depositService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.Account;
import miniproject.fintech.domain.Deposit;
import miniproject.fintech.repository.transactionrepository.depositRepository.DepositRepository;
import miniproject.fintech.service.accountservice.AccountService;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static miniproject.fintech.type.DepositStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepositServiceImpl implements DepositService {

    private final DepositRepository depositRepository;
    private final AccountService accountService;

    @Override
    public Deposit processDeposit(Deposit deposit) {
        Account account = deposit.getAccount();
        account.setAmount(account.getAmount() + deposit.getDepositAmount());

        Deposit savedDeposit = depositRepository.save(deposit);
        accountService.save(account);

        return savedDeposit;
    }

    @Override
    public List<Deposit> findDepositsByDateRange(LocalDateTime startDate, LocalDateTime endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return depositRepository.findByDepositAtBetween(startDate, endDate, pageable);
    }

    @Override
    public List<Deposit> findDepositsByAccountId(Long accountId) {
        return depositRepository.findByAccountId(accountId);
    }
}
