package miniproject.fintech.service.transactionservice.depositService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.Account;
import miniproject.fintech.domain.Deposit;
import miniproject.fintech.dto.DepositDto;
import miniproject.fintech.repository.AccountRepository;
import miniproject.fintech.repository.DepositRepository;
import miniproject.fintech.service.accountservice.AccountService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class DepositServiceImpl implements DepositService {

    private final DepositRepository depositRepository;
    private final AccountService accountService;
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public Deposit processDeposit(DepositDto depositDto) {
        //입금 될 계좌 확인
        Account account = accountRepository.findById(depositDto.getAccountId())
                        .orElseThrow(() -> new IllegalArgumentException("계좌를 찾을 수 없습니다."));

        //입금 엔티티 생성
        Deposit deposit = Deposit.builder()
                .depositAmount(depositDto.getDepositAmount())
                .depositAt(depositDto.getDepositAt())
                .depositStatus(depositDto.getDepositStatus())
                .message("입금 확인")
                .account(account)
                .build();

        //입금 추가
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
