package miniproject.fintech.type;

public enum ErrorType {

    EMAIL_DUPLICATE("중복된 이메일입니다."),
    ACCOUNT_NUMBER_DUPLICATE("중복된 계좌번호입니다."),
    ACCOUNT_NOT_FOUND("존재하지 않는 계좌입니다."),
    ACCOUNT_DELETE_FAILED("계좌 삭제에 실패했습니다."),
    MEMBER_NOT_FOUND("존재하지 않는 회원입니다."),
    ID_NULL("Id 값이 Null이면 안됩니다."),
    MUST_NOT_NULL("빈 값이면 안됩니다."),
    SOURCE_ID_NOT_FOUND("보낼 아이디를 찾을 수 없습니다."),
    DESTINATION_ID_NOT_FOUND("받을 아이디를 찾을 수 없습니다."),
    TRANSACTION_DELETE_FAILED("거래 삭제에 실패했습니다."),
    TRANSACTION_NOT_FOUND("거래를 찾을 수 없습니다."),
    NOT_ALLOWED_ACCESS("잘못된 접근입니다."),
    PASSWORD_INCORRECT("비밀번호가 일치하지 않습니다."),
    MEMBER_EXIST("존재하는 회원입니다."),
    ACCOUNT_ID_NOT_FOUND("계좌 아이디가 존재하지 않습니다."),
    IN_CORRECT("잘못된 값입니다."), GRADE_NOT_VIP("VIP 등급이 아닙니다.");

    private final String message;
    ErrorType(String message){
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
