package miniproject.fintech.tyoe;

import lombok.Getter;

@Getter
public enum MemberStatus {

    ACTIVE("활성"),

    INACTIVE("비활성");

    private String message;

    MemberStatus(String message) {
        this.message = message;
    }
}
