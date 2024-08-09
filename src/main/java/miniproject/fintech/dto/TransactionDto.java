package miniproject.fintech.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import miniproject.fintech.type.Grade;
import miniproject.fintech.type.TransactionStatus;
import miniproject.fintech.type.TransactionType;

import java.time.LocalDateTime;

@Getter
@Builder (toBuilder = true) //setter 사용을 줄여보기 위해 사용
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
