package blackbird.core.packets;

import blackbird.core.HostDevice;
import blackbird.core.connection.Packet;

/**
 * The handshake packet is used to identify
 * the device on the remote side of the connection.
 * <p>
 * In general a handshake should be answered with the own
 * handshake to identify each other (this packet is used as request/query and response).
 * If the {@code doNotAnswer} flag is set, the receiver might not answer.
 * Use this flag to avoid endless ping pong handshakes.
 */
public class HandshakePacket extends Packet {

    private static final long serialVersionUID = 5603371753276866796L;

    private boolean doNotAnswer;
    private HostDevice device;

    public HandshakePacket(HostDevice device) {
        super();
        this.device = device;
    }

    public HostDevice getDevice() {
        return device;
    }

    public boolean isDoNotAnswer() {
        return doNotAnswer;
    }

}
