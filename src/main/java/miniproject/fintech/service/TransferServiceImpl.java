package miniproject.fintech.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.Account;
import miniproject.fintech.domain.BankMember;
import miniproject.fintech.domain.Transaction;
import miniproject.fintech.domain.Transfer;
import miniproject.fintech.dto.*;
import miniproject.fintech.error.CustomError;
import miniproject.fintech.repository.AccountRepository;
import miniproject.fintech.repository.MemberRepository;
import miniproject.fintech.repository.TransactionRepository;
import miniproject.fintech.repository.TransferRepository;
import miniproject.fintech.type.ErrorType;
import miniproject.fintech.type.TransactionStatus;
import miniproject.fintech.type.TransactionType;
import miniproject.fintech.type.TransferStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static miniproject.fintech.type.ErrorType.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferServiceImpl {

    private final TransferRepository transferRepository;
    private final AccountRepository accountRepository;
    private final NotificationServiceImpl notificationService;
    private final DtoConverter dtoConverter;
    private final TransactionRepository transactionRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public TransferDto processTransfer(TransferDto transferDto) {
        log.info("송금 처리 시작: 송금액 = {}, 출발 계좌 = {}, 도착 계좌 = {}",
                transferDto.getTransferAmount(), transferDto.getSourceAccountNumber(), transferDto.getDestinationAccountNumber());

        // 소스 계좌 확인
        Account sourceAccount = accountRepository.findByAccountNumber(transferDto.getSourceAccountNumber())
                .orElseThrow(() -> {
                    log.error("출발 계좌를 찾을 수 없습니다: 계좌 번호 = {}", transferDto.getSourceAccountNumber());
                    return new CustomError(ACCOUNT_NOT_FOUND);
                });

        // 목적 계좌 확인
        Account destinationAccount = accountRepository.findByAccountNumber(transferDto.getDestinationAccountNumber())
                .orElseThrow(() -> {
                    log.error("도착 계좌를 찾을 수 없습니다: 계좌 번호 = {}", transferDto.getDestinationAccountNumber());
                    return new CustomError(ACCOUNT_NOT_FOUND);
                });

        // 회원 정보 조회
        BankMember bankMember = memberRepository.findByUserId(transferDto.getBankMemberDto().getUserId())
                .orElseThrow(() -> new CustomError(MEMBER_NOT_FOUND));

        // 소스와 목적 계좌가 동일한지 확인
        if (sourceAccount.getId().equals(destinationAccount.getId())) {
            throw new CustomError(ErrorType.SOURCE_ACCOUNT_DESTINATION_ACCOUNT_SHOULD_NOT_SAME);
        }

        // 출발 계좌 잔액 확인
        if (sourceAccount.getAmount() < transferDto.getTransferAmount()) {
            log.error("출발 계좌 잔액 부족: 출발 계좌 = {}, 잔액 = {}",
                    transferDto.getSourceAccountNumber(), sourceAccount.getAmount());
            throw new CustomError(NOT_ENOUGH_MONEY);
        }

        // 출발 계좌에서 금액 차감
        double previousSourceAmount = sourceAccount.getAmount();
        sourceAccount.setAmount((long) (previousSourceAmount - transferDto.getTransferAmount()));
        accountRepository.save(sourceAccount);

        // 목적 계좌에 금액 추가
        double previousDestinationAmount = destinationAccount.getAmount();
        destinationAccount.setAmount((long) (previousDestinationAmount + transferDto.getTransferAmount()));
        accountRepository.save(destinationAccount);

        // Transfer 엔티티 생성
        Transfer transfer = Transfer.builder()
                .transferAmount(transferDto.getTransferAmount())
                .transferAt(transferDto.getTransferAt())
                .sourceAccountNumber(transferDto.getSourceAccountNumber())
                .destinationAccountNumber(transferDto.getDestinationAccountNumber())
                .transferStatus(TransferStatus.COMPLETED)
                .message("송금 완료")
                .build();

        // 거래 내역 기록 - 이미 조회한 계좌와 회원 정보 전달
        Transaction transaction = recordTransaction(transfer, bankMember, sourceAccount, destinationAccount);
        // Transfer 정보 저장
        Transfer savedTransfer = transferRepository.save(transfer);
        TransferDto resultDto = dtoConverter.convertToTransferDto(savedTransfer);
        resultDto.setTransactionId(transaction.getId());

        log.info("송금 처리 완료: 출발 계좌 = {}, 도착 계좌 = {}, 송금액 = {}, 출발 계좌 이전 잔액 = {}, 새로운 잔액 = {}, 도착 계좌 이전 잔액 = {}, 새로운 잔액 = {}",
                transferDto.getSourceAccountNumber(), transferDto.getDestinationAccountNumber(), transfer.getTransferAmount(),
                previousSourceAmount, sourceAccount.getAmount(), previousDestinationAmount, destinationAccount.getAmount());

        // 알림 전송
        try {
            notificationService.sendNotification(dtoConverter.convertToTransferDto(savedTransfer));
        } catch (Exception e) {
            log.error("알림 전송 실패: {}", e.getMessage());
            // 알림 전송 실패 시 추가 처리 로직 (필요하다면)
        }

        return  resultDto;
    }


    private Transaction recordTransaction(Transfer transfer, BankMember bankMember, Account sourceAccount, Account destinationAccount) {
        Transaction transaction = Transaction.builder()
                .bankMember(bankMember)
                .transactionAmount(transfer.getTransferAmount())
                .transactionType(TransactionType.TRANSFER)
                .transactionStatus(TransactionStatus.SUCCESS)
                .transactedAt(LocalDateTime.now())
                .sourceAccountNumber(transfer.getSourceAccountNumber())  // 출발 계좌 번호
                .curAmount(destinationAccount.getAmount())  // 도착 계좌의 현재 잔액
                .referenceNumber(UUID.randomUUID().toString())  // 고유 참조 번호 생성
                .currency("KRW")  // 통화 정보
                .fee(0.0)  // 수수료 정보 필요 시 추가
                .message(transfer.getMessage())
                .counterpartyInfo(transfer.getDestinationAccountNumber())  // 상대방 정보 (도착 계좌)
                .build();

        return transactionRepository.save(transaction);
    }



    @Transactional(readOnly = true)
    public Transfer getTransferById(Long transferId) {
        log.info("송금 기록 조회 시작: 송금 ID = {}", transferId);

        Transfer transfer = transferRepository.findById(transferId)
                .orElseThrow(() -> new CustomError(TRANSFER_NOT_FOUND));

        log.info("송금 기록 조회 완료: 송금 ID = {}, 송금 정보 = {}", transferId, transfer);
        return transfer;
    }

    @Transactional(readOnly = true)
    public List<TransferDto> getTransfersByAccountNumber(String accountNumber) {
        log.info("특정 계좌의 모든 송금 기록 조회 시작: 계좌 ID = {}", accountNumber);

        List<Transfer> transfers = transferRepository.findBySourceAccountNumberOrDestinationAccountNumber(accountNumber, accountNumber);
        List<TransferDto> transferDtos = transfers.stream()
                .map(dtoConverter::convertToTransferDto)
                .collect(Collectors.toList());

        log.info("특정 계좌의 모든 송금 기록 조회 완료: 계좌 ID = {}, 송금 수 = {}", accountNumber, transferDtos.size());
        return transferDtos;
    }

    @Transactional
    public void deleteTransferById(Long transferId) {
        log.info("송금 기록 삭제 시작: 송금 ID = {}", transferId);

        Transfer transfer = transferRepository.findById(transferId)
                .orElseThrow(() -> new CustomError(TRANSFER_NOT_FOUND));

        transferRepository.delete(transfer);

        log.info("송금 기록 삭제 완료: 송금 ID = {}", transferId);
    }
}

