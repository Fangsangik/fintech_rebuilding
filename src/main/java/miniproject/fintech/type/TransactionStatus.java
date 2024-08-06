package miniproject.fintech.type;

public enum TransactionStatus {
    SUCCESS("성공"),
    FAIL("실패");

    private String message;

    TransactionStatus(String message) {
        this.message = message;
    }
}
