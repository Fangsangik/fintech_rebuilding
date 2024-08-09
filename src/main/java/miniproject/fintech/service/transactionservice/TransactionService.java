package miniproject.fintech.service.transactionservice;

import miniproject.fintech.domain.BankMember;
import miniproject.fintech.domain.Transaction;
import miniproject.fintech.dto.TransactionDto;
import miniproject.fintech.type.TransactionStatus;

import java.util.List;
import java.util.Optional;

public interface TransactionService {
    Transaction createTransaction(TransactionDto transactionDto);

    Optional<Transaction> getTransactionById (Long transactionId, BankMember bankMember);

    List<Transaction> getAllTransaction();

    Transaction updateTransaction(Long transactionId, TransactionDto transactionDto, BankMember bankMember);

    void deleteTransaction(Long transactionId, BankMember bankMember);
}
