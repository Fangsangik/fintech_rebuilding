package miniproject.fintech.error;

import lombok.extern.slf4j.Slf4j;
import miniproject.fintech.dto.ErrorResponse;
import miniproject.fintech.type.ErrorType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice //모든 컨트롤러에서 발생하는 예외를 처리 가능
public class GlobalHandlerException {

    @ExceptionHandler(CustomError.class)
    public ResponseEntity<ErrorResponse> handleCustomError(CustomError ex, HttpServletRequest request) {
        log.error("CustomError 발생: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                determineHttpStatus(ex.getErrorType()).value(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(determineHttpStatus(ex.getErrorType())).body(errorResponse);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> handleNullPointException(NullPointerException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("A null pointer exception occurred: " + exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex, HttpServletRequest request) {
        if (responseIsCommitted(request)) {
            log.error("응답이 이미 커밋되었습니다. 추가적인 오류 처리 불가.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        log.error("예외 발생: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred: " + ex.getMessage());
    }

    private boolean responseIsCommitted(HttpServletRequest request) {
        // 응답이 커밋되었는지 확인하는 로직
        return false;
    }


    private HttpStatus determineHttpStatus(ErrorType errorType) {
        return switch (errorType) {
            case ID_NULL -> HttpStatus.BAD_REQUEST;
            case EMAIL_DUPLICATE, ACCOUNT_NUMBER_DUPLICATE -> HttpStatus.CONFLICT;
            case PASSWORD_INCORRECT -> HttpStatus.UNAUTHORIZED;
            case MEMBER_NOT_FOUND, SOURCE_ID_NOT_FOUND, DESTINATION_ID_NOT_FOUND, ACCOUNT_ID_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case ACCOUNT_DELETE_FAILED, TRANSACTION_DELETE_FAILED -> HttpStatus.EXPECTATION_FAILED;
            case IN_CORRECT -> HttpStatus.BAD_REQUEST;
            case MUST_NOT_NULL -> HttpStatus.BAD_REQUEST;
            case NOT_ALLOWED_ACCESS -> HttpStatus.FORBIDDEN;
            case GRADE_NOT_VIP -> HttpStatus.BAD_GATEWAY;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
