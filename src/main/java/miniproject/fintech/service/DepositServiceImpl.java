package miniproject.fintech.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.Account;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.domain.Deposit;
import miniproject.fintech.domain.Transaction;
import miniproject.fintech.dto.DepositDto;
import miniproject.fintech.dto.DtoConverter;
import miniproject.fintech.error.CustomError;
import miniproject.fintech.repository.AccountRepository;
import miniproject.fintech.repository.DepositRepository;
import miniproject.fintech.repository.MemberRepository;
import miniproject.fintech.repository.TransactionRepository;
import miniproject.fintech.type.TransactionStatus;
import miniproject.fintech.type.TransactionType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static miniproject.fintech.type.ErrorType.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class DepositServiceImpl {


    private final TransactionRepository transactionRepository;
    private final DepositRepository depositRepository;
    private final AccountRepository accountRepository;
    private final DtoConverter dtoConverter;
    private final MemberRepository memberRepository;

    @Transactional
    public DepositDto processDeposit(DepositDto depositDto) {
        log.info("입금 처리 시작: 출금 계좌 = {}, 입금 계좌 = {}, 입금 금액 = {}",
                depositDto.getSourceAccountNumber(), depositDto.getDestinationAccountNumber(), depositDto.getDepositAmount());

        // 출금 계좌 확인
        Account sourceAccount = accountRepository.findByAccountNumber(depositDto.getSourceAccountNumber())
                .orElseThrow(() -> {
                    log.error("출금 계좌를 찾을 수 없습니다: 계좌 번호 = {}", depositDto.getSourceAccountNumber());
                    return new CustomError(ACCOUNT_NOT_FOUND);
                });

        // 입금 계좌 확인
        Account destinationAccount = accountRepository.findByAccountNumber(depositDto.getDestinationAccountNumber())
                .orElseThrow(() -> {
                    log.error("입금 계좌를 찾을 수 없습니다: 계좌 번호 = {}", depositDto.getDestinationAccountNumber());
                    return new CustomError(ACCOUNT_NOT_FOUND);
                });

        // 회원 정보 조회
        BankMember bankMember = memberRepository.findByUserId(depositDto.getBankMemberDto().getUserId())
                .orElseThrow(() -> new CustomError(MEMBER_NOT_FOUND));

        // 출금 계좌 잔액 확인
        if (sourceAccount.getAmount() < depositDto.getDepositAmount()) {
            log.error("출금 계좌 잔액 부족: 출금 계좌 = {}, 잔액 = {}",
                    depositDto.getSourceAccountNumber(), sourceAccount.getAmount());
            throw new CustomError(NOT_ENOUGH_MONEY);
        }

        // 출금 계좌 금액 차감
        double previousSourceAmount = sourceAccount.getAmount();
        sourceAccount.setAmount((long) (previousSourceAmount - depositDto.getDepositAmount()));
        accountRepository.save(sourceAccount);

        // 입금 계좌 금액 추가
        double previousDestinationAmount = destinationAccount.getAmount();
        destinationAccount.setAmount((long) (previousDestinationAmount + depositDto.getDepositAmount()));
        accountRepository.save(destinationAccount);

        // 입금 엔티티 생성
        Deposit deposit = Deposit.builder()
                .depositAmount(depositDto.getDepositAmount())
                .depositAt(depositDto.getDepositAt())
                .depositStatus(depositDto.getDepositStatus())
                .message("입금 확인")
                .sourceAccountNumber(depositDto.getSourceAccountNumber())
                .destinationAccountNumber(depositDto.getDestinationAccountNumber())
                .account(destinationAccount) // 입금 계좌와 연결
                .build();

        // 입금 정보 저장
        Deposit savedDeposit = depositRepository.save(deposit);

        // 거래 내역 기록 (이미 조회한 계좌와 회원 정보 전달)
        Transaction transaction = recordTransaction(deposit, bankMember, sourceAccount, destinationAccount);

        log.info("입금 처리 완료: 출금 계좌 = {}, 입금 계좌 = {}, 입금 금액 = {}, 출금 계좌 이전 잔액 = {}, 새로운 잔액 = {}, 입금 계좌 이전 잔액 = {}, 새로운 잔액 = {}",
                depositDto.getSourceAccountNumber(), depositDto.getDestinationAccountNumber(), deposit.getDepositAmount(),
                previousSourceAmount, sourceAccount.getAmount(), previousDestinationAmount, destinationAccount.getAmount());

        // DTO에 transactionId 추가 후 반환
        DepositDto resultDto = dtoConverter.convertToDepositDto(savedDeposit);
        resultDto.setTransactionId(transaction.getId());

        return resultDto;
    }

    private Transaction recordTransaction(Deposit deposit, BankMember bankMember, Account sourceAccount, Account destinationAccount) {
        // 입금 후 잔액 정보 설정 (입금 계좌의 현재 잔액을 가져옴)
        Transaction transaction = Transaction.builder()
                .transactionAmount(deposit.getDepositAmount())
                .transactionType(TransactionType.DEPOSIT)
                .transactionStatus(TransactionStatus.SUCCESS)
                .transactedAt(LocalDateTime.now())
                .account(sourceAccount)
                .bankMember(bankMember)
                .sourceAccountNumber(deposit.getSourceAccountNumber())  // 출금 계좌 번호 설정
                .destinationAccountNumber(deposit.getDestinationAccountNumber())  // 입금 계좌 번호 설정
                .curAmount(destinationAccount.getAmount())  // 입금 계좌의 현재 잔액
                .referenceNumber(UUID.randomUUID().toString())  // 고유 참조 번호 생성
                .currency("KRW")  // 통화 정보 설정
                .message(deposit.getMessage())
                .counterpartyInfo(deposit.getSourceAccountNumber())  // 상대방 정보 설정 (출금 계좌)
                .build();

        return transactionRepository.save(transaction);
    }

    @Transactional(readOnly = true)
    public List<DepositDto> findDepositsByDateRange(LocalDateTime startDate, LocalDateTime endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        log.info("날짜 범위에 따른 입금 조회 시작: 시작일 = {}, 종료일 = {}, 페이지 = {}, 크기 = {}",
                startDate, endDate, page, size);

        List<Deposit> deposits = depositRepository.findByDepositAtBetween(startDate, endDate, pageable);

        // List of Deposit entities to List of DepositDto
        return deposits.stream()
                .map(dtoConverter::convertToDepositDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DepositDto> findDepositsByAccountId(Long accountId) {
        log.info("계좌 ID에 따른 입금 조회 시작: 계좌 ID = {}", accountId);

        List<Deposit> deposits = depositRepository.findByAccountId(accountId);

        // List of Deposit entities to List of DepositDto
        return deposits.stream()
                .map(dtoConverter::convertToDepositDto)
                .collect(Collectors.toList());
    }
}
