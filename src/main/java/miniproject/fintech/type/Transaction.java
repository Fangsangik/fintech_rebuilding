package miniproject.fintech.type;

import miniproject.fintech.type.TransactionStatus;
import miniproject.fintech.type.TransactionType;

import java.time.LocalDateTime;

public class Transaction {

    private Long id;
    private TransactionType transactionType;
    private TransactionStatus transactionStatus;
    private long transactionAmount;
    private LocalDateTime transactionDate;
    private long curAmount;

}
