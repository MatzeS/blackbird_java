package blackbird.core.packets;

import blackbird.core.HostDevice;
import blackbird.core.Packet;

/**
 * The handshake packet used by the host connection to identify
 * the device on the remote side of the connection.
 */
public class HandshakePacket extends Packet {

    private static final long serialVersionUID = 5603371753276866796L;

    private HostDevice device;
    private int rmiImplementationID;

    public HandshakePacket(HostDevice device, int rmiImplementationID) {
        super();
        this.device = device;
        this.rmiImplementationID = rmiImplementationID;
    }

    public HostDevice getDevice() {
        return device;
    }

    public int getRmiImplementationID() {
        return rmiImplementationID;
    }

}
