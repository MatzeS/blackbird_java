package blackbird.core;

import org.apache.commons.lang3.NotImplementedException;

import blackbird.core.exception.ImplementationFailedException;

/**
 * The basic local host device implementation.
 * <p>
 * It is created only by its local {@link Builder} and exists in the blackbird instance of the host itself.
 */
public class LocalHostDeviceImplementation
        extends ComponentImplementation<HostDevice, DInterface> implements HostDevice.Interface {

    private Blackbird blackbird = Blackbird.getInstance();

    private LocalHostDeviceImplementation(DInterface component) {
        super(component);
    }

    @Override
    public void destroyDeviceImplementation(Device device) {
        throw new NotImplementedException("not implemented..."); //TODO
    }

    @Override
    public int getImplementationDistanceTo(Device device) {
        //TODO
        if (blackbird.getDeviceManager(device) != null)
            return 0;

        return 1;
    }

    @Override
    public <T> T interfaceDevice(Device device, Class<T> interfaceType) {
        return blackbird.interfaceDevice(device, interfaceType);
    }

    public static class Builder extends ComponentDIBuilder<HostDevice, LocalHostDeviceImplementation, DPort, DInterface> {

        @Override
        public LocalHostDeviceImplementation build(HostDevice device, DPort port, DInterface componentInterface) {
            if (!blackbird.getLocalDevice().equals(device))
                throw new ImplementationFailedException("only implementing the local host device");

            return new LocalHostDeviceImplementation(componentInterface);
        }

    }

}
