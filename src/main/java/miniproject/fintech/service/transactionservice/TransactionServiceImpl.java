package miniproject.fintech.service.transactionservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.*;
import miniproject.fintech.dto.TransactionDto;
import miniproject.fintech.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Override
    @Transactional(readOnly = true) //읽기전용
    public Optional<Transaction> getTransactionById(Long transactionId, BankMember bankMember) {
        Transaction findTransaction = validationOfId(transactionId, bankMember);
        return Optional.ofNullable(findTransaction);
    }

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

    @Override
    public List<Transaction> getAllTransaction() {
        return transactionRepository.findAll();
    }

    @Override
    @Transactional
    public Transaction updateTransaction(Long transactionId, TransactionDto transactionDto, BankMember bankMember) {
        Transaction existingTransaction = validationOfId(transactionId, bankMember);

        Transaction updatedeTransaction = existingTransaction.toBuilder()
                .transactedAt(transactionDto.getTransactedAt())
                .transactionAmount(transactionDto.getTransactionAmount())
                .transactionStatus(transactionDto.getTransactionStatus())
                .curAmount(transactionDto.getCurAmount())
                .referenceNumber(transactionDto.getReferenceNumber())
                .grade(transactionDto.getGrade())
                .build();


        return transactionRepository.save(updatedeTransaction);
    }

    @Override
    @Transactional
    public void deleteTransaction(Long transactionId, BankMember bankMember) {
        Transaction existingTransaction = validationOfId(transactionId, bankMember);
        transactionRepository.delete(existingTransaction);

        /**
         * overhead 발생??
         */
        boolean exists = transactionRepository.existsById(transactionId);

        if (exists) {
            throw new IllegalArgumentException("거래 삭제에 실패했습니다.");
        }
    }

    private Transaction validationOfId(Long transactionId, BankMember bankMember) {
        if (transactionId == null) {
            throw new IllegalArgumentException("잘못된 값입니다.");
        }

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new EntityNotFoundException("트랜잭션을 찾을 수 없습니다."));

        if (!transaction.getBankMember().equals(bankMember)) {
            throw new IllegalArgumentException("잘못된 접근입니다.");
        }

        return transaction;
    }
}
