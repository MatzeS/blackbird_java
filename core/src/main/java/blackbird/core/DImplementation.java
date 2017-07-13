package blackbird.core;

/**
 * The basic implementation for a {@link Device}.
 * <p>
 * Primarily created by its {@link Builder} and from there commonly inserted into a {@link ComponentImplementation}.
 * <p>
 * Device implementations are encouraged to extend the {@link ComponentImplementation} class and use the
 * {@link ComponentDIBuilder} to insert the component implementation. The composite pattern reduces a bit of boiler plate
 * and allows a more flexible device implementation construction.
 *
 * @see DInterface
 */
public class DImplementation implements DInterface {

    protected Device device;
    protected HostDevice host;

    public DImplementation() {
        super();
    }

    @Override
    public Device getDevice() {
        return device;
    }

    protected void setDevice(Device device) {
        this.device = device;
    }

    @Override
    public HostDevice getHost() {
        return host;
    }

    protected void setHost(HostDevice host) {
        this.host = host;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[" + getDevice() + "]";
    }

}
