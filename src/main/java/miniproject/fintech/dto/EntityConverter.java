package miniproject.fintech.dto;

import miniproject.fintech.domain.*;
import miniproject.fintech.error.CustomError;
import miniproject.fintech.repository.AccountRepository;
import miniproject.fintech.type.ErrorType;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EntityConverter {

    private final AccountRepository accountRepository;

    public EntityConverter(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    // DTO를 엔티티로 변환
    public Account convertToAccount(AccountDto accountDto) {
        if (accountDto == null) return null;

        return Account.builder()
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
                .id(bankMemberDto.getId())
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
                .depositAmount(depositDto.getDepositAmount())
                .depositAt(depositDto.getDepositAt())
                .account(account)
                .depositStatus(depositDto.getDepositStatus())
                .message(depositDto.getMessage())
                .build();
    }

    public Transaction convertToTransaction(TransactionDto transactionDto) {
        if (transactionDto == null) return null;

        return Transaction.builder()
                .id(transactionDto.getId())
                .transactionAmount(transactionDto.getTransactionAmount())
                .transactedAt(transactionDto.getTransactedAt())
                .sourceAccountId(transactionDto.getSourceAccountId())
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

// DTO에서 ID를 가져와서 Account 엔티티로 변환하는 과정 추가 필요
        Account sourceAccount = accountRepository.findById(transferDto.getSourceAccountId())
                .orElseThrow(() -> new CustomError(ErrorType.ACCOUNT_ID_NOT_FOUND));
        Account destinationAccount = accountRepository.findById(transferDto.getDestinationAccountId())
                .orElseThrow(() -> new CustomError(ErrorType.ACCOUNT_ID_NOT_FOUND));


        return Transfer.builder()
                .id(transferDto.getId())
                .transferAmount(transferDto.getTransferAmount())
                .transferAt(transferDto.getTransferAt())
                .sourceAccount(sourceAccount)
                .destinationAccount(destinationAccount)
                .transferStatus(transferDto.getTransferStatus())
                .message(transferDto.getMessage())
                .build();
    }

    public List<BankMember> convertToBankMemberList(List<BankMemberDto> bankMemberDtos) {
        if (bankMemberDtos == null || bankMemberDtos.isEmpty()) {
            return null;
        }

        return bankMemberDtos.stream()
                .map(this::convertToBankMember)
                .collect(Collectors.toUnmodifiableList());
    }

    public List<Transaction> convertToTransactionList(List<TransactionDto> transactionDtos) {
        if (transactionDtos == null || transactionDtos.isEmpty()) {
            return Collections.emptyList();
        }

        return transactionDtos.stream()
                .map(this::convertToTransaction)
                .collect(Collectors.toList());
    }
}
