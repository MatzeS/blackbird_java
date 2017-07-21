package blackbird.core.packets;

import blackbird.core.connection.Packet;

/**
 * The HostDI packet pair is used
 * to request the other hosts implementation.
 *
 * TODO more
 */
public class HostDIRequest extends Packet {

    private Class<?> type;

    public HostDIRequest(Class<?> type) {
        this.type = type;
    }

    public Class<?> getType() {
        return type;
    }

}
