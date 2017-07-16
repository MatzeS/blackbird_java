package blackbird.core;

import blackbird.core.util.ConstructionPlan;

/**
 * The basic local host device implementation.
 * <p>
 * It is created only by its local Builder and exists in the blackbird instance of the host itself.
 */
public class LocalHostDeviceImplementation
        extends DImplementation implements HostDevice.Interface {

    private BlackbirdImpl blackbird;

    @Override
    public ConstructionPlan constructHandle(ConstructionPlan plan) {
        return blackbird.constructHandle(plan);
    }

    @Override
    public Object interfaceDevice(Device device, Class<?> type) {
        return blackbird.interfaceDevice(device, type);
    }

    @Override
    public boolean isDeviceLocallyImplemented(Device device) {
        return blackbird.isLocallyImplemented(device);
    }

}
