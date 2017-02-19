package blackbird.core;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import blackbird.core.exception.ImplementationNotAvailableException;

/**
 * This singleton is the main handle to work with blackbird.
 * It creates, stores and manages device interfaces.
 */
public class Blackbird {

    private static Blackbird ourInstance = new Blackbird();

    private Logger logger = LogManager.getLogger(Blackbird.class);

    /**
     * The device this blackbird instances runs on.
     */
    private HostDevice localDevice;

    /**
     * The implementation for the local device.
     */
    private HostDevice.Interface localDeviceImplementation;

    /**
     * Set of all known devices.
     */
    private Set<Device> devices;

    /**
     * Stored DIStates from destroyed implementations.
     */
    private Map<Device, DIState> deviceImplementationStates;

    /**
     * Implementation builder for other blackbird hosts.
     */
    private HostDeviceImplementationBuilder hostDeviceImplementationBuilder;

    /**
     * Device Managers, each handles one device and its implementation.
     */
    private List<DeviceManager> deviceManagers;

    /**
     * Locks blocking remote implementation attempts.
     */
    private List<Device> remoteImplementLocks;

    private Blackbird() {
    }

    public void addDevice(Device device) {
        if (devices.add(device))
            logger.trace("device added: " + device);
    }

    public void addDevices(List<Device> devices) {
        devices.forEach(this::addDevice);
    }

    public <T> T buildDeviceImplementation(Device device, Class<T> implementationType) {
        return getDeviceManager(device)
                .orElseThrow(ImplementationNotAvailableException::new)
                .buildDeviceImplementation(implementationType);

    }

    public <T> T buildDeviceImplementation(Device device, Class<T> implementationType, DPort port) {
        return getDeviceManager(device)
                .orElseThrow(ImplementationNotAvailableException::new)
                .buildDeviceImplementation(implementationType, port);
    }

    private void clearRemoteImplementLock(Device device) {
        remoteImplementLocks.remove(device);
    }

    private DeviceManager createDeviceManager(Device device) {
        DeviceManager deviceManager = new DeviceManager(device);
        deviceManagers.add(deviceManager);
        return deviceManager;
    }

    protected Optional<DeviceManager> getDeviceManager(Device device) {
        addDevice(device);
        return deviceManagers.stream()
                .filter(d -> d.getDevice().equals(device))
                .findAny();
    }

    public List<DeviceManager> getDeviceManagers() {
        return deviceManagers;
    }

    public Set<Device> getDevices() {
        return devices;
    }

    public HostDeviceImplementationBuilder getHostDeviceImplementationBuilder() {
        return hostDeviceImplementationBuilder;
    }

    public void setHostDeviceImplementationBuilder(HostDeviceImplementationBuilder hostDeviceImplementationBuilder) {
        if (hostDeviceImplementationBuilder != null)
            DIBuilderRegistry.removeBuilder(hostDeviceImplementationBuilder);

        this.hostDeviceImplementationBuilder = hostDeviceImplementationBuilder;

        DIBuilderRegistry.addBuilder(hostDeviceImplementationBuilder);
    }

    public HostDevice getLocalDevice() {
        return localDevice;
    }

    public void setLocalDevice(HostDevice localDevice) {
        this.localDevice = localDevice;
    }

    public HostDevice.Interface getLocalDeviceImplementation() {
        return localDeviceImplementation;
    }

    private DeviceManager getOrCreateDeviceManager(Device device) {
        return getDeviceManager(device).orElse(createDeviceManager(device));
    }

    private synchronized DInterface getRemoteDeviceImplementation(Device device, Class<?> implementationType) {

        if (isRemoteImplementLocked(device))
            return null;

        setRemoteImplementLock(device);

        // system wide check
        // gathering all possible bridges...
        ListMultimap<Integer, HostDevice> bridges = ArrayListMultimap.create();
        Map<HostDevice, HostDevice.Interface> bridgeInterfaces = new HashMap<>();
        for (HostDevice host : devices.stream().distinct()
                .filter(d -> d instanceof HostDevice)
                .filter(d -> !d.equals(getLocalDevice()))
                .filter(d -> !d.equals(device))
                .map(d -> (HostDevice) d)
                .collect(Collectors.toList())) {

            try {
                HostDevice.Interface hostInterface = Blackbird.this.interfaceDevice(host, HostDevice.Interface.class);
                int distance = hostInterface.getImplementationDistanceTo(device);

                if (distance != -1) {
                    bridges.put(distance, host);
                    bridgeInterfaces.put(host, hostInterface);
                }
            } catch (Exception ignored) {
            }

        }

        ListMultimap<HostDevice, Integer> distanceMap = Multimaps.invertFrom(bridges, ArrayListMultimap.create());
        //List<Integer> distances = StreamSupport.stream(bridges.keySet()).collect(Collectors.toList());
        //distances = StreamSupport.stream(distances).sorted((a, b) -> a - b).collect(Collectors.toList());

        //...and try to use them
        DInterface implementation = null;
        if (!bridges.isEmpty()) {

            @SuppressWarnings("OptionalGetWithoutIsPresent")
            int minDistance = bridges.keySet().stream().mapToInt(i -> i).min().getAsInt();
            HostDevice bridge = bridges.get(minDistance).get(0); // any bridge with min distance

            HostDevice.Interface bridgeInterface = bridgeInterfaces.get(bridge);

            implementation = bridgeInterface.interfaceDevice(device, (Class<DInterface>) implementationType);

            logger.info("{}, acquired remote implementation from {}", device, bridge);


        }

        clearRemoteImplementLock(device);

        return implementation;

    }

    public <T> T implementDevice(Device device, Class<T> implementationType) {
        interfaceDevice(device, DInterface.class);

        return getDeviceManager(device)
                .orElseThrow(ImplementationNotAvailableException::new)
                .getImplementationObjectFor(implementationType);

    }

    public void implementLocalDevice(Class<? extends HostDevice.Interface> implementationType) {
        localDeviceImplementation = implementDevice(localDevice, implementationType);
    }

    public void init() {
        devices = new HashSet<>();
        deviceImplementationStates = new HashMap<>();

        deviceManagers = new ArrayList<>();
        remoteImplementLocks = new ArrayList<>();

        setHostDeviceImplementationBuilder(new HostDeviceImplementationBuilder());
    }

    public <T> T interfaceDevice(Device device, Class<T> interfaceType) {

        DeviceManager deviceManager = getDeviceManager(device).orElse(null);

        if (deviceManager == null) {
            DInterface remoteImplementation = getRemoteDeviceImplementation(device, interfaceType);
            if (remoteImplementation != null)
                return (T) remoteImplementation;

            deviceManager = createDeviceManager(device);
        }

        return deviceManager.interfaceDevice(interfaceType);
    }

    public boolean isRemoteImplementLocked(Device device) {
        return remoteImplementLocks.contains(device);
    }

    public void registerImplementation(DInterface implementation) {
        getOrCreateDeviceManager(implementation.getDevice()).registerImplementation(implementation);
    }

    private void setRemoteImplementLock(Device device) {
        remoteImplementLocks.add(device);
    }

    public static Blackbird getInstance() {
        return ourInstance;
    }

}
