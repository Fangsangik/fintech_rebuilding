package miniproject.fintech.service.notificationservice;

import miniproject.fintech.domain.Deposit;
import miniproject.fintech.domain.Transfer;

public interface NotificationService {
    void sendNotification(Transfer transfer);

    void sendNotification(Deposit deposit);
}
