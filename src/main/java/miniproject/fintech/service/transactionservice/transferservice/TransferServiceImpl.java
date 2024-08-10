package miniproject.fintech.service.transactionservice.transferservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.Account;
import miniproject.fintech.domain.Transfer;
import miniproject.fintech.dto.TransferDto;
import miniproject.fintech.error.CustomError;
import miniproject.fintech.repository.AccountRepository;
import miniproject.fintech.repository.TransferRepository;
import miniproject.fintech.service.notificationservice.NotificationService;
import miniproject.fintech.service.notificationservice.NotificationServiceImpl;
import miniproject.fintech.type.TransferStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static miniproject.fintech.type.ErrorType.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepository;
    private final AccountRepository accountRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public Transfer processTransfer(TransferDto transferDto) {
        log.info("송금 처리 시작: 송금액 = {}, 송금 출발 계좌 ID = {}, 송금 도착 계좌 ID = {}",
                transferDto.getTransferAmount(), transferDto.getSourceAccountId(), transferDto.getDestinationAccountId());

        // 소스 계좌 조회
        Account sourceAccount = accountRepository.findById(transferDto.getSourceAccountId())
                .orElseThrow(() -> {
                    log.error("소스 계좌를 찾을 수 없습니다: 계좌 ID = {}", transferDto.getSourceAccountId());
                    return new CustomError(SOURCE_ID_NOT_FOUND);
                });

        // 목적 계좌 조회
        Account destinationAccount = accountRepository.findById(transferDto.getDestinationAccountId())
                .orElseThrow(() -> {
                    log.error("목적 계좌를 찾을 수 없습니다: 계좌 ID = {}", transferDto.getDestinationAccountId());
                    return new CustomError(DESTINATION_ID_NOT_FOUND);
                });

        // Transfer 엔티티 생성
        Transfer transfer = Transfer.builder()
                .transferAmount(transferDto.getTransferAmount())
                .transferAt(transferDto.getTransferAt())
                .transferStatus(TransferStatus.FAILED) // 초기 상태는 FAILED로 설정
                .sourceAccount(sourceAccount)
                .destinationAccount(destinationAccount)
                .message("송금이 실패되었습니다.")
                .build();

        // 송금 가능 여부 확인 후 송금
        if (sourceAccount.getAmount() >= transferDto.getTransferAmount()) {
            // 소스 계좌에서 금액 차감
            sourceAccount.setAmount(sourceAccount.getAmount() - transferDto.getTransferAmount());
            // 목적 계좌에 돈 추가
            destinationAccount.setAmount(destinationAccount.getAmount() + transferDto.getTransferAmount());

            // 계좌 정보 저장
            accountRepository.save(sourceAccount);
            accountRepository.save(destinationAccount);

            // 송금 성공 상태 업데이트
            transfer.setTransferStatus(TransferStatus.COMPLETED);
            transfer.setMessage("송금이 완료되었습니다.");

            log.info("송금 처리 완료: 송금액 = {}, 출발 계좌 ID = {}, 도착 계좌 ID = {}, 새로운 출발 계좌 잔액 = {}, 새로운 도착 계좌 잔액 = {}",
                    transferDto.getTransferAmount(), transferDto.getSourceAccountId(), transferDto.getDestinationAccountId(),
                    sourceAccount.getAmount(), destinationAccount.getAmount());
        } else {
            log.warn("송금 실패: 잔액 부족, 송금액 = {}, 출발 계좌 ID = {}, 현재 잔액 = {}",
                    transferDto.getTransferAmount(), transferDto.getSourceAccountId(), sourceAccount.getAmount());
        }

        // 송금 기록 저장
        Transfer savedTransfer = transferRepository.save(transfer);

        // 알림 전송
        notificationService.sendNotification(savedTransfer);

        return savedTransfer;
    }
}