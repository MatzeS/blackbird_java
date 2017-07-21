package blackbird.core.managers;

import java.util.Map.Entry;

import blackbird.core.Blackbird;
import blackbird.core.DInterface;
import blackbird.core.Device;
import blackbird.core.exception.BFException;
import blackbird.core.util.ConstructionPlan;

/**
 * TODO lock builder
 */
public class AgentManager extends ConstructingManager {


    public AgentManager(Blackbird blackbird, Device device) {
        super(blackbird, device);
    }

    protected Object constructHandle(Class<?> type) {
        ConstructionPlan plan = new ConstructionPlan(device, type);
        plan = blackbird.constructHandle(plan);

        if (!plan.succeeded())
            throw new BFException("could not implement");

        return blackbird.interfaceHost(plan.getSucceeded())
                .interfaceDevice(device, type);
    }

    @Override
    protected Object extendHandle(Class<?> type) {
        if (isLocallyImplemented())
            return constructHandle(type);// implement -> extend current impl
        else if (isRemoteImplemented())
            return remoteImplement(type);
        else // not implemented: construct on cluster
            return constructHandle(type);
    }

    private boolean isRemoteImplemented() {
        if (isLocallyImplemented())
            return false;

        if (getRemoteHandle().isPresent())
            return true;

        return blackbird.getAvailableHostDeviceInterfaces().entrySet().stream()
                .filter(e -> e.getValue().isDeviceLocallyImplemented(device))
                .count() > 0;
    }

    private Object remoteImplement(Class<?> type) {
        Object impl = blackbird.getAvailableHostDeviceInterfaces().entrySet().stream()
                .map(Entry::getValue)
                .filter(i -> i.isDeviceLocallyImplemented(device))
                .map(i -> i.interfaceDevice(device, type))
                .findAny().get();
        remoteHandle = (DInterface) impl;
        return impl;
    }

}
