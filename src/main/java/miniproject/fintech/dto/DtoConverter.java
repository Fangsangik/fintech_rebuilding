package miniproject.fintech.dto;

import miniproject.fintech.domain.*;
import miniproject.fintech.error.CustomError;
import miniproject.fintech.type.ErrorType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DtoConverter {

    public AdminDto convertToAdminDto(Admin admin) {
        if (admin == null) {
            return null;
        }

        return AdminDto.builder()
                .id(admin.getId())
                .password(admin.getPassword())
                .name(admin.getName())
                .email(admin.getEmail())
                .roles(admin.getRoles())
                .superAdmin(admin.isSuperAdmin())
                .build();
    }


    // 엔티티를 DTO로 변환
    public AccountDto convertToAccountDto(Account account) {
        if (account == null) return null;

        return AccountDto.builder()
                .id(account.getId())
                .name(account.getName())
                .accountNumber(account.getAccountNumber())
                .amount(account.getAmount())
                .createdAt(account.getCreatedAt())
                .deletedAt(account.getDeletedAt())
                .accountStatus(account.getAccountStatus())
                .build();
    }

    public BankMemberDto convertToBankMemberDto(BankMember bankMember) {
        if (bankMember == null) return null;

        return BankMemberDto.builder()
                .id(bankMember.getId())
                .name(bankMember.getName())
                .amount(bankMember.getAmount())
                .email(bankMember.getEmail())
                .curAmount(bankMember.getCurAmount())
                .createdAt(bankMember.getCreatedAt())
                .isActive(bankMember.isActive())
                .roles(bankMember.getRoles())
                .deletedAt(bankMember.getDeletedAt())
                .build();
    }

    public DepositDto convertToDepositDto(Deposit deposit) {
        if (deposit == null) return null;

        return DepositDto.builder()
                .id(deposit.getId())
                .depositAmount(deposit.getDepositAmount())
                .depositAt(deposit.getDepositAt())
                .accountId(deposit.getAccount().getId())
                .depositStatus(deposit.getDepositStatus())
                .message(deposit.getMessage())
                .build();


    }

    public List<DepositDto> convertToDepositDtoList(List<Deposit> deposits) {
        return deposits.stream()
                .map(this::convertToDepositDto)
                .collect(Collectors.toList());
    }

    public TransactionDto convertToTransactionDto(Transaction transaction) {
        if (transaction == null) return null;

        return TransactionDto.builder()
                .id(transaction.getBankMember().getId())
                .transactionAmount(transaction.getTransactionAmount())
                .transactedAt(transaction.getTransactedAt())
                .sourceAccountId(transaction.getSourceAccountId())
                .curAmount(transaction.getCurAmount())
                .transactionType(transaction.getTransactionType())
                .transactionStatus(transaction.getTransactionStatus())
                .referenceNumber(transaction.getReferenceNumber())
                .currency(transaction.getCurrency())
                .grade(transaction.getGrade())
                .counterpartyInfo(transaction.getCounterpartyInfo())
                .fee(transaction.getFee())
                .build();
    }

    public TransferDto convertToTransferDto(Transfer transfer) {
        if (transfer == null) {
            throw new CustomError(ErrorType.TRANSFER_NOT_FOUND);
        }

        Long sourceAccountId = transfer.getSourceAccount() != null ? transfer.getSourceAccount().getId() : null;
        Long destinationAccountId = transfer.getDestinationAccount() != null ? transfer.getDestinationAccount().getId() : null;

        return TransferDto.builder()
                .id(transfer.getId())
                .transferAmount(transfer.getTransferAmount())
                .transferAt(transfer.getTransferAt())
                .sourceAccountId(sourceAccountId) // ID만 사용
                .destinationAccountId(destinationAccountId) // ID만 사용
                .transferStatus(transfer.getTransferStatus())
                .message(transfer.getMessage())
                .build();
    }

    // 엔티티 리스트를 DTO 리스트로 변환
    public List<AccountDto> convertToAccountDtoList(List<Account> accounts) {
        if (accounts == null || accounts.isEmpty()) return null;

        return accounts.stream()
                .map(this::convertToAccountDto)
                .collect(Collectors.toList());
    }

    public List<BankMemberDto> convertToBankMemberDtoList(List<BankMember> bankMembers) {
        if (bankMembers == null || bankMembers.isEmpty()) return null;

        return bankMembers.stream()
                .map(this::convertToBankMemberDto)
                .collect(Collectors.toList());
    }

    public List<TransactionDto> convertToTransactionDtoList(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) return null;

        return transactions.stream()
                .map(this::convertToTransactionDto)
                .collect(Collectors.toList());
    }
}
