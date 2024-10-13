package uniauth.exception;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Setter
@Getter
@Slf4j
public class ErrorMessageException extends RuntimeException {

    private int errorCode = 406;

    public ErrorMessageException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        log.error(errorMessage);
        if (cause != null) {
            log.error(cause.getMessage());
        }
    }

    public ErrorMessageException(String errorMessage) {
        super(errorMessage);
        log.error(errorMessage);
    }

    public ErrorMessageException(String errorMessage, int errorCode) {
        this(errorMessage);
        this.errorCode = errorCode;
    }

    public ErrorMessageException(String errorMessage, int errorCode, Throwable cause) {
        this(errorMessage, cause);
        this.errorCode = errorCode;
    }

}
