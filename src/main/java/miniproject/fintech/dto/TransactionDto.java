package miniproject.fintech.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import miniproject.fintech.type.Grade;
import miniproject.fintech.type.TransactionStatus;
import miniproject.fintech.type.TransactionType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    @NotNull(message = "Transaction ID cannot be null")
    private Long id;

    @NotNull(message = "Bank member ID cannot be null")
    private Long bankMemberId;


    @Min(value = 1, message = "Transaction amount must be greater than 0")
    private long transactionAmount;

    @NotNull(message = "Transaction type cannot be null")
    private TransactionType transactionType;

    @NotNull(message = "Transaction status cannot be null")
    private TransactionStatus transactionStatus;

    @NotNull(message = "Transaction date cannot be null")
    private LocalDateTime transactedAt;

    @Min(value = 0, message = "Current amount must be non-negative")
    private long curAmount;

    @NotNull(message = "Source account ID cannot be null")
    private Long sourceAccountId;

    @NotEmpty(message = "Reference number cannot be empty")
    private String referenceNumber;

    @NotEmpty(message = "Currency cannot be empty")
    private String currency;

    @NotNull(message = "Grade cannot be null")
    private Grade grade;

    private String counterpartyInfo;

    @Min(value = 0, message = "Fee must be non-negative")
    private double fee;
}