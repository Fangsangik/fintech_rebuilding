package miniproject.fintech.dto;

import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.domain.*;
import miniproject.fintech.error.CustomError;
import miniproject.fintech.repository.AccountRepository;
import miniproject.fintech.repository.MemberRepository;
import miniproject.fintech.type.ErrorType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class EntityConverter {

    private final AccountRepository accountRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public EntityConverter(AccountRepository accountRepository, MemberRepository memberRepository
    , PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Account convertToAccount(AccountDto accountDto) {
        if (accountDto == null) {
            log.error("AccountDto가 null입니다.");
            return null; // 이 부분이 문제라면 null을 반환하지 않도록 수정 필요
        }

        // 필요한 필드들이 null이 아닌지 확인
        if (accountDto.getAccountNumber() == null) {
            log.error("계좌 번호가 null입니다.");
            throw new CustomError(ErrorType.ACCOUNT_NUMBER_NULL);
        }

        BankMember bankMember = memberRepository.findByUserId(accountDto.getBankMemberId())
                .orElseThrow(() -> new CustomError(ErrorType.MEMBER_NOT_FOUND));

        return Account.builder()
                .bankMember(bankMember)
                .id(accountDto.getId())
                .name(accountDto.getName())
                .accountNumber(accountDto.getAccountNumber())
                .amount(accountDto.getAmount())
                .createdAt(accountDto.getCreatedAt())
                .deletedAt(accountDto.getDeletedAt())
                .accountStatus(accountDto.getAccountStatus())
                .build();
    }



    public BankMember convertToBankMember(BankMemberDto bankMemberDto) {
        if (bankMemberDto == null) return null;

        return BankMember.builder()
                .userId(bankMemberDto.getUserId())
                .id(bankMemberDto.getId())
                .userId(bankMemberDto.getUserId())
                .name(bankMemberDto.getName())
                .isActive(bankMemberDto.isActive())
                .email(bankMemberDto.getEmail())
                .amount(bankMemberDto.getAmount())
                .createdAt(bankMemberDto.getCreatedAt())
                .deletedAt(bankMemberDto.getDeletedAt())
                .roles(bankMemberDto.getRoles())
                .build();
    }

    public Deposit convertToDeposit(DepositDto depositDto, Account account) {
        if (depositDto == null || account == null) return null;

        return Deposit.builder()
                .id(depositDto.getId())
                .sourceAccountNumber(depositDto.getSourceAccountNumber())
                .destinationAccountNumber(depositDto.getDestinationAccountNumber())
                .sourceAccountNumber(depositDto.getSourceAccountNumber())
                .depositAmount(depositDto.getDepositAmount())
                .depositAt(depositDto.getDepositAt())
                .account(account)
                .depositStatus(depositDto.getDepositStatus())
                .message(depositDto.getMessage())
                .build();
    }

    public Transaction convertToTransaction(TransactionDto transactionDto) {
        if (transactionDto == null) return null;

        // DTO에서 userId를 가져와 데이터베이스에서 BankMember 조회
        BankMember bankMember = memberRepository.findByUserId(transactionDto.getBankMemberId())
                .orElseThrow(() -> new CustomError(ErrorType.MEMBER_NOT_FOUND));

        return Transaction.builder()
                .id(transactionDto.getId())
                .bankMember(bankMember)  // BankMember 설정
                .transactionAmount(transactionDto.getTransactionAmount())
                .transactedAt(transactionDto.getTransactedAt())
                .sourceAccountNumber(transactionDto.getSourceAccountNumber())
                .destinationAccountNumber(transactionDto.getDestinationAccountNumber())
                .curAmount(transactionDto.getCurAmount())
                .transactionType(transactionDto.getTransactionType())
                .transactionStatus(transactionDto.getTransactionStatus())
                .referenceNumber(transactionDto.getReferenceNumber())
                .currency(transactionDto.getCurrency())
                .grade(transactionDto.getGrade())
                .counterpartyInfo(transactionDto.getCounterpartyInfo())
                .fee(transactionDto.getFee())
                .build();
    }

    public Transfer convertToTransfer(TransferDto transferDto) {
        if (transferDto == null) return null;

        Account sourceAccount = accountRepository.findByAccountNumber(transferDto.getSourceAccountNumber())
                .orElseThrow(() -> new CustomError(ErrorType.ACCOUNT_ID_NOT_FOUND));
        Account destinationAccount = accountRepository.findByAccountNumber(transferDto.getDestinationAccountNumber())
                .orElseThrow(() -> new CustomError(ErrorType.ACCOUNT_ID_NOT_FOUND));

        return Transfer.builder()
                .id(transferDto.getId())
                .transferAmount(transferDto.getTransferAmount())
                .transferAt(transferDto.getTransferAt())
                .sourceAccountNumber(transferDto.getSourceAccountNumber())
                .destinationAccountNumber(transferDto.getDestinationAccountNumber())
                .transferStatus(transferDto.getTransferStatus())
                .message(transferDto.getMessage())
                .build();
    }

    public List<BankMember> convertToBankMemberList(List<BankMemberDto> bankMemberDtos) {
        if (bankMemberDtos == null || bankMemberDtos.isEmpty()) {
            return Collections.emptyList();
        }

        return bankMemberDtos.stream()
                .map(this::convertToBankMember)
                .collect(Collectors.toList());
    }

    public List<Transaction> convertToTransactionList(List<TransactionDto> transactionDtos, BankMember bankMember) {
        if (transactionDtos == null || transactionDtos.isEmpty()) {
            return Collections.emptyList();
        }

        return transactionDtos.stream()
                .map(this::convertToTransaction)
                .collect(Collectors.toList());
    }

    public void updateBankMemberFromDto(BankMemberDto dto, BankMember entity) {
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            String newPassword = passwordEncoder.encode(dto.getPassword());
            entity.setPassword(newPassword);
        }
        entity.setUserId(dto.getUserId());
        entity.setName(dto.getName());
        entity.setAddress(dto.getAddress());
        entity.setEmail(dto.getEmail());
        entity.setAge(dto.getAge());
        entity.setCurAmount(dto.getCurAmount());
        entity.setBirth(dto.getBirth());
        entity.setGrade(dto.getGrade());
    }
}
