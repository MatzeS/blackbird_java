package blackbird.core.builders;

import blackbird.core.DImplementation;
import blackbird.core.DInterface;
import blackbird.core.Device;
import blackbird.core.exception.BFException;
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
public abstract class GenericBuilder<D extends Device, I> extends DIBuilder {

    /**
     * The interface type produced by the builder.
     * If null the builder might produce every type of DInterface.
     */
    protected Class<I> interfaceType;

    protected Class<D> deviceType;

    public GenericBuilder() {
        deviceType = (Class<D>) Generics.getGenericArgument(this, 0);
        interfaceType = (Class<I>) Generics.getGenericArgument(this, 1);
    }

    @Override
    public DImplementation build(Device device) throws BFException {
        return (DImplementation) buildGeneric((D) device);
    }

    public abstract I buildGeneric(D device);

    @Override
    public boolean canBuild(Device device) {
        return deviceType.isAssignableFrom(device.getClass()) && filterDevice((D) device);
    }

    public boolean filterDevice(D device) {
        return true;
    }

    public Class<D> getDeviceType() {
        return deviceType;
    }

    public Class<I> getInterfaceType() {
        return interfaceType;
    }

    @Override
    public Class<DInterface> produces() {
        return (Class<DInterface>) interfaceType;
    }

}
