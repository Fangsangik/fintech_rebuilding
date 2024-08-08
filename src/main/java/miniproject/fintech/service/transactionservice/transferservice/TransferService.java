package miniproject.fintech.service.transactionservice.transferservice;

import miniproject.fintech.domain.Transfer;

public interface TransferService {
    Transfer processTransfer(Transfer transfer);
}
