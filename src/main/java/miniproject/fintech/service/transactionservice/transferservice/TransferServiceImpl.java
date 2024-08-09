package miniproject.fintech.service.transactionservice.transferservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.Account;
import miniproject.fintech.domain.Transfer;
import miniproject.fintech.dto.TransferDto;
import miniproject.fintech.repository.AccountRepository;
import miniproject.fintech.repository.TransferRepository;
import miniproject.fintech.service.notificationservice.NotificationServiceImpl;
import miniproject.fintech.type.TransferStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {


    private final TransferRepository transferRepository;
    private final AccountRepository accountRepository;
    private final NotificationServiceImpl notificationService;

    @Override
    @Transactional
    public Transfer processTransfer(TransferDto transferDto) {
        //소스 계좌를 조회
        Account sourceAccount = accountRepository.findById(transferDto.getSourceAccountId())
                .orElseThrow(() -> new IllegalArgumentException("sourceId not Found"));

        //목적 계좌를 조회
        Account destinationAccount = accountRepository.findById(transferDto.getDestinationAccountId())
                .orElseThrow(() -> new IllegalArgumentException("destinationAccountId not Found"));

        //Transfer 엔티티 생성
        Transfer transfer = Transfer.builder()
                .transferAmount(transferDto.getTransferAmount())
                .transferAt(transferDto.getTransferAt())
                .transferStatus(transferDto.getTransferStatus())
                .sourceAccount(sourceAccount)
                .destinationAccount(destinationAccount)
                .message("송금이 실패되었습니다.")
                .build();

        //송금이 충분한지 확인 후 송금
        if (sourceAccount.getAmount() >= transferDto.getTransferAmount()) {
            //소스 계좌에서 금액 차감
            sourceAccount.setAmount(sourceAccount.getAmount() - transferDto.getTransferAmount());

            //목적 계좌에 돈 추가
            destinationAccount.setAmount(destinationAccount.getAmount() + transferDto.getTransferAmount());

            accountRepository.save(sourceAccount);
            accountRepository.save(destinationAccount);

            transfer.setTransferStatus(TransferStatus.COMPLETED);
            transfer.setMessage("송금이 완료되었습니다.");

        } else {
            transfer.setTransferStatus(TransferStatus.FAILED);
            transfer.setMessage("송금이 실패되었습니다. 잔액부족");
        }

        Transfer savedTransfer = transferRepository.save(transfer);
        notificationService.sendNotification(savedTransfer);

        return transfer;
    }
}
