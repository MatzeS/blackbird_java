package blackbird.core;

import blackbird.core.device.avr.AVRDevice;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.*;

/**
 * TODO
 * A device can be any type of hardware represented in the blackbird system.
 * It does not necessarily has to be a single piece of hardware, <br>
 * but can also be a group, part, subsystem of existing device(s).
 * <p>
 * <p>The device object defines values directly bound to the device.<br>
 * Mainly name, perhaps token and UI data (mask values), <br>
 * but also hardware configuration or preset values.
 * Note: Make sure these values do not collide with the {@see Port} object.</p>
 * <p>
 * Note: Make sure to override the {@see Device#equals} and {@see Device#hashCode} methods.
 */
public class Device implements Serializable {

    public static final String UI_NAME = "blackbird.core.Device.UI_NAME";

    private static final long serialVersionUID = 7683600417241772350L;

    private Logger logger = LogManager.getLogger(this.getClass());

    /**
     * The id is used as user reference for a device, it should, but not necessarily has to be unique.
     */
    private String id;

    private Device superModule;
    private Map<String, Device> modules;
    /**
     * The UI data map is used by the user interface implementation to store any soft values according to a device.<br>
     * The key convention should be ... TODO full canonical class name . key name.
     */
    private Properties uiProperties;
    private DIState state;


    @Deprecated
    public Device() {

        this(UUID.randomUUID().toString());
    }


    public Device(String id) {

        modules = new HashMap<>();
        uiProperties = new Properties();
        uiProperties.put("iconName", "ic_integrated_circuit");

        this.id = id;
    }


    /**
     * The equals property is internally used for device comparison and identification.
     * Equal devices are might be merged or overwritten.<br>
     * <p>
     * Note: At this point the equals method still (only) checks the name field, but this might be removed in the future.
     * TODO
     *
     * @param o the other device
     * @return true, if they are effectively the same
     */
    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Device device = (Device) o;
        return com.google.common.base.Objects.equal(id, device.id);
    }


    public String getID() {

        if (id == null) {
            id = UUID.randomUUID().toString();
            logger.warn("device has no ID, created UUID {}", id);
        }

        return getSuperModule()
                .map(Object::toString)
                .map(s -> s + ".")
                .orElse("")
                + id;
    }


    public void setID(String id) {

        this.id = id.trim();
    }


    public boolean isHost() {

        return this instanceof HostDevice;
    }


    public Device getModule(String identifier) {

        return modules.get(identifier);
    }


    public Map<String, Device> getModules() {

        return modules;
    }


    public Optional<Device> getSuperModule() {

        return Optional.ofNullable(superModule);
    }


    public void setSuperModule(AVRDevice superModule) {

        this.superModule = superModule;
    }


    public Properties getUIProperties() {

        return uiProperties;
    }


    public Optional<String> getUIProperty(String key) {

        return Optional.ofNullable(uiProperties.getProperty(key));
    }


    @Override
    public int hashCode() {

        return Objects.hash(id);
    }


    public String getName() {

        return getUIProperty(UI_NAME).orElse(getID());
    }


    @Override
    public String toString() {

        return getName() + "[" + this.getClass().getSimpleName() + "]";
    }

}