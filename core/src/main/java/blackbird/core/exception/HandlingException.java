package blackbird.core.exception;

public class HandlingException extends RuntimeException {

    public HandlingException() {
    }

    public HandlingException(String message) {
        super(message);
    }

    public HandlingException(String message, Throwable cause) {
        super(message, cause);
    }

    public HandlingException(Throwable cause) {
        super(cause);
    }

    public HandlingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
