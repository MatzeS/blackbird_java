package blackbird.core.packets;

import blackbird.core.HostDevice;
import blackbird.core.connection.Packet;

/**
 * The handshake packet used by the host connection to identify
 * the device on the remote side of the connection.
 */
public class HandshakePacket extends Packet {

    private static final long serialVersionUID = 5603371753276866796L;

    private HostDevice device;

    public HandshakePacket(HostDevice device) {
        super();
        this.device = device;
    }

    public HostDevice getDevice() {
        return device;
    }

}
