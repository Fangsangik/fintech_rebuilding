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

    private AccountDto getAccountDtoById(Long accountId, String accountType) {
        return accountRepository.findById(accountId)
                .map(dtoConverter::convertToAccountDto)
                .orElseThrow(() -> {
                    log.error("{} 계좌를 찾을 수 없습니다: 계좌 ID = {}", accountType, accountId);
                    return new CustomError(accountType.equals("소스") ? SOURCE_ID_NOT_FOUND : DESTINATION_ID_NOT_FOUND);
                });
    }

    private TransferDto executeTransfer(TransferDto transferDto, AccountDto sourceAccountDto, AccountDto destinationAccountDto) {
        // Transfer 엔티티 생성
        Transfer transfer = new Transfer();
        transfer.setTransferAmount(transferDto.getTransferAmount());
        transfer.setTransferAt(transferDto.getTransferAt());
        transfer.setTransferStatus(TransferStatus.FAILED); // 초기 상태는 FAILED로 설정
        transfer.setSourceAccountId(transferDto.getSourceAccountId());
        transfer.setDestinationAccountId(transferDto.getDestinationAccountId());
        transfer.setMessage("송금이 실패되었습니다.");

        // 송금 가능 여부 확인 후 송금
        if (sourceAccountDto.getAmount() >= transferDto.getTransferAmount()) {
            // 소스 계좌에서 금액 차감
            sourceAccountDto.setAmount(sourceAccountDto.getAmount() - transferDto.getTransferAmount());
            // 목적 계좌에 돈 추가
            destinationAccountDto.setAmount(destinationAccountDto.getAmount() + transferDto.getTransferAmount());

            // 계좌 정보 업데이트
            accountRepository.save(entityConverter.convertToAccount(sourceAccountDto));
            accountRepository.save(entityConverter.convertToAccount(destinationAccountDto));

            // 송금 성공 상태 업데이트
            transfer.setTransferStatus(TransferStatus.COMPLETED);
            transfer.setMessage("송금이 완료되었습니다.");

            log.info("송금 처리 완료: 송금액 = {}, 출발 계좌 ID = {}, 도착 계좌 ID = {}, 새로운 출발 계좌 잔액 = {}, 새로운 도착 계좌 잔액 = {}",
                    transferDto.getTransferAmount(), transferDto.getSourceAccountId(), transferDto.getDestinationAccountId(),
                    sourceAccountDto.getAmount(), destinationAccountDto.getAmount());
        } else {
            log.warn("송금 실패: 잔액 부족, 송금액 = {}, 출발 계좌 ID = {}, 현재 잔액 = {}",
                    transferDto.getTransferAmount(), transferDto.getSourceAccountId(), sourceAccountDto.getAmount());
        }

        // 송금 기록 저장
        Transfer savedTransfer = transferRepository.save(transfer);

        return dtoConverter.convertToTransferDto(savedTransfer);
    }
}
