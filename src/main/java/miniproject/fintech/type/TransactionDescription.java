package miniproject.fintech.type;

public enum TransactionDescription {
    MESSAGE("메세지를 입력하세요");

    private String message;

    TransactionDescription(String message) {
        this.message = message;
    }
}
