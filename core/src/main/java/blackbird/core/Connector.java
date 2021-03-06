package blackbird.core;

import blackbird.core.exception.NoConnectionException;

/**
 * Deriving objects are used by the {@link HostDeviceImplementationBuilder}
 * to connect to other host devices.
 * <p>
 * The pattern is similar to the {@link DIBuilder}s, but here only a {@link Connection} is produced.
 * <p>
 * Typically the {@link GenericConnector} is extended.
 *
 * @see GenericConnector
 */
public interface Connector {

    Blackbird blackbird = Blackbird.getInstance();

    /**
     * The general contract for <code>connectTo</code> is to return a fresh connection
     * with no communication done including no handshake and no remote side device check
     * or throw an exception.
     * <p>
     * TODO not null
     *
     * @param device the device to connect to, typically unused1
     * @param port   the port
     * @return the connection, not null
     * @throws NoConnectionException if the connection could not be established
     */
    Connection connectTo(HostDevice device, DPort port) throws NoConnectionException;

}
