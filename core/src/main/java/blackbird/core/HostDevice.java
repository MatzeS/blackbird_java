package blackbird.core;

import java.util.Objects;

/**
 * A device running a blackbird instance like this.
 * <p>
 * Host devices are uniquely identified by their IDs.
 */
public class HostDevice extends Device {

    private static final long serialVersionUID = 2321322602384965052L;

    private String ID;

    public HostDevice(String ID, String name) {
        super(name);
        this.ID = ID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        //if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        HostDevice that = (HostDevice) o;
        return Objects.equals(ID, that.ID);
    }

    public String getID() {
        return ID;
    }

    public boolean isHere() {

    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), ID);
    }

    /**
     * The host device interface offers basic blackbird functions to other devices.
     * <p>
     * This is necessary for a consistent device implementation creation and look up.
     */
    public interface Interface extends DInterface {

        void destroyDeviceImplementation(Device device);

        @Override
        HostDevice getDevice();

        <T> T interfaceDevice(Device device, Class<T> interfaceType);

    }

    public static abstract class Implementation
            extends ComponentImplementation<HostDevice, DInterface> implements Interface {

        public Implementation(DInterface component) {
            super(component);
        }

    }

}
