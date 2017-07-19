package blackbird.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import blackbird.core.HostDevice.Interface;
import blackbird.core.builders.DIBuilder;
import blackbird.core.exception.OtherHostException;
import blackbird.core.util.ConstructionPlan;
import blackbird.core.util.ValueKeyedMap;

//TODO threadsafety
//TODO hosthopping
public class BlackbirdImpl implements Blackbird {


    private Logger logger = LogManager.getLogger(Blackbird.class);
    private Cluster cluster;
    private ValueKeyedMap<Device, DeviceManager> deviceManagers;
    private List<DIBuilder> builders;
    private List<Connector> connectors;

    private HostDevice localDevice;

    public BlackbirdImpl() {

        deviceManagers = new ValueKeyedMap<>(DeviceManager::getDevice);

    }

    protected ConstructionPlan constructHandle(ConstructionPlan plan) {
        Device device = plan.getDevice();

        List<HostDevice> otherHosts = new ArrayList<>();

        for (DIBuilder builder : getBuilders())
            if (builder.canBuild(device) && builder.produces(plan.getType())) { // pre filter

                try {
                    DImplementation impl = builder.build(device);

                    impl.setDevice(device);
                    impl.setHost(getLocalDevice());

                    //TODO call after construction/load state
                    //TODO call devicemanager postconstruction

                    getDeviceManager(device).getImplementationStack().push(impl);

                    plan.setSucceeded(getLocalDevice());

                    return plan;

                } catch (OtherHostException e) {

                    otherHosts.add(e.getHost());

                } catch (Exception ignored) {
                }

            }

        plan.addFailed(getLocalDevice());
        plan.addPossibleHosts(otherHosts);

        if (plan.getPossible().isEmpty())
            return plan;

        ConstructionPlan finalPlan = plan;
        List<Entry<HostDevice, Interface>> possibleHosts =
                getAvailableHostDeviceInterfaces().entrySet().stream()
                        .filter(e -> finalPlan.getPossible().contains(e.getKey()))
                        .collect(Collectors.toList());
        for (Entry<HostDevice, HostDevice.Interface> possibleHost : possibleHosts)
            plan = possibleHost.getValue().constructHandle(plan);

        return plan;
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
        return connectors;
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
        return localDevice;
    }

    @Override
    public <T> T interfaceDevice(Device device, Class<T> type) {
        return (T) getDeviceManager(device).getHandle(type);
    }

    public HostDevice.Interface interfaceHost(HostDevice host) {
        return interfaceDevice(host, HostDevice.Interface.class);
    }

    public boolean isLocalDevice(Device device) {
        return getLocalDevice().equals(device);
    }

    @Override
    public boolean isLocallyImplemented(Device device) {
        return getDeviceManager(device)
                .isLocallyImplemented();
    }

}
