package blackbird.core.exception;

public class BFException extends RuntimeException {

    public BFException() {
    }

    public BFException(String message) {
        super(message);
    }

    public BFException(String message, Throwable cause) {
        super(message, cause);
    }

    public BFException(Throwable cause) {
        super(cause);
    }

    public BFException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
