package blackbird.core.exception;

/**
 * Signals that an implementation could not be build
 * for any reason.
 */
public class ImplementationFailedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ImplementationFailedException() {
        super();
    }

    public ImplementationFailedException(String message) {
        super(message);
    }

    public ImplementationFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImplementationFailedException(Throwable cause) {
        super(cause);
    }

}
