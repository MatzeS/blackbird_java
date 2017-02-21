package blackbird.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

import blackbird.core.ImplementationGraph.Node;
import blackbird.core.exception.ImplementationFailedException;

/**
 * A device can be any type of hardware represented in the blackbird system.
 * It does not necessarily has to be a single piece of hardware, <br>
 * but can also be a group, part, subsystem of existing device(s).
 * <p>
 * <p>The device object defines values directly bound to the device.<br>
 * Mainly name, perhaps token and UI data (mask values), <br>
 * but also hardware configuration or preset values.
 * Note: Make sure these values do not collide with the {@see DPort} object.</p>
 * <p>
 * Note: Make sure to override the {@see Device#equals} and {@see Device#hashCode} methods.
 */
public abstract class Device implements Serializable {

    private static final long serialVersionUID = 7683600417241772350L;

    private Logger logger = LogManager.getLogger(Device.class);

    /**
     * The name is used as user reference for a device, it should, but not necessarily has to be unique.
     */
    private String name;
    /**
     * The token is a short user reference for a device, it should be 3-6 characters long (internal up to 10).
     * Generally an abreviation (abkürzung) of the name and in the same way unique.
     */
    private String token;

    /**
     * The UI data map is used by the user interface implementation to store any soft values according to a device.<br>
     * The key convention should be ... TODO full canonical class name . key name.
     */
    private Map<String, Serializable> uiData;

    private List<DPort> ports;

    private DIState state;

    private Lock remoteImplementationLock;
    private ImplementationGraph implementationGraph;

    public Device() {
        uiData = new HashMap<>();
    }

    /**
     * Although the name is not used for internal identification there are not ghost devices allowed.
     *
     * @param name the name
     */
    public Device(String name) {
        this();
        this.name = name;
        uiData.put("iconName", "ic_integrated_circuit");
    }

    /**
     * The equals property is internally used for device comparison and identification.
     * Equal devices are might be merged or overwritten.<br>
     * <p>
     * Note: At this point the equals method still (only) checks the name field, but this might be removed in the future.
     *
     * @param o the other device
     * @return true, if they are effectively the same
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        //if (o == null || getClass() != o.getClass()) return false;
        if (!(o instanceof Device))
            return false;
        Device device = (Device) o;
        return Objects.equals(name, device.name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DPort> getPorts() {
        return ports;
    }

    /**
     * Gets the token. If the token is null, the name is returned.
     *
     * @return a short identifier
     */
    public String getToken() {
        return token != null ? token : getName();
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Map<String, Serializable> getUIData() {
        return uiData;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return getToken();
    }


    public synchronized <T> T getInterface(Class<T> interfaceType) {
        return getImplementation(interfaceType);
    }

    public synchronized <T> T getImplementation(Class<T> implementationType) {

        // if locally present take it or build on top
        if (isImplemented()) {
            Node implNode = implementationGraph.find(implementationType);
            if (implNode != null)
                return (T) implNode.getImplementation();
            else
                return buildImplementation(implementationType); // is this safe?
        }

        T remoteImpl = getRemoteImplementation(implementationType);
        if (remoteImpl != null)
            return remoteImpl;


        // if nowhere present build it, at the best location

        if(!implementationType.isInterface() && isImplementable())
            return buildImplementation(implementationType);

        HostDevice idealHost = getIdealHost();
        if(idealHost.isHere())
            return buildImplementation(implementationType);
        else
            return idealHost.getInterface(HostDevice.Interface.class).interfaceDevice(this, implementationType);

    }

    private HostDevice getIdealHost(){


    }

    private boolean isImplementable(){
        return getPossibleHosts().stream().filter(HostDevice::isHere).count() > 0;
    }

    private boolean isImplemented() {
        return implementationGraph != null;
    }

    private synchronized <T> T buildImplementation(Class<T> implementationType) {
        if(!isImplementable())
            throw new ImplementationFailedException("device can not be build here");

        


    }

    private List<HostDevice> getPossibleHosts(){
        return getPorts().stream()
                .map(DPort::getPossibleHosts)
                .reduce(new ArrayList<>(), (a, b) -> {
                    a.addAll(b);
                    return a;
                });
    }

    public synchronized <T> T getRemoteImplementation(Class<T> interfaceType) {

        List<HostDevice> hosts = getPossibleHosts().stream()
                .filter(host -> !host.isHere()) //TODO remove
                .filter(host -> host.getInterface(HostDevice.Interface.class).hasDeviceImplementation())
                .collect(Collectors.toList());
        HostDevice host = hosts.get(0);

        if (hosts.size() > 1)
            throw new RuntimeException("more than one host implements a device, inconsistent system state");

        if (hosts.isEmpty())
            return null;

        return host.getInterface(HostDevice.Interface.class).interfaceDevice(this, interfaceType);

    }


}