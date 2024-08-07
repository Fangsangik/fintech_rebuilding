package miniproject.fintech.type;

import lombok.Getter;

@Getter
public enum TransactionStatus {
    SUCCESS("성공"),
    FAIL("실패");

    private String message;

    TransactionStatus(String message) {
        this.message = message;
    }
}
