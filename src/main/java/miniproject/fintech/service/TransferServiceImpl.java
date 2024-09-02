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
import miniproject.fintech.type.TransferStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    private final EntityConverter entityConverter;

    @Transactional
    public TransferDto processTransfer(TransferDto transferDto) {
        log.info("송금 처리 시작: 송금액 = {}, 송금 출발 계좌 ID = {}, 송금 도착 계좌 ID = {}",
                transferDto.getTransferAmount(), transferDto.getSourceAccountId(), transferDto.getDestinationAccountId());

        // 소스 계좌와 목적 계좌 조회
        AccountDto sourceAccountDto = getAccountDtoById(transferDto.getSourceAccountId(), "소스");
        AccountDto destinationAccountDto = getAccountDtoById(transferDto.getDestinationAccountId(), "목적");

        // 송금 처리 로직
        TransferDto savedTransferDto = executeTransfer(transferDto, sourceAccountDto, destinationAccountDto);

        // 알림 전송
        notificationService.sendNotification(savedTransferDto);

        return savedTransferDto;
    }

    @Transactional(readOnly = true)
    public TransferDto getTransferById(Long transferId) {
        log.info("송금 기록 조회 시작: 송금 ID = {}", transferId);

        Transfer transfer = transferRepository.findById(transferId)
                .orElseThrow(() -> new CustomError(TRANSFER_NOT_FOUND));

        log.info("송금 기록 조회 완료: 송금 ID = {}, 송금 정보 = {}", transferId, transfer);
        return dtoConverter.convertToTransferDto(transfer);
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

    private AccountDto getAccountDtoById(Long accountId, String accountType) {
        return accountRepository.findById(accountId)
                .map(dtoConverter::convertToAccountDto)
                .orElseThrow(() -> {
                    log.error("{} 계좌를 찾을 수 없습니다: 계좌 ID = {}", accountType, accountId);
                    return new CustomError(accountType.equals("소스") ? SOURCE_ID_NOT_FOUND : DESTINATION_ID_NOT_FOUND);
                });
    }

    private TransferDto executeTransfer(TransferDto transferDto, AccountDto sourceAccountDto, AccountDto destinationAccountDto) {
        Transfer transfer = new Transfer();
        transfer.setTransferAmount(transferDto.getTransferAmount());
        transfer.setTransferAt(transferDto.getTransferAt());
        transfer.setTransferStatus(TransferStatus.FAILED); // 초기 상태는 FAILED로 설정
        transfer.setSourceAccountId(transferDto.getSourceAccountId());
        transfer.setDestinationAccountId(transferDto.getDestinationAccountId());
        transfer.setMessage("송금이 실패되었습니다.");

        if (sourceAccountDto.getAmount() >= transferDto.getTransferAmount()) {
            sourceAccountDto.setAmount(sourceAccountDto.getAmount() - transferDto.getTransferAmount());
            destinationAccountDto.setAmount(destinationAccountDto.getAmount() + transferDto.getTransferAmount());

            accountRepository.save(entityConverter.convertToAccount(sourceAccountDto));
            accountRepository.save(entityConverter.convertToAccount(destinationAccountDto));

            transfer.setTransferStatus(TransferStatus.COMPLETED);
            transfer.setMessage("송금이 완료되었습니다.");

            log.info("송금 처리 완료: 송금액 = {}, 출발 계좌 ID = {}, 도착 계좌 ID = {}, 새로운 출발 계좌 잔액 = {}, 새로운 도착 계좌 잔액 = {}",
                    transferDto.getTransferAmount(), transferDto.getSourceAccountId(), transferDto.getDestinationAccountId(),
                    sourceAccountDto.getAmount(), destinationAccountDto.getAmount());
        } else {
            log.warn("송금 실패: 잔액 부족, 송금액 = {}, 출발 계좌 ID = {}, 현재 잔액 = {}",
                    transferDto.getTransferAmount(), transferDto.getSourceAccountId(), sourceAccountDto.getAmount());
        }

        Transfer savedTransfer = transferRepository.save(transfer);
        return dtoConverter.convertToTransferDto(savedTransfer);
    }
}

