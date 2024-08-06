package miniproject.fintech.type;

public enum AccountStatus {
    RESISTER("회원"),
    UNREGISTER("미회원가입");

    private String accountStatus;

    AccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }
}
