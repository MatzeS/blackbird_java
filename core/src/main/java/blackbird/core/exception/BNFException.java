package blackbird.core.exception;

public class BNFException extends BFException {

    public BNFException() {
    }

    public BNFException(String message) {
        super(message);
    }

    public BNFException(String message, Throwable cause) {
        super(message, cause);
    }

    public BNFException(Throwable cause) {
        super(cause);
    }

}
