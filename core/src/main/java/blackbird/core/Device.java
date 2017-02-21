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

import blackbird.core.exception.ImplementationFailedException;
import blackbird.core.util.MultiException;

import static com.google.common.base.Preconditions.checkArgument;

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

    public <T> T buildImplementation(Class<T> implementationType) {
        return buildImplementation(implementationType, getPort());
    }

    public synchronized <T> T buildImplementation(Class<T> implementationType, DPort port) {
        checkArgument(implementationType != null, "implementationType must not be null");

        logger.trace("building {} / {} / {}", this, implementationType, port);

        List<Exception> exceptionList = new ArrayList<>();
        for (DIBuilder builder : DIBuilderRegistry.getBuilders())
            try {
                logger.trace("attempting {} on {} / {} / {}", builder.getClass().getName(), this, implementationType, port);

                DInterface implementation = builder.buildImplementationRecursionSave(this, implementationType, port);
                logger.info("build with {} on {} / {} / {}", builder.getClass().getName(), this, implementationType, port);

                if (implementation.getHost().isHere())
                    registerImplementation(implementation);

                return (T) implementation;

            } catch (ImplementationFailedException e) { //TODO failures
                logger.trace("build failed," + builder + "/ " + this + "/" + port);
                if (!(e.getCause() instanceof IllegalArgumentException || e.getMessage().contains("recursive")))
                    exceptionList.add(e);
            }

        throw new ImplementationFailedException("no builder succeeded for " + this + "/"
                + implementationType.getName() + "/" + (port != null ? port.getClass().getName() : "no port")
                + MultiException.generateMultipleExceptionText(exceptionList));
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

    private synchronized <T> T getImplementationObject(Class<T> implementationType) {
        ImplementationGraph.Node node = null;

        if (implementationGraph != null)
            node = implementationGraph.find(implementationType);

        if (node != null)
            return (T) node.getImplementation();

        return buildImplementation(implementationType);
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

    public synchronized <T> T getInterface(Class<T> interfaceType) {
        return getImplementationObject(interfaceType);
    }

    public synchronized <T> T getImplementation(Class<T> interfaceType) {
        getInterface(DInterface.class);
        if (implementationGraph == null)
            throw new ImplementationFailedException("implementation is not on this device"); // TODO check

        return getImplementationObject(interfaceType);
    }

    private void registerImplementation(DInterface implementation) {
        if (!implementation.getHost().isHere()) {
            logger.warn("trying to register implementation not for this blackbird instance, rejected"); //TODO remove?
            return;
        }

        if (implementationGraph == null)
            implementationGraph = new ImplementationGraph();

        implementationGraph.add(implementation);
    }

    @Override
    public String toString() {
        return getToken();
    }

}