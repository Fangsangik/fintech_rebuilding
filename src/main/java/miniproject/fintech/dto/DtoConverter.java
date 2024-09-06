package miniproject.fintech.dto;

import miniproject.fintech.domain.*;
import miniproject.fintech.error.CustomError;
import miniproject.fintech.repository.MemberRepository;
import miniproject.fintech.type.ErrorType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static miniproject.fintech.type.ErrorType.MEMBER_NOT_FOUND;


@Component
public class DtoConverter {

    private final MemberRepository memberRepository;

    public DtoConverter(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public AdminDto convertToAdminDto(Admin admin) {
        if (admin == null) {
            return null;
        }

        return AdminDto.builder()
                .id(admin.getId())
                .adminId(admin.getAdminId())
                .password(admin.getPassword())
                .name(admin.getName())
                .email(admin.getEmail())
                .roles(admin.getRoles())
                .superAdmin(admin.isSuperAdmin())
                .build();
    }

    public AccountDto convertToAccountDto(Account account) {
        if (account == null) return null;

        if (account.getId() == null) {
            throw new CustomError(ErrorType.ACCOUNT_ID_NOT_FOUND);
        }

        return AccountDto.builder()
                .bankMemberId(account.getBankMember().getUserId())
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
                .userId(bankMember.getUserId())
                .id(bankMember.getId())
                .name(bankMember.getName())
                .amount(bankMember.getAmount())
                .email(bankMember.getEmail())
                .curAmount(bankMember.getCurAmount())
                .createdAt(bankMember.getCreatedAt())
                .isActive(bankMember.isActive())
                .roles(bankMember.getRoles())
                .userId(bankMember.getUserId())
                .deletedAt(bankMember.getDeletedAt())
                .build();
    }

    public DepositDto convertToDepositDto(Deposit deposit) {
        if (deposit == null) return null;

        return DepositDto.builder()
                .id(deposit.getId())
                .transactionId(deposit.getTransaction() != null ? deposit.getTransaction().getId() : null)  // Transaction ID 설정
                .depositAt(deposit.getDepositAt())
                .depositAmount(deposit.getDepositAmount())
                .destinationAccountNumber(deposit.getDestinationAccountNumber())
                .depositAt(deposit.getDepositAt())
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

        // Transaction 객체에서 userId를 가져옴 (이미 객체에 있음)
        String bankMemberId = transaction.getBankMember().getUserId();

        return TransactionDto.builder()
                .id(transaction.getId())
                .bankMemberId(bankMemberId)  // BankMember ID 설정
                .transactionAmount(transaction.getTransactionAmount())
                .transactedAt(transaction.getTransactedAt())
                .sourceAccountNumber(transaction.getSourceAccountNumber())
                .destinationAccountNumber(transaction.getDestinationAccountNumber())
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

        return TransferDto.builder()
                .id(transfer.getId())
                .transferAmount(transfer.getTransferAmount())
                .transferAt(transfer.getTransferAt())
                .sourceAccountNumber(transfer.getSourceAccountNumber())
                .destinationAccountNumber(transfer.getDestinationAccountNumber())
                .transferStatus(transfer.getTransferStatus())
                .transactionId(transfer.getTransaction() != null ? transfer.getTransaction().getId() : null)  // Transaction ID 설정
                .message(transfer.getMessage())
                .build();
    }

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

