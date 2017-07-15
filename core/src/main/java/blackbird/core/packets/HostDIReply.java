package blackbird.core.packets;

import java.util.UUID;

import blackbird.core.connection.Packet;

public class HostDIReply extends Packet {

    private int implID;
    private Exception exception;

    public HostDIReply(UUID answerTo, Exception exception) {

        super(answerTo);
        this.exception = exception;
    }

    public HostDIReply(UUID answerTo, int implID) {

        super(answerTo);
        this.implID = implID;
    }

    public Exception getException() {
        return exception;
    }

    public int getImplID() {
        return implID;
    }

}
