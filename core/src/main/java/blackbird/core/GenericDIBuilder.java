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
 */
public abstract class GenericDIBuilder<D extends Device, I> extends DIBuilder {

    private Class<D> deviceType;

    /**
     * The interface type produced by the builder.
     * If null the builder might produce every type of DInterface.
     */
    private Class<I> interfaceType;

    public GenericDIBuilder() {
        deviceType = (Class<D>) Generics.getGenericArgument(this, 0);
        interfaceType = (Class<I>) Generics.getGenericArgument(this, 1);
    }

    public abstract I build(D device, Class<I> interfaceType);

    @Override
    public DInterface buildImplementation(Device device, Class<?> interfaceType) {

        if (this.interfaceType != null && !interfaceType.isAssignableFrom(this.interfaceType))
            throw new ImplementationFailedException(
                    new IllegalArgumentException(this.getClass() + " produces a " + this.interfaceType));

        if (!deviceType.isAssignableFrom(device.getClass()))
            throw new ImplementationFailedException(
                    new IllegalArgumentException(this.getClass() + " expects a " + deviceType));

        checkDevice((D) device);

        return (DInterface) build((D) device, (Class<I>) interfaceType);
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

}
