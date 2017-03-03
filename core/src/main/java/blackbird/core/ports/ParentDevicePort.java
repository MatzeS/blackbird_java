package blackbird.core.ports;

import blackbird.core.DInterface;
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
public final class ParentDevicePort extends DPort {

    private static final long serialVersionUID = 5759808796154790653L;
    private Device parentDevice;

    public ParentDevicePort(Device parentDevice) {
        this.parentDevice = parentDevice;
    }

    public Device getParentDevice() {
        return parentDevice;
    }

    public HostDevice getHost() {
        Device parent = parentDevice;
        while (true)
            if (parent instanceof HostDevice)
                return (HostDevice) parent;
            else if (parent.getPort() instanceof ParentDevicePort)
                parent = ((ParentDevicePort) parent.getPort()).getParentDevice();
            else
                throw new RuntimeException("parent device chain does not end on host device");
    }

    public abstract static class Builder<D extends Device, I extends DInterface, P, PI>
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
            HostDevice host = port.getHost();

            if (!host.isHere())
                return host.getInterface(HostDevice.Interface.class).interfaceDevice(device, interfaceType);

            Device parent = port.getParentDevice();
            PI parentInterface = (PI) parent.getImplementation((Class<DInterface>) parentInterfaceType);

            // the null port here is crucial to the default DImplementationBuilder, which does not accept ParentDevicePorts
            DInterface component = device.buildImplementation(DInterface.class, null);

            return assemble(component, parentInterface);
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
