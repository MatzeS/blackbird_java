package blackbird.core;

import java.util.Objects;

/**
 * A device running a blackbird instance like this.
 * <p>
 * Host devices are uniquely identified by their IDs.
 */
public class HostDevice extends Device {

    private static final long serialVersionUID = 2321322602384965052L;

    /**
     * The host device interface offers basic blackbird functions to other devices.
     * <p>
     * This is necessary for a consistent device implementation creation and look up.
     */
    public interface Interface extends DInterface {

        void destroyDeviceImplementation(Device device);

        boolean hasDeviceImplementation(Device device);

        <T> T interfaceDevice(Device device, Class<T> interfaceType);

        boolean isDeviceImplemented(Device device);

        <T> T implement(Device device, Class<T> implementationType);

        int getImplementationDistanceTo(Device device);
    }

    public static abstract class Implementation
            extends ComponentImplementation<HostDevice, DInterface> implements Interface {

        public Implementation(DInterface component) {
            super(component);
        }

    }

}
