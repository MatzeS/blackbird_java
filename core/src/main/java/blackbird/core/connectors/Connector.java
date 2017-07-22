package blackbird.core.connectors;

import blackbird.core.HostConnection;
import blackbird.core.HostDevice;
import blackbird.core.connection.Connection;
import blackbird.core.connection.exceptions.NoConnectionException;
import blackbird.core.packets.HostIdentification;
import blackbird.core.util.Generics;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Connectors are used to setup {@link Connection}s between hosts
 * since their implementations are only provided locally and
 * cannot be build from remote.
 * <p>
 * Typically the {@link } is extended.
 */
public abstract class Connector<P> {

    private Consumer<HostConnection> acceptConnectionHandle;


    public void setAcceptConnectionHandle(Consumer<HostConnection> handle) {

        this.acceptConnectionHandle = handle;
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
     * @param parameters the target host
     * @return a connection to the target, not null
     * @throws NoConnectionException if the connection could not be established
     */
    public abstract Connection connect(P parameters);


    public HostConnection connect(HostDevice device, Object parameters) {

        Connection connection = connect((P) parameters);

        try {
            HostConnection hostConnection = new HostConnection(connection);
            HostDevice remoteHost = HostIdentification.identify(hostConnection);

            System.out.println("remote host" + remoteHost);
            System.out.println("expected" + device);

            if (!remoteHost.equals(device))
                throw new RuntimeException("inconsistent model, remote host is not expected one");

            return hostConnection;
        } catch (IOException e) {
            throw new RuntimeException("IO Exception", e);
        }
    }


    /**
     * This method is used when the connector accepts
     * an incoming connection.
     * Generally the connection should be raw without anny communication
     * done accordingly to the connect-method.
     * <p>
     * From here the connection is adapted to a host connection
     * and further communication like handshake and so will be done.
     *
     * @param connection passed to blackbird
     */
    public void acceptConnection(HostConnection connection) {

        acceptConnectionHandle.accept(connection);
    }


    public boolean accepts(Class<?> parameterType) {

        return getParameterType().isAssignableFrom(parameterType);
    }


    public boolean accepts(Object parameters) {

        return accepts(parameters.getClass());
    }


    public Class<P> getParameterType() {

        return (Class<P>) Generics.getGenericArgument(this, 0);
    }

}
