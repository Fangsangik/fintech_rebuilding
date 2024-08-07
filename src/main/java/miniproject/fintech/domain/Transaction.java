package miniproject.fintech.domain;

import lombok.RequiredArgsConstructor;
import miniproject.fintech.type.TransactionStatus;
import miniproject.fintech.type.TransactionType;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@RequiredArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;

    private long transactionAmount;
    private LocalDateTime transactionDate;
    private long curAmount;
}
