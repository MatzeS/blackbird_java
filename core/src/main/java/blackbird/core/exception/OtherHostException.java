package blackbird.core.exception;

public class OtherHostException extends HandlingException {

    public OtherHostException() {
    }

    public OtherHostException(String message) {
        super(message);
    }

    public OtherHostException(String message, Throwable cause) {
        super(message, cause);
    }

    public OtherHostException(Throwable cause) {
        super(cause);
    }

    public OtherHostException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
