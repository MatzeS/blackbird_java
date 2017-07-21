package blackbird.core.packets;

import blackbird.core.connection.Packet;

/**
 * @see HostDIRequest
 */
public class HostDIReply extends Packet {

    private int implID;
    private Exception exception;


    public HostDIReply(Exception exception) {

        this.exception = exception;
    }


    public HostDIReply(int implID) {

        this.implID = implID;
    }


    public Exception getException() {

        return exception;
    }


    public int getImplID() {

        return implID;
    }

}
