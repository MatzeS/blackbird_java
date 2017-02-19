package blackbird.core.exception;

/**
 * Signals that a connection could not be established.
 */
public class NoConnectionException extends Exception {

    private static final long serialVersionUID = 3471819030868263906L;

    public NoConnectionException() {
    }

    public NoConnectionException(String message) {
        super(message);
    }

    public NoConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

}
