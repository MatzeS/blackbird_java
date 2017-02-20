package blackbird.spring;

import java.lang.reflect.ParameterizedType;
import java.util.Map;

import blackbird.core.Device;

public abstract class DataProvider<D extends Device, DI> {

    private Class<D> deviceType;
    private Class<DI> implementationType;

    @SuppressWarnings("unchecked")
    public DataProvider() {
        deviceType = (Class<D>) ((ParameterizedType) getClass().getGenericSuperclass())
                .getActualTypeArguments()[0];
        implementationType = (Class<DI>) ((ParameterizedType) getClass().getGenericSuperclass())
                .getActualTypeArguments()[1];
    }

    public boolean applicable(Device device, Object implementation) {
        return deviceType.isAssignableFrom(device.getClass())
                && implementation == null
                || (implementation != null && implementationType.isAssignableFrom(implementation.getClass()));
    }

    public Class<D> getDeviceType() {
        return deviceType;
    }

    public Class<DI> getImplementationType() {
        return implementationType;
    }

    @SuppressWarnings("unchecked")
    public void populate(Map<String, Object> rootData, Map<String, Object> data, Device device, Object implementation, Exception exception) {
        if (applicable(device, implementation)) {
            populateWithDevice(rootData, data, (D) device);
            if (implementation != null)
                populateWithImplementation(rootData, data, (D) device, (DI) implementation);
            else
                populateWithImplementationException(rootData, data, (D) device, exception);
        }
    }

    public abstract void populateWithDevice(Map<String, Object> rootData, Map<String, Object> data, D device);

    public abstract void populateWithImplementation(Map<String, Object> rootData, Map<String, Object> data, D device, DI implementation);

    public abstract void populateWithImplementationException(Map<String, Object> rootData, Map<String, Object> data, D device, Exception exception);

}
