package blackbird.core;

import blackbird.core.util.ConstructionPlan;

import java.util.ArrayList;
import java.util.List;

/**
 * A device running a blackbird instance like this.
 * <p>
 * Host devices are uniquely identified by their IDs.
 */
public class HostDevice extends Device {

    private static final long serialVersionUID = 2321322602384965052L;

    private List<Port> ports;


    public HostDevice(String id) {

        super(id);

        ports = new ArrayList<>();
    }


    public HostDevice(String id, String name) {

        this(id);
        getUIProperties().setProperty("name", name);
    }


    public List<Port> getPorts() {

        return ports;
    }

    /**
     * The host device interface offers basic blackbird functions to other devices.
     * <p>
     * This is necessary for a consistent device implementation creation and look up.
     */
    public interface Interface extends DInterface {

        /**
         * constructs the device in the cluster and tells the final host
         */
        ConstructionPlan constructHandle(ConstructionPlan plan);

        Object interfaceDevice(Device device, Class<?> type);

        boolean isDeviceLocallyImplemented(Device device);

    }

    public static class Port {
    }

}
