package miniproject.fintech.service.transactionservice;

import miniproject.fintech.domain.Transaction;
import miniproject.fintech.dto.TransactionDto;

public interface TransactionService {
    Transaction createTransaction(TransactionDto transactionDto);
}
