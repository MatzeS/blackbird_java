package blackbird.core;


import blackbird.core.managers.LocalHostDeviceManager;
import blackbird.core.util.ConstructionPlan;

/**
 * The most basic host device implementation.
 * <p>
 * The implementation of a host remains on the host itself and
 * is only build by him. It is only remotely accessed through rmi connections.
 * <p>
 * It is created only by the {@link LocalHostDeviceManager} which injects the
 * blackbird instance.
 */
public class HostDeviceImplementation
        extends DImplementation implements HostDevice.Interface {

    private Blackbird blackbird;

    public HostDeviceImplementation(Blackbird blackbird) {
        this.blackbird = blackbird;
    }

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
