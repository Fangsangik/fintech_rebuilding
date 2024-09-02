package miniproject.fintech.type;

import org.springframework.http.HttpStatus;

public enum ErrorType {

    EMAIL_DUPLICATE("중복된 이메일입니다.", HttpStatus.CONFLICT),
    ACCOUNT_NUMBER_DUPLICATE("중복된 계좌번호입니다.", HttpStatus.CONFLICT),
    ACCOUNT_NOT_FOUND("존재하지 않는 계좌입니다.", HttpStatus.BAD_REQUEST),
    ACCOUNT_DELETE_FAILED("계좌 삭제에 실패했습니다.", HttpStatus.BAD_REQUEST),
    MEMBER_NOT_FOUND("존재하지 않는 회원입니다.", HttpStatus.NOT_FOUND),
    ID_NULL("Id 값이 Null이면 안됩니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    MUST_NOT_NULL("빈 값이면 안됩니다.", HttpStatus.BAD_REQUEST),
    SOURCE_ID_NOT_FOUND("보낼 아이디를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    DESTINATION_ID_NOT_FOUND("받을 아이디를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    TRANSACTION_DELETE_FAILED("거래 삭제에 실패했습니다.", HttpStatus.BAD_REQUEST), // 수정
    TRANSACTION_NOT_FOUND("거래를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    NOT_ALLOWED_ACCESS("잘못된 접근입니다.", HttpStatus.FORBIDDEN), // 수정
    PASSWORD_INCORRECT("비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    MEMBER_EXIST("존재하는 회원입니다.", HttpStatus.CONFLICT), // 수정
    ACCOUNT_ID_NOT_FOUND("계좌 아이디가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    IN_CORRECT("잘못된 값입니다.", HttpStatus.BAD_REQUEST),
    GRADE_NOT_VIP("VIP 등급이 아닙니다.", HttpStatus.BAD_REQUEST),
    ADMIN_NOT_FOUND("관리자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    TRANSFER_NOT_FOUND("해당 송금을 찾을 수 없습니다", HttpStatus.NOT_FOUND),;

    private final String message;
    private final HttpStatus status;
    ErrorType(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }
}
