package blackbird.core.ports;

import blackbird.core.DInterface;
import blackbird.core.DPort;
import blackbird.core.Device;
import blackbird.core.GenericDIBuilder;
import blackbird.core.HostDevice;

/**
 * This ports forces the implementation to be on the defined host.
 * <p>
 * It can be used to access Implementation objects from the host.
 * More likely this is used to hold implementations at a useful location, reducing traffic and stabilizing the network.
 * <p>
 * Its builder disassembles the port and implements the requested interface
 * on the required host device.
 */
public class LocalHostDevicePort extends DPort {

    private static final long serialVersionUID = -5301514267372432380L;

    private HostDevice host;
    private DPort child;

    public LocalHostDevicePort(HostDevice host, DPort child) {
        this.host = host;
        this.child = child;
    }

    public DPort getChild() {
        return child;
    }

    public HostDevice getHost() {
        return host;
    }

    public static class Builder extends GenericDIBuilder<Device, DInterface, LocalHostDevicePort> {

        public Builder() {
            setInterfaceType(null);
        }

        @Override
        public DInterface build(Device device, Class<DInterface> interfaceType, LocalHostDevicePort port) {
            HostDevice host = port.getHost();
            if (host.equals(blackbird.getLocalDevice()))
                return blackbird.buildDeviceImplementation(device, interfaceType, port.getChild());
            else
                return blackbird
                        .interfaceDevice(host, HostDevice.Interface.class)
                        .interfaceDevice(device, interfaceType);
        }

    }

}
