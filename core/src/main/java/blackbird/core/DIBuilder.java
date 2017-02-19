package blackbird.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import blackbird.core.exception.ImplementationFailedException;

/**
 * DIBuilders are used by blackbird to create implementations for devices.
 * <p>
 * The primary <code>buildImplementationMethod</code> consumes the device and the port.
 * Also the expected DInterface type is given.
 * <p>
 */
public abstract class DIBuilder {

    protected Blackbird blackbird = Blackbird.getInstance();

    private boolean recursiveInterfaceChecking = true;
    private boolean recursivePortChecking = true;

    private List<ArgumentSet> excludedArgumentSets;

    public DIBuilder() {
        excludedArgumentSets = new ArrayList<>();
    }

    /**
     * This method consumes a device and a device port attempting to create a DInterface satisfying the expected interface type.
     *
     * @param device        the device to implement
     * @param interfaceType the expected interface type
     * @param port          the port to be used
     * @return the build DInterface
     * @throws ImplementationFailedException in case the build fails
     * @see Blackbird#implementDevice(Device, Class)
     */
    public abstract DInterface buildImplementation(Device device, Class<?> interfaceType, DPort port);

    public DInterface buildImplementationRecursionSave(Device device, Class<?> interfaceType, DPort port) {
        ArgumentSet set = new ArgumentSet(device, interfaceType, port);

        if (excludedArgumentSets.contains(set))
            throw new ImplementationFailedException("aborting recursive stack");

        excludedArgumentSets.add(set);
        DInterface result = buildImplementation(device, interfaceType, port);
        excludedArgumentSets.remove(set);
        return result;
    }

    public boolean isRecursiveInterfaceChecking() {
        return recursiveInterfaceChecking;
    }

    public void setRecursiveInterfaceChecking(boolean recursiveInterfaceChecking) {
        this.recursiveInterfaceChecking = recursiveInterfaceChecking;
    }

    public boolean isRecursivePortChecking() {
        return recursivePortChecking;
    }

    public void setRecursivePortChecking(boolean recursivePortChecking) {
        this.recursivePortChecking = recursivePortChecking;
    }


    private class ArgumentSet {

        Device device;
        Class<?> interfaceType;
        DPort port;

        public ArgumentSet(Device device, Class<?> interfaceType, DPort port) {
            this.device = device;
            this.interfaceType = isRecursiveInterfaceChecking() ? interfaceType : null;
            this.port = isRecursivePortChecking() ? port : null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ArgumentSet that = (ArgumentSet) o;
            return Objects.equals(device, that.device) &&
                    Objects.equals(interfaceType, that.interfaceType) &&
                    Objects.equals(port, that.port);
        }

        @Override
        public int hashCode() {
            return Objects.hash(device, interfaceType, port);
        }

    }

}
