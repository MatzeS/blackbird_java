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

import javax.inject.Inject;

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

    @Inject
    Blackbird blackbird;

    /**
     * The name is used as user reference for a device, it should, but not necessarily has to be unique.
     */
    private String name;
    /**
     * The token is a short user reference for a device, it should be 3-6 characters long (internal up to 10).
     * Generally an abbreviation of the name and in the same way unique.
     */
    private String token;

    /**
     * The UI data map is used by the user interface implementation to store any soft values according to a device.<br>
     * The key convention should be ... TODO full canonical class name . key name.
     */
    private Map<String, Serializable> uiData;

    private DIState state;

    public Device() {
        blackbird.getDevices().add(this);

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

    private DIFrame getFrame() {
        return blackbird.getDIFrames().get(this);
    }


    public <T> T implement(Class<T> implementationType) {
        }


}