package blackbird.core.exception;

import java.io.IOException;

import blackbird.core.Packet;
import blackbird.core.PacketConnection;

/**
 * Signals for a send request there was no answer received (due the given timeout).
 *
 * @see PacketConnection#sendAndReceive(Packet, Class)
 */
public class NoReplyException extends IOException {

    private static final long serialVersionUID = 1L;

    public NoReplyException() {
        super();
    }

    public NoReplyException(String message) {
        super(message);
    }

    public NoReplyException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoReplyException(Throwable cause) {
        super(cause);
    }

}
