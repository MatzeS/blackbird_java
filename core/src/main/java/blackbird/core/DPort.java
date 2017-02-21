package blackbird.core;

import java.io.Serializable;

/**
 * Stores information how to connect/implement a device.
 * <p>
 * Typically connection parameters, hardware configuration or the device address.
 *
 * It is consumed by the DIBuilder to produce a DeviceImplementation
 *
 * @see DIBuilder
 */
public class DPort implements Serializable {

    private static final long serialVersionUID = -3882766448598589630L;

}
