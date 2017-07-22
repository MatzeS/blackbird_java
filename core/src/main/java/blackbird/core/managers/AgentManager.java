package blackbird.core.managers;

import blackbird.core.Blackbird;
import blackbird.core.DInterface;
import blackbird.core.Device;
import blackbird.core.HostDevice;
import blackbird.core.exception.BFException;
import blackbird.core.exception.OtherHostException;
import blackbird.core.util.ConstructionPlan;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map.Entry;

/**
 * TODO lock builder
 */
public class AgentManager extends ConstructingManager {

    private Logger logger = LogManager.getLogger(AgentManager.class);


    public AgentManager(Blackbird blackbird, Device device) {

        super(blackbird, device);
    }


    protected Object constructHandle(Class<?> type) {

        logger.info("{} constructing handle",
                device);

        ConstructionPlan plan = new ConstructionPlan(device, type);
        plan = blackbird.constructHandle(plan); //TODO  super.constructHandle(plan);

        if (!plan.succeeded())
            throw new BFException("could not implement");

        return blackbird.interfaceHost(plan.getSucceeded())
                .interfaceDevice(device, type);
    }


    @Override
    protected Object extendHandle(Class<?> type) {

        logger.info("{} extending handle, local{}, remote {}",
                device, isLocallyImplemented(), isRemoteImplemented());

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


    private Entry<HostDevice, HostDevice.Interface> getRemoteHost() {

        return blackbird.getAvailableHostDeviceInterfaces().entrySet().stream()
                .filter(h -> h.getValue().isDeviceLocallyImplemented(device))
                .findAny().get();
    }


    private Object remoteImplement(Class<?> type) {

        if (!type.isInterface())
            throw new OtherHostException(
                    getRemoteHost().getKey()
            );

        Object impl = getRemoteHost().getValue().interfaceDevice(device, type);
        remoteHandle = (DInterface) impl;
        return impl;
    }

}
