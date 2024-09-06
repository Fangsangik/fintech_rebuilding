package miniproject.fintech.domain;

import jakarta.persistence.*;
import lombok.*;
import miniproject.fintech.type.Grade;
import miniproject.fintech.type.TransactionDescription;
import miniproject.fintech.type.TransactionStatus;
import miniproject.fintech.type.TransactionType;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
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

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "bank_member_id", nullable = false)
    private BankMember bankMember;

    private String sourceAccountNumber;
    private String destinationAccountNumber;

    @Enumerated(EnumType.STRING)
    private Grade grade;

    @Enumerated(EnumType.STRING)
    private TransactionDescription transactionDescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @OneToOne(mappedBy = "transaction", cascade = CascadeType.ALL)
    private Deposit deposit;  // 1:1 관계

    @OneToOne(mappedBy = "transaction", cascade = CascadeType.ALL)
    private Transfer transfer;  // 1:1 관계

    private double fee;
    private String message;
    private String counterpartyInfo;
}
