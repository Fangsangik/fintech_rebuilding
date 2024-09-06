package miniproject.fintech.type;

public enum TransactionType {
    TRANSFER("송금"),
    WITHDRAW("인출"),
    DEPOSIT("입금"),
    RECEIVE("받기");

    private final String transactionMessage;

    TransactionType(String transactionMessage) {
        this.transactionMessage = transactionMessage;
    }
}
