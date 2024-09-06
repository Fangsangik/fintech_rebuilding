package miniproject.fintech.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import miniproject.fintech.type.Grade;
import miniproject.fintech.type.TransactionStatus;
import miniproject.fintech.type.TransactionType;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    private Long id;

    private String bankMemberId;
    private long transactionAmount;
    private TransactionType transactionType;
    private TransactionStatus transactionStatus;
    private LocalDateTime transactedAt;
    private long curAmount;
    private String sourceAccountNumber;
    private String destinationAccountNumber;
    private String referenceNumber;
    private String currency;
    private Grade grade;
    private String counterpartyInfo;
    private double fee;
}
