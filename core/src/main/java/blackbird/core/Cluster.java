package blackbird.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Cluster {

    private Map<String, Domain> domains;
    private Map<String, Device> devices;

    /**
     * The device this blackbird instances runs on.
     */
    private HostDevice localDevice;
    /**
     * Stored DIStates from destroyed implementations.
     */
    private Map<Device, DIState> deviceImplementationStates;

    public Cluster() {
        domains = new HashMap<>();
        devices = new HashMap<>();
    }

    public Collection<Device> getDevices() {
        return devices.values();
    }

    public List<HostDevice> getHostDevices() {
        return getDevices().stream()
                .filter(d -> d instanceof HostDevice)
                .map(HostDevice.class::cast)
                .collect(Collectors.toList());
    }

}
