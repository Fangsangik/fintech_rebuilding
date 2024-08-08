package miniproject.fintech.service.notificationservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.Deposit;
import miniproject.fintech.domain.Transfer;
import miniproject.fintech.type.TransferStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    @Override
    public void sendNotification(Transfer transfer) {
        if (transfer.getTransferStatus() == TransferStatus.COMPLETED) {
            // Implement email or SMS notification logic here
            log.info("송금 성공 알림: {}", transfer.getMessage());
        } else {
            log.error("송금 실패 알림: {}", transfer.getMessage());
        }
    }

    @Override
    public void sendNotification(Deposit deposit) {
        // Implement email or SMS notification logic here
        log.info("입금 알림: {}", deposit.getMessage());
    }
}
