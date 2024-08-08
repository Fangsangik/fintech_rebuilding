package miniproject.fintech.service.transactionservice.transferservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.Account;
import miniproject.fintech.domain.Transfer;
import miniproject.fintech.repository.accountrepository.AccountRepository;
import miniproject.fintech.repository.transactionrepository.transferrepository.TransferRepository;
import miniproject.fintech.service.notificationservice.NotificationServiceImpl;
import miniproject.fintech.type.TransferStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {


    private final TransferRepository transferRepository;
    private final AccountRepository accountRepository;
    private final NotificationServiceImpl notificationService;

    @Override
    public Transfer processTransfer(Transfer transfer) {
        Account sourceAccount = transfer.getSourceAccount();
        Account destinationAccount = transfer.getDestinationAccount();

        if (sourceAccount.getAmount() >= transfer.getTransferAmount()) {
            sourceAccount.setAmount(sourceAccount.getAmount() - transfer.getTransferAmount());
            destinationAccount.setAmount(destinationAccount.getAmount() + transfer.getTransferAmount());
            accountRepository.save(sourceAccount);
            accountRepository.save(destinationAccount);

            transfer.setTransferStatus(TransferStatus.COMPLETED);
            transfer.setMessage("송금이 완료되었습니다.");
        } else {
            transfer.setTransferStatus(TransferStatus.FAILED);
            transfer.setMessage("송금이 실패되었습니다. 잔액부족");
        }

        transfer = transferRepository.save(transfer);
        notificationService.sendNotification(transfer);

        return transfer;
    }
}
