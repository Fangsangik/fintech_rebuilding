package miniproject.fintech.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import miniproject.fintech.type.Grade;
import miniproject.fintech.type.TransactionDescription;
import miniproject.fintech.type.TransactionStatus;
import miniproject.fintech.type.TransactionType;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long transactionAmount;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;

    private LocalDateTime transactedAt;
    private long curAmount;
    private String referenceNumber;
    private String currency;

    @Enumerated(EnumType.STRING)
    private Grade grade;

    @Enumerated(EnumType.STRING)
    private TransactionDescription transactionDescription;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    private double fee;
    private String message;
    private String counterpartyInfo;
}
