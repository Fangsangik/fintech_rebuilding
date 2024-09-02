package miniproject.fintech.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.*;
import miniproject.fintech.dto.BankMemberDto;
import miniproject.fintech.dto.DtoConverter;
import miniproject.fintech.dto.TransactionDto;
import miniproject.fintech.error.CustomError;
import miniproject.fintech.repository.MemberRepository;
import miniproject.fintech.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static miniproject.fintech.type.ErrorType.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl {

    private final TransactionRepository transactionRepository;
    private final MemberRepository memberRepository;
    private final DtoConverter dtoConverter;

    @Transactional(readOnly = true)
    public Optional<TransactionDto> getTransactionById(Long transactionId, BankMemberDto bankMemberDto) {
        log.info("거래 조회 시작: 거래 ID = {}, 사용자 ID = {}", transactionId, bankMemberDto.getId());

        // 유효성 검사를 위한 검증 메서드 호출
        Transaction transaction = validationOfId(transactionId, bankMemberDto);

        log.info("거래 조회 완료: 거래 ID = {}, 거래 정보 = {}", transactionId, transaction);
        return Optional.of(dtoConverter.convertToTransactionDto(transaction));
    }

    @Transactional(readOnly = true)
    public List<TransactionDto> getAllTransactionsByMemberId(Long memberId) {
        log.info("특정 회원의 모든 거래 조회 시작: 회원 ID = {}", memberId);
        List<Transaction> transactions = transactionRepository.findByBankMemberId(memberId);
        List<TransactionDto> transactionDtos = transactions.stream()
                .map(dtoConverter::convertToTransactionDto)
                .collect(Collectors.toList());
        log.info("특정 회원의 모든 거래 조회 완료: 거래 수 = {}", transactions.size());
        return transactionDtos;
    }

    @Transactional
    public TransactionDto createTransaction(TransactionDto transactionDto) {
        log.info("거래 생성 시작: 거래 DTO = {}", transactionDto);

        BankMember bankMember = memberRepository.findById(transactionDto.getId())
                .orElseThrow(() -> new CustomError(MEMBER_NOT_FOUND));

        Transaction transaction = getTransaction(transactionDto, bankMember);

        Transaction savedTransaction = transactionRepository.save(transaction);

        log.info("거래 생성 완료: 거래 ID = {}", savedTransaction.getId());
        return dtoConverter.convertToTransactionDto(savedTransaction);
    }


    @Transactional
    public TransactionDto updateTransaction(Long transactionId, TransactionDto transactionDto, BankMemberDto bankMemberDto) {
        log.info("거래 업데이트 시작: 거래 ID = {}, 거래 DTO = {}", transactionId, transactionDto);
        Transaction existingTransaction = validationOfId(transactionId, bankMemberDto);

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
        return dtoConverter.convertToTransactionDto(savedTransaction);
    }

    @Transactional
    public void deleteTransaction(TransactionDto transactionDto, BankMemberDto bankMember) {
        log.info("거래 삭제 시작: 거래 ID = {}", transactionDto.getId());
        Transaction existingTransaction = validationOfId(transactionDto.getId(), bankMember);
        transactionRepository.delete(existingTransaction);
        log.info("거래 삭제 완료: 거래 ID = {}", transactionDto.getId());
    }

    @Transactional(readOnly = true)
    public List<TransactionDto> getAllTransactions() {
        log.info("모든 거래 조회 시작");
        List<Transaction> transactions = transactionRepository.findAll();
        List<TransactionDto> transactionDtos = transactions.stream()
                .map(dtoConverter::convertToTransactionDto)
                .collect(Collectors.toList());
        log.info("모든 거래 조회 완료: 거래 수 = {}", transactions.size());
        return transactionDtos;
    }

    @Transactional(readOnly = true)
    public Page<TransactionDto> getTransactionsByAccountId(Long accountId, Pageable pageable) {
        log.info("계좌별 거래 조회 시작: 계좌 ID = {}", accountId);
        Page<Transaction> transactions = transactionRepository.findByAccount_Id(accountId, pageable);
        Page<TransactionDto> transactionDtos = transactions.map(dtoConverter::convertToTransactionDto);
        log.info("계좌별 거래 조회 완료: 거래 수 = {}", transactions.getTotalElements());
        return transactionDtos;
    }

    @Transactional(readOnly = true)
    public Page<TransactionDto> getTransactionsByMemberId(Long memberId, Pageable pageable) {
        log.info("회원별 거래 조회 시작: 회원 ID = {}", memberId);
        Page<Transaction> transactions = transactionRepository.findByBankMemberId(memberId, pageable);
        Page<TransactionDto> transactionDtos = transactions.map(dtoConverter::convertToTransactionDto);
        log.info("회원별 거래 조회 완료: 거래 수 = {}", transactions.getTotalElements());
        return transactionDtos;
    }

    @Transactional(readOnly = true)
    public Page<TransactionDto> getAllTransactions(Pageable pageable) {
        log.info("모든 거래 조회 (페이징) 시작");
        Page<Transaction> transactions = transactionRepository.findAll(pageable);
        Page<TransactionDto> transactionDtos = transactions.map(dtoConverter::convertToTransactionDto);
        log.info("모든 거래 조회 완료: 거래 수 = {}", transactions.getTotalElements());
        return transactionDtos;
    }

    @Transactional(readOnly = true)
    public Page<TransactionDto> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        log.info("기간별 거래 조회 시작: 시작일 = {}, 종료일 = {}", startDate, endDate);
        Page<Transaction> transactions = transactionRepository.findByTransactedAtBetween(startDate, endDate, pageable);
        Page<TransactionDto> transactionDtos = transactions.map(dtoConverter::convertToTransactionDto);
        log.info("기간별 거래 조회 완료: 거래 수 = {}", transactions.getTotalElements());
        return transactionDtos;
    }

    private Transaction validationOfId(Long transactionId, BankMemberDto bankMemberDto) {
        if (transactionId == null) {
            log.error("거래 ID가 null입니다.");
            throw new CustomError(IN_CORRECT);
        }

        if (bankMemberDto.getId() == null) {
            log.error("bankMember ID가 null입니다.");
            throw new CustomError(IN_CORRECT);
        }

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> {
                    log.error("거래를 찾을 수 없습니다: 거래 ID = {}", transactionId);
                    return new CustomError(TRANSACTION_NOT_FOUND);
                });

        // 사용자가 해당 거래에 접근할 수 있는 권한이 있는지 확인
        if (!transaction.getBankMember().getId().equals(bankMemberDto.getId())) {
            log.error("권한이 없는 사용자 접근 시도: 거래 ID = {}, 사용자 ID = {}", transactionId, bankMemberDto.getId());
            throw new CustomError(NOT_ALLOWED_ACCESS);
        }

        return transaction;
    }

    private static Transaction getTransaction(TransactionDto transactionDto, BankMember bankMember) {
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
        return transaction;
    }
}
