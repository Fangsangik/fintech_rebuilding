package miniproject.fintech.error;

import miniproject.fintech.type.ErrorType;

public class CustomError extends RuntimeException{

    private final ErrorType errorType;

    public CustomError(ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }
}
