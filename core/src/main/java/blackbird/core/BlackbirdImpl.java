package blackbird.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import blackbird.core.builders.DIBuilder;
import blackbird.core.util.ValueKeyedMap;

//TODO threadsafety
public class BlackbirdImpl implements Blackbird {


    private Logger logger = LogManager.getLogger(Blackbird.class);
    private Cluster cluster;
    private ValueKeyedMap<Device, DeviceManager> deviceManagers;
    private List<DIBuilder> builders;


    public BlackbirdImpl() {

        deviceManagers = new ValueKeyedMap<>(DeviceManager::getDevice);

    }

    public Map<HostDevice, HostDevice.Interface> getAvailableHostDeviceInterfaces() {
        return cluster.getHostDevices().stream()
                .filter(d -> {
                    try {
                        interfaceHost(d);
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                })
                .collect(Collectors.toMap(Function.identity(),
                        this::interfaceHost));
    }

    public List<DIBuilder> getBuilders() {
        return builders;
    }

    public List<Connector> getConnectors() {
        //TODO
    }

    protected DeviceManager getDeviceManager(Device device) {
        DeviceManager manager = deviceManagers.get(device);

        if (manager == null) {
            if (device instanceof HostDevice)
                manager = new HostDeviceManager(this, device);
            else
                manager = new AgentDeviceManager(this, device); //TODO use some builder here, also for thelocal device
            deviceManagers.put(manager);
        }

        return manager;
    }

    public Collection<Device> getDevices() {
        return cluster.getDevices();
    }

    public HostDevice getLocalDevice() {
        //TODO
    }

    @Override
    public <T> T interfaceDevice(Device device, Class<T> type) {
        return (T) getDeviceManager(device).getHandle(type);
    }

    public HostDevice.Interface interfaceHost(HostDevice host) {
        return interfaceDevice(host, HostDevice.Interface.class);
    }

    public boolean isLocalDevice(Device device) {
    }

    @Override
    public boolean isLocallyImplemented(Device device) {
        return getDeviceManager(device)
                .isLocallyImplemented();
    }


}
