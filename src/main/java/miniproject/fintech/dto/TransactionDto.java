package miniproject.fintech.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import miniproject.fintech.type.Grade;
import miniproject.fintech.type.TransactionStatus;
import miniproject.fintech.type.TransactionType;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class TransactionDto {

    private long transactionAmount;
    private TransactionType transactionType;
    private TransactionStatus transactionStatus;
    private LocalDateTime transactedAt;
    private long curAmount;
    private Long sourceAccountId;
    private String referenceNumber;
    private String currency;
    private Grade grade;
    private String counterpartyInfo;
    private double fee;
}
