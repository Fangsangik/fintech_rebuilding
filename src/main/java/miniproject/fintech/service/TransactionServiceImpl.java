package miniproject.fintech.service;

import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.*;
import miniproject.fintech.dto.DtoConverter;
import miniproject.fintech.dto.TransactionDto;
import miniproject.fintech.error.CustomError;
import miniproject.fintech.repository.MemberRepository;
import miniproject.fintech.repository.TransactionRepository;
import miniproject.fintech.type.ErrorType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static miniproject.fintech.type.ErrorType.*;

@Slf4j
@Service
public class TransactionServiceImpl {

    private final TransactionRepository transactionRepository;
    private final DtoConverter dtoConverter;
    private final MemberRepository memberRepository;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository, DtoConverter dtoConverter, MemberRepository memberRepository) {
        this.transactionRepository = transactionRepository;
        this.dtoConverter = dtoConverter;
        this.memberRepository = memberRepository;
    }

    @Transactional(readOnly = true)
    public TransactionDto getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .map(dtoConverter::convertToTransactionDto)
                .orElseThrow(() -> new CustomError(TRANSACTION_NOT_FOUND));
    }


    @Transactional(readOnly = true)
    public List<TransactionDto> getAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();
        return transactions.stream()
                .map(dtoConverter::convertToTransactionDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransactionDto> getTransactionsByAccountNumber(String accountNumber) {
        List<Transaction> transactions = transactionRepository.findByAccountNumber(accountNumber);
        return transactions.stream()
                .map(dtoConverter::convertToTransactionDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransactionDto> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Transaction> transactions = transactionRepository.findByTransactedAtBetween(startDate, endDate);
        return transactions.stream()
                .map(dtoConverter::convertToTransactionDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransactionDto> getTransactionsByBankMember(String userId) {
        // 회원 정보 조회
        BankMember bankMember = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomError(ErrorType.MEMBER_NOT_FOUND));

        // 해당 회원과 관련된 거래 내역 조회
        List<Transaction> transactions = transactionRepository.findByBankMember(bankMember);

        return transactions.stream()
                .map(dtoConverter::convertToTransactionDto)
                .collect(Collectors.toList());
    }

}
