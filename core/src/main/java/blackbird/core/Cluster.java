package blackbird.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Cluster {

    private List<Domain> domains;
    private List<Device> devices;

    /**
     * The device this blackbird instances runs on.
     */
    private HostDevice localDevice;
    /**
     * Stored DIStates from destroyed implementations.
     */
    private Map<Device, DIState> deviceImplementationStates;


    public Cluster() {

        domains = new ArrayList<>();
        devices = new ArrayList<>();
    }


    public List<Device> getDevices() {

        return devices;
    }


    public void addDevice(Device device) {

        devices.add(device);
    }


    public List<HostDevice> getHostDevices() {

        return getDevices().stream()
                .filter(d -> d instanceof HostDevice)
                .map(HostDevice.class::cast)
                .collect(Collectors.toList());
    }

}
