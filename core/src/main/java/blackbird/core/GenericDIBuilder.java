package blackbird.core;

import blackbird.core.exception.ImplementationFailedException;
import blackbird.core.util.Generics;

/**
 * The builder pre casts valid build parameters to generic types.
 * <p>
 * The <code>check...</code> methods can be overwritten for more complex argument analysis and exclusion.
 * Make no active IO operation in the <code>check...</code> methods.
 * <p>
 * Notice: Accept and check device and port as wide as possible and define interface type as narrow as possible.
 *
 * @param <D> the accepted device
 * @param <I> the produced interface
 * @param <P> the accepted port
 */
public abstract class GenericDIBuilder<D, I, P> extends DIBuilder {

    private Class<D> deviceType;

    /**
     * The interface type produced by the builder.
     * If null the builder might produce every type of DInterface.
     */
    private Class<I> interfaceType;

    private Class<P> portType;

    public GenericDIBuilder() {
        deviceType = (Class<D>) Generics.getGenericArgument(this, 0);
        interfaceType = (Class<I>) Generics.getGenericArgument(this, 1);
        portType = (Class<P>) Generics.getGenericArgument(this, 2);
    }

    public abstract I build(D device, Class<I> interfaceType, P port);

    @Override
    public DInterface buildImplementation(Device device, Class<?> interfaceType, DPort port) {

        if (this.interfaceType != null && !interfaceType.isAssignableFrom(this.interfaceType))
            throw new ImplementationFailedException(
                    new IllegalArgumentException(this.getClass() + " produces a " + this.interfaceType));

        if (!deviceType.isAssignableFrom(device.getClass()))
            throw new ImplementationFailedException(
                    new IllegalArgumentException(this.getClass() + " expects a " + deviceType));

        if (this.portType != null)
            if (port == null || !portType.isAssignableFrom(port.getClass()))
                throw new ImplementationFailedException(
                        new IllegalArgumentException(this.getClass() + " builder expects a " + portType));

        checkDevice((D) device);
        checkPort((P) port);

        return (DInterface) build((D) device, (Class<I>) interfaceType, (P) port);
    }

    /**
     * Used to filter the device passed to the <code>buildImplementation</code> method.
     * If the device is not suitable for the builder
     * throw an {@link ImplementationFailedException} and indicate the reason.
     *
     * @param device the device to check
     */
    public void checkDevice(D device) {
    }

    /**
     * Used to filter the port passed to the <code>buildImplementation</code> method.
     * If the port is not suitable for the builder
     * throw an {@link ImplementationFailedException} and indicate the reason.
     *
     * @param port the port to check
     */
    public void checkPort(P port) {
    }

    public Class<D> getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(Class<D> deviceType) {
        this.deviceType = deviceType;
    }

    public Class<I> getInterfaceType() {
        return interfaceType;
    }

    public void setInterfaceType(Class<I> interfaceType) {
        this.interfaceType = interfaceType;
    }

    public Class<P> getPortType() {
        return portType;
    }

    public void setPortType(Class<P> portType) {
        this.portType = portType;
    }

}
