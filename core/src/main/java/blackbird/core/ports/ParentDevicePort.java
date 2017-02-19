package blackbird.core.ports;

import blackbird.core.DInterface;
import blackbird.core.DPort;
import blackbird.core.Device;
import blackbird.core.GenericDIBuilder;
import blackbird.core.HostDevice;
import blackbird.core.exception.ImplementationFailedException;
import blackbird.core.util.Generics;

/**
 * This port is used for implementations based on parent devices and their implementation.
 * <p>
 * This is typically used for a device attached to another and communicating through the parent.
 */
public class ParentDevicePort extends DPort {

    private static final long serialVersionUID = 5759808796154790653L;
    private Device parentDevice;

    public ParentDevicePort(Device parentDevice) {
        this.parentDevice = parentDevice;
    }

    public Device getParentDevice() {
        return parentDevice;
    }

    public abstract static class Builder<D, I extends DInterface, P, PI>
            extends GenericDIBuilder<D, I, ParentDevicePort> {

        private Class<P> parentDeviceType;
        private Class<PI> parentInterfaceType;

        public Builder() {
            parentDeviceType = (Class<P>) Generics.getGenericArgument(this, 2);
            parentInterfaceType = (Class<PI>) Generics.getGenericArgument(this, 3);

            setPortType(ParentDevicePort.class);

        }

        public abstract I assemble(DInterface component, PI parentInterface);

        @Override
        public I build(D device, Class<I> interfaceType, ParentDevicePort port) {
            Device parent = port.getParentDevice();

            DInterface parentDInterface = blackbird.interfaceDevice(parent, DInterface.class);

            HostDevice parentInterfaceHost = parentDInterface.getHost();

            if (parentInterfaceHost.equals(blackbird.getLocalDevice())) {

                // the null port here is crucial to the default DImplementationBuilder, which does not accept ParentDevicePorts
                DInterface component = blackbird.buildDeviceImplementation((Device) device, DInterface.class, null);

                PI parentInterface = (PI) blackbird.implementDevice(parent, (Class<DInterface>) parentInterfaceType);
                return assemble(component, parentInterface);

            } else {
                return blackbird
                        .interfaceDevice(parentInterfaceHost, HostDevice.Interface.class)
                        .interfaceDevice((Device) device, interfaceType);
            }
        }

        @Override
        public void checkPort(ParentDevicePort port) throws ImplementationFailedException {
            Class<?> nestedParentType = port.getParentDevice().getClass();
            if (!parentDeviceType.isAssignableFrom(nestedParentType))
                throw new ImplementationFailedException("The builder expects parent devices deriving from " + parentDeviceType);
        }

        public Class<PI> getParentInterfaceType() {
            return parentInterfaceType;
        }

    }

}
