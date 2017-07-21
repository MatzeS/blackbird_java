package blackbird.core;

/**
 * The basic implementation for any device.
 * <p>
 * Device implementations are encouraged to extend the
 *
 * TODO builder and setter methods, no builder needs to prudce a dimplementation
 *
 * The composite pattern reduces a bit of boiler plate
 * and allows a more flexible device implementation construction.
 *
 * @see DInterface
 */
public class DImplementation implements DInterface {

    private Device device;
    private HostDevice host;

    public DImplementation() {
        super();
    }

    @Override
    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    @Override
    public HostDevice getHost() {
        return host;
    }

    public void setHost(HostDevice host) {
        this.host = host;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[" + getDevice() + "]";
    }

}
