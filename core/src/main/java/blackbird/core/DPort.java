package blackbird.core;

import java.io.Serializable;
import java.util.List;

/**
 * Stores information how to connect/implement a device.
 * <p>
 * Typically connection parameters, hardware configuration or the device address.
 *
 * It is consumed by the DIBuilder to produce a DeviceImplementation
 *
 * Describes how to connect to the device added to
 *
 * @see DIBuilder
 */
public abstract class DPort implements Serializable {

    private static final long serialVersionUID = -3882766448598589630L;

    // getBuilder // use factory for builders

    public abstract List<HostDevice> getPossibleHosts();

    // network port has all in netscope as host?

}
