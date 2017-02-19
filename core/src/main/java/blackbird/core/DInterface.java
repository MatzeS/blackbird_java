package blackbird.core;

import blackbird.core.rmi.Remote;

/**
 * The device interface is the facade for each device produced by blackbird.<br>
 * It is accessible from any location in the system and defines the abilities of any device.<br>
 * The functions defined in this interface are mainly used internal (implementation con-/destruction).
 */
public interface DInterface extends Remote {

    /**
     * Destroys the implementation, freeing IO resources and collecting
     * persistent information of the device in a DIState for later reconstruction (loadState).
     *
     * @return state for reconstruction, null by default.
     */
    DIState destroy();

    /**
     * The device accessed by this interface.
     *
     * @return the device
     */
    Device getDevice();

    /**
     * The host device storing and managing the implementation of the device.
     *
     * @return the host
     */
    HostDevice getHost();

    /**
     * This method can be used to restore any persistent values of the implementation.
     *
     * @param state the previous state
     */
    void loadState(DIState state);

}
