package miniproject.fintech.service.transactionservice.depositService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.Account;
import miniproject.fintech.domain.Deposit;
import miniproject.fintech.dto.DepositDto;
import miniproject.fintech.error.CustomError;
import miniproject.fintech.repository.AccountRepository;
import miniproject.fintech.repository.DepositRepository;
import miniproject.fintech.service.accountservice.AccountService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static miniproject.fintech.type.ErrorType.*;


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
        log.info("입금 처리 시작: 계좌 ID = {}, 입금 금액 = {}", depositDto.getAccountId(), depositDto.getDepositAmount());

        // 입금될 계좌 확인
        Account account = accountRepository.findById(depositDto.getAccountId())
                .orElseThrow(() -> {
                    log.error("입금 계좌를 찾을 수 없습니다: 계좌 ID = {}", depositDto.getAccountId());
                    return new CustomError(ACCOUNT_NOT_FOUND);
                });

        // 입금 엔티티 생성
        Deposit deposit = Deposit.builder()
                .depositAmount(depositDto.getDepositAmount())
                .depositAt(depositDto.getDepositAt())
                .depositStatus(depositDto.getDepositStatus())
                .message("입금 확인")
                .account(account)
                .build();

        // 계좌 금액 업데이트
        double previousAmount = account.getAmount();
        account.setAmount((long) (previousAmount + deposit.getDepositAmount()));

        // 입금 정보 저장
        Deposit savedDeposit = depositRepository.save(deposit);

        // 계좌 정보 업데이트
        accountService.save(account);

        log.info("입금 처리 완료: 계좌 ID = {}, 입금 금액 = {}, 이전 금액 = {}, 새 금액 = {}",
                depositDto.getAccountId(), deposit.getDepositAmount(), previousAmount, account.getAmount());

        return savedDeposit;
    }

    @Override
    public List<Deposit> findDepositsByDateRange(LocalDateTime startDate, LocalDateTime endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        log.info("날짜 범위에 따른 입금 조회 시작: 시작일 = {}, 종료일 = {}, 페이지 = {}, 크기 = {}",
                startDate, endDate, page, size);
        return depositRepository.findByDepositAtBetween(startDate, endDate, pageable);
    }

    @Override
    public List<Deposit> findDepositsByAccountId(Long accountId) {
        log.info("계좌 ID에 따른 입금 조회 시작: 계좌 ID = {}", accountId);
        return depositRepository.findByAccountId(accountId);
    }
}