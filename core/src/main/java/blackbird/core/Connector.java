package blackbird.core;

import blackbird.core.connection.Connection;
import blackbird.core.connection.exceptions.NoConnectionException;

import java.util.function.Consumer;

/**
 * Connectors are used to setup {@link Connection}s between hosts
 * since their implementations are only provided locally and
 * cannot be build from remote.
 * <p>
 * Typically the {@link GenericConnector} is extended.
 */
public abstract class Connector {

    private Consumer<Connection> acceptConnection;

    public void setAcceptConnection(Consumer<Connection> acceptConnection) {
        this.acceptConnection = acceptConnection;
    }

    /**
     * An implementation of this method should return a new connection
     * targeting the given device.
     * <p>
     * This connection should be fresh and unused.
     * No communication neither handshake nor remote side device check should
     * be done.
     * <p>
     * Keep in mind this method might be called on the same device
     * and it should not connect along the same 'path'.
     * It should only do so if other possible paths were used meanwhile
     * and the repeated path is corrupted. TODO
     *
     * @param device the target device
     * @return a connection to the target, not null
     * @throws NoConnectionException if the connection could not be established
     */
    public abstract Connection connect(HostDevice device);

    /**
     * This method is used when the connector accepts
     * an incoming connection.
     * Generally the connection should be raw without anny communication
     * done accordingly to the connect-method.
     * <p>
     * From here the connection is adapted to a host connection
     * and further communication like handshake and so will be done.
     *
     * @param connection
     */
    public void acceptConnection(Connection connection) {
        acceptConnection.accept(connection);
    }

}
