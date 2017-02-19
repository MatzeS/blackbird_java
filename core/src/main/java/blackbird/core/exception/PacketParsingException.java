package blackbird.core.exception;

public class PacketParsingException extends Exception {

    private static final long serialVersionUID = 2556293309604248174L;

    public PacketParsingException() {
    }

    public PacketParsingException(String message) {
        super(message);
    }

    public PacketParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    public PacketParsingException(Throwable cause) {
        super(cause);
    }

}
