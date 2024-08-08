package miniproject.fintech.type;

import lombok.Getter;

@Getter
public enum DepositStatus {
    WAITING("대기중"),
    COMPLETED("완료"),
    FAILED("실패");

    private final String message;

    DepositStatus(String message) {
        this.message = message;
    }
}
