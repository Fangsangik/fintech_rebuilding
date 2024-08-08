package miniproject.fintech.type;

public enum AccountStatus {
    RESISTER("회원"),
    UNREGISTER("미회원가입"),

    ACTIVE ("활성"),
    UNACITVE("비활성");

    private final String accountStatus;

    AccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }
}
