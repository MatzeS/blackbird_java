package blackbird.core;

import blackbird.core.exception.ImplementationFailedException;
import blackbird.core.ports.LocalHostDevicePort;
import blackbird.core.ports.ParentDevicePort;

/**
 * The basic implementation for a {@link Device}.
 * <p>
 * Primarily created by its {@link Builder} and from there commonly inserted into a {@link ComponentImplementation}.
 * <p>
 * Device implementations are encouraged to extend the {@link ComponentImplementation} class and use the
 * {@link ComponentDIBuilder} to insert the component implementation. The composite pattern reduces a bit of boiler plate
 * and allows a more flexible device implementation construction.
 *
 * @see DInterface
 */
public class DImplementation implements DInterface {

    private Device device;
    private HostDevice host;

    public DImplementation(Device device, HostDevice host) {
        super();
        this.device = device;
        this.host = host;
    }

    @Override
    public DIState destroy() {
        return null;
    }

    public Device getDevice() {
        return device;
    }

    @Override
    public HostDevice getHost() {
        return host;
    }

    @Override
    public void loadState(DIState state) {
    }

    @Override
    public String toString() {
        return getClass().getName() + "[" + getDevice() + "]";
    }

    /**
     * Local DIBuilder, can create a {@link DImplementation} for any device without dependencies.
     */
    public static class Builder extends GenericDIBuilder<Device, DInterface, DPort> {

        public Builder() {
            setPortType(null);
        }

        @Override
        public DInterface build(Device device, Class<DInterface> interfaceType, DPort port) {
            return new DImplementation(device, blackbird.getLocalDevice());
        }

        @Override
        public void checkPort(DPort port) throws ImplementationFailedException {
            if (port == null)
                return;

            // this allows ParentDevice- and LocalHostDevicePorts to be first resolved by their own builders
            // TODO might refactor this into an interface or flag, every build should be able to prevent this build
            if (port.getClass().equals(ParentDevicePort.class) || port.getClass().equals(LocalHostDevicePort.class))
                throw new ImplementationFailedException("invalid port");

        }

    }

}
