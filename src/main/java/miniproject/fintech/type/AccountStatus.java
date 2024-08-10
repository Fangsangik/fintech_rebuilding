package miniproject.fintech.type;

public enum AccountStatus {
    REGISTER("회원"),
    UNREGISTER("미회원가입"),

    ACTIVE ("활성"),
    UN_ACTIVE("비활성");

    private final String accountStatus;

    AccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }
}
