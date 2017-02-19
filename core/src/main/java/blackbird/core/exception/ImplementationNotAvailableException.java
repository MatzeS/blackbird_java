package blackbird.core.exception;

public class ImplementationNotAvailableException extends RuntimeException {

    public ImplementationNotAvailableException() {
        this("device manager is not on this device");
    }

    public ImplementationNotAvailableException(String message) {
        super(message);
    }

    public ImplementationNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImplementationNotAvailableException(Throwable cause) {
        super(cause);
    }

    public ImplementationNotAvailableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
