package blackbird.core.builders;

import blackbird.core.DImplementation;
import blackbird.core.DInterface;
import blackbird.core.Device;

/**
 * DIBuilders are used by blackbird to create implementations for devices.
 * <p>
 * The primary <code>buildImplementation</code>Method consumes the device and the port.
 * Also the expected DInterface type is given. TODO
 * <p>
 */
public abstract class DIBuilder {

    private ImplementationReference implementationReference;

    public abstract DImplementation build(Device device);

    public boolean canBuild(Device device) {
        return true;
    }

    protected <T> T implement(Device device, Class<T> type) {
        return implementationReference.implement(device, type);
    }

    public Class<DInterface> produces() {
        return DInterface.class;
    }

    public boolean produces(Class<?> type) {
        return produces().isAssignableFrom(type);
    }

    public void setImplementationReference(ImplementationReference implementationReference) {
        this.implementationReference = implementationReference;
    }

    public interface ImplementationReference {
        <T> T implement(Device device, Class<T> type);
    }

}