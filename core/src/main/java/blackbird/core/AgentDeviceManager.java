package blackbird.core;

import java.util.List;
import java.util.Map.Entry;

import blackbird.core.builders.DIBuilder.ImplementationReference;
import blackbird.core.exception.BFException;
import blackbird.core.exception.OtherHostException;
import blackbird.core.util.BuildRequirement;
import blackbird.core.util.ConstructionPlan;

/**
 * TODO lock builder
 */
public class AgentDeviceManager extends DeviceManager {

    private LogImplRef implRef = new LogImplRef();

    public AgentDeviceManager(BlackbirdImpl blackbird, Device device) {
        super(blackbird, device);
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

    private class LogImplRef implements ImplementationReference {

        private List<BuildRequirement> list;

        public List<BuildRequirement> getList() {
            return list;
        }

        @Override
        public <T> T implement(Device device, Class<T> type) {

            // here we restrict builders to access agents from other hosts or even other hosts

            if (device.isHost())
                if (blackbird.isLocalDevice(device))
                    return blackbird.interfaceDevice(device, type); // own host
                else
                    throw new OtherHostException((HostDevice) device); // other host

            AgentDeviceManager manager = (AgentDeviceManager) blackbird.getDeviceManager(device);

            if (manager.isRemoteImplemented())
                throw new BFException("remote implemented, new target is the other host");

            return blackbird.interfaceDevice(device, type); // own implementation or not implemented
        }

    }

}
