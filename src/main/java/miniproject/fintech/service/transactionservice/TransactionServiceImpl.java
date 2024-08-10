package miniproject.fintech.service.transactionservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.*;
import miniproject.fintech.dto.TransactionDto;
import miniproject.fintech.error.CustomError;
import miniproject.fintech.repository.MemberRepository;
import miniproject.fintech.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static miniproject.fintech.type.ErrorType.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final MemberRepository memberRepository;


    @Override
    @Transactional(readOnly = true)
    public Optional<Transaction> getTransactionById(Long transactionId, BankMember bankMember) {
        log.info("거래 조회 시작: 거래 ID = {}, 사용자 ID = {}", transactionId, bankMember.getId());
        Transaction findTransaction = validationOfId(transactionId, bankMember);
        log.info("거래 조회 완료: 거래 ID = {}, 거래 정보 = {}", transactionId, findTransaction);
        return Optional.ofNullable(findTransaction);
    }

    @Override
    @Transactional
    public Transaction createTransaction(TransactionDto transactionDto) {
        log.info("거래 생성 시작: 거래 DTO = {}", transactionDto);

        BankMember bankMember = memberRepository.findById(transactionDto.getBankMemberId())
                .orElseThrow(() -> new CustomError(MEMBER_NOT_FOUND));

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
                .bankMember(bankMember)
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);

        log.info("거래 생성 완료: 거래 ID = {}", savedTransaction.getId());
        return savedTransaction;
    }

    @Override
    @Transactional
    public Transaction updateTransaction(Long transactionId, TransactionDto transactionDto, BankMember bankMember) {
        log.info("거래 업데이트 시작: 거래 ID = {}, 거래 DTO = {}", transactionId, transactionDto);
        Transaction existingTransaction = validationOfId(transactionId, bankMember);

        // 업데이트할 거래 생성
        Transaction updatedTransaction = existingTransaction.toBuilder()
                .transactedAt(transactionDto.getTransactedAt())
                .transactionAmount(transactionDto.getTransactionAmount())
                .transactionStatus(transactionDto.getTransactionStatus())
                .curAmount(transactionDto.getCurAmount())
                .referenceNumber(transactionDto.getReferenceNumber())
                .grade(transactionDto.getGrade())
                .build();

        Transaction savedTransaction = transactionRepository.save(updatedTransaction);

        log.info("거래 업데이트 완료: 거래 ID = {}", savedTransaction.getId());
        return savedTransaction;
    }

    @Override
    @Transactional
    public void deleteTransaction(Long transactionId, BankMember bankMember) {
        log.info("거래 삭제 시작: 거래 ID = {}", transactionId);
        Transaction existingTransaction = validationOfId(transactionId, bankMember);
        transactionRepository.delete(existingTransaction);
        log.info("거래 삭제 완료: 거래 ID = {}", transactionId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getAllTransaction() {
        log.info("모든 거래 조회 시작");
        List<Transaction> transactions = transactionRepository.findAll();
        log.info("모든 거래 조회 완료: 거래 수 = {}", transactions.size());
        return transactions;
    }

    private Transaction validationOfId(Long transactionId, BankMember bankMember) {
        if (transactionId == null) {
            log.error("거래 ID가 null입니다.");
            throw new CustomError(IN_CORRECT);
        }

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> {
                    log.error("거래를 찾을 수 없습니다: 거래 ID = {}", transactionId);
                    return new CustomError(TRANSACTION_NOT_FOUND);
                });

        // 사용자가 해당 거래에 접근할 수 있는 권한이 있는지 확인
        if (!transaction.getBankMember().equals(bankMember)) {
            log.error("권한이 없는 사용자 접근 시도: 거래 ID = {}, 사용자 ID = {}", transactionId, bankMember.getId());
            throw new CustomError(NOT_ALLOWED_ACCESS);
        }

        return transaction;
    }
}