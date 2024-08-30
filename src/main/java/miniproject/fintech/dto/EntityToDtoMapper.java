package miniproject.fintech.dto;

import jakarta.persistence.EntityNotFoundException;
import miniproject.fintech.domain.Account;
import miniproject.fintech.domain.Deposit;
import miniproject.fintech.domain.Transaction;
import miniproject.fintech.domain.Transfer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Component
public class EntityToDtoMapper {

    // Account -> AccountDto 변환
    public AccountDto toAccountDto(Account account) {
        if (account == null) {
            throw new EntityNotFoundException("계좌가 null이면 안됩니다.");
        }

        return AccountDto.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .name(account.getName())
                .amount(account.getAmount())
                .createdAt(account.getCreatedAt())
                .deletedAt(account.getDeletedAt())
                .accountStatus(account.getAccountStatus())
                .deposits(convertToDepositDtos(account.getDeposits()))
                .sentTransfers(convertToTransferDtos(account.getSentTransfers()))
                .receivedTransfers(convertToTransferDtos(account.getReceivedTransfers()))
                .transactions(convertToTransactionDtos(account.getTransactions()))
                .build();
    }

    // Deposit -> DepositDto 변환
    public DepositDto toDepositDto(Deposit deposit) {
        if (deposit == null) {
            throw new EntityNotFoundException("Deposit은 null일 수 없습니다.");
        }

        Long accountId = deposit.getAccount() != null ? deposit.getAccount().getId() : null;

        return DepositDto.builder()
                .id(deposit.getId())
                .depositAmount(deposit.getDepositAmount())
                .depositAt(deposit.getDepositAt())
                .accountId(accountId)
                .depositStatus(deposit.getDepositStatus())
                .message(deposit.getMessage())
                .build();
    }

    // Transfer -> TransferDto 변환
    public TransferDto toTransferDto(Transfer transfer) {
        if (transfer == null) {
            throw new EntityNotFoundException("Transfer는 null일 수 없습니다.");
        }

        Long sourceAccountId = transfer.getSourceAccountId() != null ? transfer.getSourceAccountId() : null;
        Long destinationAccountId = transfer.getDestinationAccountId() != null ? transfer.getDestinationAccountId() : null;

        return TransferDto.builder()
                .id(transfer.getId())
                .transferAmount(transfer.getTransferAmount())
                .transferAt(transfer.getTransferAt())
                .sourceAccountId(sourceAccountId)
                .destinationAccountId(destinationAccountId)
                .transferStatus(transfer.getTransferStatus())
                .message(transfer.getMessage())
                .build();
    }

    // Transaction -> TransactionDto 변환
    public TransactionDto toTransactionDto(Transaction transaction) {
        if (transaction == null) {
            throw new EntityNotFoundException("Transaction은 null일 수 없습니다.");
        }

        Long bankMemberId = transaction.getBankMember() != null ? transaction.getBankMember().getId() : null;
        Long sourceAccountId = transaction.getSourceAccountId(); // assuming Transaction has SourceAccount reference

        return TransactionDto.builder()
                .id(transaction.getId())
                .transactionAmount(transaction.getTransactionAmount())
                .transactionType(transaction.getTransactionType())
                .transactionStatus(transaction.getTransactionStatus())
                .transactedAt(transaction.getTransactedAt())
                .curAmount(transaction.getCurAmount())
                .sourceAccountId(sourceAccountId)
                .referenceNumber(transaction.getReferenceNumber())
                .currency(transaction.getCurrency())
                .grade(transaction.getGrade())
                .counterpartyInfo(transaction.getCounterpartyInfo())
                .fee(transaction.getFee())
                .build();
    }

    // 리스트 변환
    public List<DepositDto> convertToDepositDtos(List<Deposit> deposits) {
        return deposits == null ? List.of() : deposits.stream()
                .map(this::toDepositDto)
                .collect(Collectors.toList());
    }

    public List<TransferDto> convertToTransferDtos(List<Transfer> transfers) {
        return transfers == null ? List.of() : transfers.stream()
                .map(this::toTransferDto)
                .collect(Collectors.toList());
    }

    public List<TransactionDto> convertToTransactionDtos(List<Transaction> transactions) {
        return transactions == null ? List.of() : transactions.stream()
                .map(this::toTransactionDto)
                .collect(Collectors.toList());
    }
}

