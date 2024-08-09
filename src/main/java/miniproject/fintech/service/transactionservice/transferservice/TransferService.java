package miniproject.fintech.service.transactionservice.transferservice;

import miniproject.fintech.domain.Transfer;
import miniproject.fintech.dto.TransferDto;

public interface TransferService {
    Transfer processTransfer(TransferDto transferDto);
}
