package blackbird.core.ports;

import blackbird.core.DPort;
import blackbird.core.Device;

public class SingleDevicePort extends DPort {

    private Device from;

    public SingleDevicePort(Device from) {
        this.from = from;
    }

    public Device getFrom() {
        return from;
    }

}
