package miniproject.fintech.dto;

import miniproject.fintech.domain.*;
import org.springframework.stereotype.Component;

@Component
public class EntityConverter {

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
                .accountNumber(bankMemberDto.getAccountNumber())
                .amount(bankMemberDto.getAmount())
                .createdAt(bankMemberDto.getCreatedAt())
                .deletedAt(bankMemberDto.getDeletedAt())
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

    public Transaction convertToTransaction(TransactionDto transactionDto, BankMember bankMember) {
        if (transactionDto == null || bankMember == null) return null;

        return Transaction.builder()
                .bankMember(bankMember)
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

    public Transfer convertToTransfer(TransferDto transferDto, Account sourceAccount, Account destinationAccount) {
        if (transferDto == null || sourceAccount == null || destinationAccount == null) return null;

        return Transfer.builder()
                .id(transferDto.getId())
                .transferAmount(transferDto.getTransferAmount())
                .transferAt(transferDto.getTransferAt())
                .sourceAccountId(transferDto.getSourceAccountId())
                .destinationAccountId(transferDto.getDestinationAccountId())
                .transferStatus(transferDto.getTransferStatus())
                .message(transferDto.getMessage())
                .build();
    }
}
