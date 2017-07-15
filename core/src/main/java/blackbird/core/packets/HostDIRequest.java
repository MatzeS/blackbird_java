package blackbird.core.packets;

import blackbird.core.connection.Packet;

public class HostDIRequest extends Packet {

    private Class<?> type;

    public HostDIRequest(Class<?> type) {
        this.type = type;
    }

    public Class<?> getType() {
        return type;
    }

}
