package blackbird.core;

import org.apache.commons.lang3.NotImplementedException;

import blackbird.core.builders.GenericBuilder;
import blackbird.core.exception.ImplementationFailedException;

/**
 * The basic local host device implementation.
 * <p>
 * It is created only by its local {@link Builder} and exists in the blackbird instance of the host itself.
 */
public class LocalHostDeviceImplementation
        extends DImplementation implements HostDevice.Interface {


    @Override
    public <T> T interfaceDevice(Device device, Class<T> interfaceType) {
        return device.getInterface(interfaceType);
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
