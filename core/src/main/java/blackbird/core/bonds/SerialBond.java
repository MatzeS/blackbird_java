package blackbird.core.bonds;

import blackbird.core.Bond;
import blackbird.core.HostDevice;
import blackbird.core.impl.SerialDevice;

public class SerialBond extends Bond {

    private static final long serialVersionUID = -9135816665280984514L;

    private String port;
    private int baudRate;

    public SerialBond(HostDevice host, SerialDevice serialDevice, String port, int baudRate) {
        super(host, serialDevice);
        this.port = port;
        this.baudRate = baudRate;
    }

    public int getBaudRate() {
        return baudRate;
    }

    @Override
    public HostDevice getFrom() {
        return (HostDevice) super.getFrom();
    }

    @Override
    public SerialDevice getTo() {
        return (SerialDevice) super.getTo();
    }

    public HostDevice getHost() {
        return getFrom();
    }

    public SerialDevice getSerialDevice() {
        return getTo();
    }

    public String getPort() {
        return port;
    }

}
