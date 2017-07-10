package blackbird.core;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;

import blackbird.core.HostDevice.Interface;
import blackbird.core.builders.DIBuilder;
import blackbird.core.exception.BFException;
import blackbird.core.exception.ImplementationNotAvailableException;
import blackbird.core.util.BuildRequirement;
import blackbird.core.util.ValueKeyedMap;

import static com.google.common.base.Preconditions.checkNotNull;

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

    public Optional<DeviceManager> getDeviceManager(Device device) {
        return Optional.of(deviceManagers.get(device));
    }

    public Collection<Device> getDevices() {
        return cluster.getDevices();
    }

    public HostDevice getLocalDevice() {
        //TODO
    }

    private DeviceManager getOrCreateDeviceManager(Device device) {
        DeviceManager manager = deviceManagers.get(device);

        if (manager == null) {
            manager = new DeviceManager(device);
            deviceManagers.put(manager);
        }

        return manager;
    }

    @Override
    public <T> T implementDevice(Device device, Class<T> type) {

    }

    @Override
    public <T> T interfaceDevice(Device device, Class<T> type) {
        DeviceManager manager = getOrCreateDeviceManager(device);
        return manager.interfaceDevice(type); //TODO ineffective
    }

    public HostDevice.Interface interfaceHost(HostDevice host) {
        return interfaceDevice(host, HostDevice.Interface.class);
    }

    private boolean isAvailable(Device device, Class<?> type) {
        return getDeviceManager(device)
                .filter(DeviceManager::isLocallyImplemented)
                .filter(dm -> dm.isCurrentImplSatisfying(type))
                .isPresent();
    }

    private boolean isAvailable(BuildRequirement requirement) {
        return isAvailable(requirement.getDevice(), requirement.getImplementationType());
    }

    private boolean isConstructable(BuildRequirement requirement) {
        return isConstructable(requirement.getDevice(), requirement.getImplementationType());
    }

    private boolean isConstructable(Device device, Class<?> type) {
        return builders.stream()
                .filter(builder -> builder.produces(type) && builder.canBuild(device))
                .anyMatch(builder -> builder.getBuildRequirements(device).stream()
                        .anyMatch(requirements -> requirements.stream().
                                allMatch(r -> isAvailable(r) || isConstructable(r))));
    }

    @Override
    public boolean isLocallyImplemented(Device device) {
        return getDeviceManager(device)
                .filter(DeviceManager::isLocallyImplemented)
                .isPresent();
    }

    public class DeviceManager {

        private Device device;

        private Stack<DInterface> implementationStack;

        private Lock remoteImplLock;

        public DeviceManager(Device device) {
            checkNotNull(device);

            this.device = device;
            implementationStack = new Stack<>();

            remoteImplLock = new ReentrantLock();
        }

        private <T> T buildImplementation(Class<T> type) {

            for (DIBuilder builder : builders)
                if (builder.canBuild(device) && builder.produces(type)) {

                    DImplementation impl = builder.build(device);

                    impl.setDevice(device);
                    impl.setHost(getLocalDevice());

                    //TODO call after construction/load state

                    implementationStack.push(impl);

                    return (T) impl;

                }

            throw new BFException("no builder succeeded");
        }

        private <T> void constructImplementation(Class<T> type) {

            if (isConstructable(device, type)) {
                buildImplementation(type);
                return;
            }

            //TODO if device implementation no rmi is possible


        }

        private DInterface getCurrentImplementation() {
            return implementationStack.peek();
        }

        public Device getDevice() {
            return device;
        }

        private synchronized DInterface getRemoteDeviceImplementation(Class<?> type) {

            if (remoteImplLock.tryLock()) //TODO
                return null;

            // system wide check
            // gathering all possible bridges...
            ListMultimap<Integer, HostDevice> bridges = ArrayListMultimap.create();
            Map<HostDevice, Interface> bridgeInterfaces = new HashMap<>();

            for (HostDevice host : BlackbirdImpl.this.getDevices().stream().distinct()
                    .filter(d -> d instanceof HostDevice)
                    .filter(d -> !d.equals(getLocalDevice()))
                    .filter(d -> !d.equals(device))
                    .map(d -> (HostDevice) d)
                    .collect(Collectors.toList())) {

                try {
                    HostDevice.Interface hostInterface =
                            BlackbirdImpl.this.interfaceDevice(host, HostDevice.Interface.class);
                    int distance = hostInterface.getImplementationDistanceTo(device);

                    if (distance != -1) {
                        bridges.put(distance, host);
                        bridgeInterfaces.put(host, hostInterface);
                    }
                } catch (Exception ignored) {
                }

            }

            if (bridges.isEmpty())
                return null;

            ListMultimap<HostDevice, Integer> distanceMap = Multimaps.invertFrom(bridges, ArrayListMultimap.create());
            //List<Integer> distances = StreamSupport.stream(bridges.keySet()).collect(Collectors.toList());
            //distances = StreamSupport.stream(distances).sorted((a, b) -> a - b).collect(Collectors.toList());

            //...and try to use them

            @SuppressWarnings("OptionalGetWithoutIsPresent")
            int minDistance = bridges.keySet().stream().mapToInt(i -> i).min().getAsInt();
            HostDevice bridge = bridges.get(minDistance).get(0); // any bridge with min distance

            HostDevice.Interface bridgeInterface = bridgeInterfaces.get(bridge);

            Object implementation = bridgeInterface.
                    interfaceDevice(device, type);

            logger.info("{}, acquired remote implementation from {}", device, bridge);

            remoteImplLock.unlock();

            return (DInterface) implementation;
        }

        public <T> T interfaceDevice(Class<T> type) {

            if (isLocallyImplemented()) {

                DInterface currentImpl = getCurrentImplementation();

                if (isCurrentImplSatisfying(type)) {
                    //the current implementation satisfies the requested interface type

                    return (T) currentImpl;

                } else if (currentImpl.getClass().isAssignableFrom(type)) {
                    // the requested impl is more specific than the current
                    // TODO hard and detached proxys

                    // TODO to stack?
                    return buildImplementation(type);

                } else
                    //TODO
                    throw new ImplementationNotAvailableException(
                            "the implementation can not be produced," +
                                    "since its not compatible with current instances");

            } else {

                DInterface impl = getRemoteDeviceImplementation(type);

                if (impl != null)
                    return (T) impl;
                else {
                    constructImplementation(type);
                    return interfaceDevice(type);
                }

            }

        }

        public boolean isCurrentImplSatisfying(Class<?> type) {
            return type.isAssignableFrom(getCurrentImplementation().getClass());
        }

        public boolean isLocallyImplemented() {
            return implementationStack.isEmpty();
        }
    }

}
