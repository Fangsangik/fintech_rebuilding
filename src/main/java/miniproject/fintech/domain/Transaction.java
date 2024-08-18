package miniproject.fintech.domain;

import lombok.*;
import miniproject.fintech.type.Grade;
import miniproject.fintech.type.TransactionDescription;
import miniproject.fintech.type.TransactionStatus;
import miniproject.fintech.type.TransactionType;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true) //setter 사용을 줄여보기 위해 사용
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

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_member_id", nullable = false)
    private BankMember bankMember;

    private Long sourceAccountId;

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
