package miniproject.fintech.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.Account;
import miniproject.fintech.domain.Transfer;
import miniproject.fintech.dto.AccountDto;
import miniproject.fintech.dto.DtoConverter;
import miniproject.fintech.dto.EntityConverter;
import miniproject.fintech.dto.TransferDto;
import miniproject.fintech.error.CustomError;
import miniproject.fintech.repository.AccountRepository;
import miniproject.fintech.repository.TransferRepository;
import miniproject.fintech.type.ErrorType;
import miniproject.fintech.type.TransferStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static miniproject.fintech.type.ErrorType.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferServiceImpl {

    private final TransferRepository transferRepository;
    private final AccountRepository accountRepository;
    private final AccountServiceImpl accountService;
    private final NotificationServiceImpl notificationService;
    private final DtoConverter dtoConverter;
    private final EntityConverter entityConverter;

    @Transactional
    public Transfer processTransfer(TransferDto transferDto) {
        log.info("송금 처리 시작: 송금액 = {}, 송금 출발 계좌 ID = {}, 송금 도착 계좌 ID = {}",
                transferDto.getTransferAmount(), transferDto.getSourceAccountId(), transferDto.getDestinationAccountId());

        // 소스 계좌와 목적 계좌 조회
        AccountDto sourceAccountDto = getAccountDtoById(transferDto.getSourceAccountId(), "소스");
        AccountDto destinationAccountDto = getAccountDtoById(transferDto.getDestinationAccountId(), "목적");

        if (sourceAccountDto.getId().equals(destinationAccountDto.getId())) {
            throw new CustomError(ErrorType.SOURCE_ACCOUNT_DESTINATION_ACCOUNT_SHOULD_NOT_SAME);
        }

        // 송금 처리 로직
        TransferDto savedTransferDto = executeTransfer(transferDto, sourceAccountDto, destinationAccountDto);

        // 알림 전송 (비동기 처리 고려)
        try {
            notificationService.sendNotification(savedTransferDto);
        } catch (Exception e) {
            log.error("알림 전송 실패: {}", e.getMessage());
            // 알림 전송 실패에 대한 추가 처리 필요
        }

        return entityConverter.convertToTransfer(savedTransferDto);
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
    public List<TransferDto> getTransfersByAccountId(Long accountId) {
        log.info("특정 계좌의 모든 송금 기록 조회 시작: 계좌 ID = {}", accountId);

        List<Transfer> transfers = transferRepository.findBySourceAccountIdOrDestinationAccountId(accountId, accountId);
        List<TransferDto> transferDtos = transfers.stream()
                .map(dtoConverter::convertToTransferDto)
                .collect(Collectors.toList());

        log.info("특정 계좌의 모든 송금 기록 조회 완료: 계좌 ID = {}, 송금 수 = {}", accountId, transferDtos.size());
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

    private AccountDto getAccountDtoById(Long accountId, String type) {
        return accountService.findById(accountId)
                .orElseThrow(() -> new CustomError(ACCOUNT_NOT_FOUND));
    }

    private TransferDto executeTransfer(TransferDto transferDto, AccountDto sourceAccountDto, AccountDto destinationAccountDto) {
        if (sourceAccountDto.getAmount() < transferDto.getTransferAmount()) {
            throw new CustomError(NOT_ENOUGH_MONEY);
        }

        // 출발 계좌에서 금액 차감
        sourceAccountDto.setAmount(sourceAccountDto.getAmount() - transferDto.getTransferAmount());
        accountService.updateAccount(sourceAccountDto.getId(), sourceAccountDto);

        // 도착 계좌에 금액 추가
        destinationAccountDto.setAmount(destinationAccountDto.getAmount() + transferDto.getTransferAmount());
        accountService.updateAccount(destinationAccountDto.getId(), destinationAccountDto);

        // TransferDto 생성 및 반환
        TransferDto savedTransferDto = TransferDto.builder()
                .sourceAccountId(sourceAccountDto.getId())
                .destinationAccountId(destinationAccountDto.getId())
                .transferAmount(transferDto.getTransferAmount())
                .transferStatus(TransferStatus.COMPLETED)
                .transferAt(LocalDateTime.now())
                .message("송금이 완료되었습니다.")
                .build();

        return savedTransferDto;
    }
}

