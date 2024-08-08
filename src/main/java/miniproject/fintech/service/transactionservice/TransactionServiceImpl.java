package miniproject.fintech.service.transactionservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.*;
import miniproject.fintech.dto.TransactionDto;
import miniproject.fintech.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService{

    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public Transaction createTransaction(TransactionDto transactionDto) {
        Transaction transaction = Transaction.builder()
                .transactionAmount(transactionDto.getTransactionAmount())
                .transactionType(transactionDto.getTransactionType())
                .transactionStatus(transactionDto.getTransactionStatus())
                .transactedAt(transactionDto.getTransactedAt())
                .curAmount(transactionDto.getCurAmount())
                .referenceNumber(transactionDto.getReferenceNumber())
                .currency(transactionDto.getCurrency())
                .grade(transactionDto.getGrade())
                .counterpartyInfo(transactionDto.getCounterpartyInfo())
                .fee(transactionDto.getFee())
                .build();

        return transactionRepository.save(transaction);
    }
}
