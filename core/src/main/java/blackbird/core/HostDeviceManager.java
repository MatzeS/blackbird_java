package blackbird.core;

import java.io.IOException;
import java.util.Optional;

import blackbird.core.connection.Connection;
import blackbird.core.connection.PacketConnection;
import blackbird.core.connection.PacketReceivedEvent;
import blackbird.core.connection.exceptions.NoReplyException;
import blackbird.core.packets.HandshakePacket;
import blackbird.core.packets.HostDIReply;
import blackbird.core.packets.HostDIRequest;
import blackbird.core.rmi.RemoteMethodInvocation;

// TODO inject connection
public class HostDeviceManager extends DeviceManager implements PacketConnection.Listener {

    public static final long DEFAULT_HANDSHAKE_TIMEOUT = 2000;

    private static RemoteMethodInvocation rmi = new RemoteMethodInvocation("blackbird");

    private HostConnection connection;

    private HandshakePacket ownHandshake;

    public HostDeviceManager(BlackbirdImpl blackbird, Device device) {
        super(blackbird, device);

        ownHandshake = new HandshakePacket(blackbird.getLocalDevice());

        if (!(device instanceof HostDevice))
            throw new RuntimeException("wrong device"); //TODO
    }

    protected HostConnection connect() {
        if (connection != null)
            return connection; // TODO ensure working connection

        for (Connector connector : blackbird.getConnectors())
            try {
                Connection connection = connector.connect(getDevice());

                HostConnection hostConnection = new HostConnection(connection);
                HandshakePacket handshakeResult = performHandshake(hostConnection);

                HostDevice remoteDevice = handshakeResult.getDevice();

                if (!device.equals(remoteDevice))
                    throw new RuntimeException( //TODO
                            "device on other side is not the expected host (inconsistent model)" + remoteDevice);


                return setConnection(hostConnection);

            } catch (Exception ignored) {
            }

        throw new RuntimeException("could not connect to host, no connection found");
    }

    @Override
    protected Object extendHandle(Class<?> type) {

        if (!blackbird.getLocalDevice().equals(device))
            throw new RuntimeException("inappropriate implementation of remote host");

        HostConnection connection = connect();

        try {
            HostDIReply reply = connection.sendAndReceive(
                    new HostDIRequest(type), HostDIReply.class);

            if (reply.getException() != null)
                throw new RuntimeException("exception during remote implementation", reply.getException()); //TODO

            return rmi.getRemoteObject(type, connection, reply.getImplID());

        } catch (IOException e) {
            throw new RuntimeException("io exception during remote impl request", e); //TODO
        }

    }

    protected Optional<HostConnection> getConnection() {
        return Optional.ofNullable(connection);
    }

    @Override
    protected HostDevice getDevice() {
        return (HostDevice) super.getDevice();
    }

    protected boolean isConnected() {
        return getConnection().isPresent();
    }

    @Override
    public void packetReceived(PacketReceivedEvent event) {
        try {
            event.getSource().send(ownHandshake);
        } catch (IOException e) {
            e.printStackTrace(); //TODO logger
        }
    }

    private HandshakePacket performHandshake(HostConnection hostConnection, long timeout) throws IOException {

        hostConnection.addListener(this);

        Object lock = new Object();

        final HandshakePacket[] reply = new HandshakePacket[1];
        hostConnection.addListener(new PacketConnection.PacketTypeListener<HandshakePacket>() {

            @Override
            public void packetReceived(HandshakePacket packet, PacketReceivedEvent event) {
                reply[0] = packet;
                synchronized (lock) {
                    lock.notify();
                }
            }
        });

        hostConnection.send(ownHandshake);

        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (lock) {
            try {
                lock.wait(timeout);
            } catch (InterruptedException ignored) {
            }
        }

        hostConnection.removeListener(this);

        if (reply[0] == null)
            throw new NoReplyException("no handshake received");

        return reply[0];
    }

    private HandshakePacket performHandshake(HostConnection hostConnection) throws IOException {
        return performHandshake(hostConnection, DEFAULT_HANDSHAKE_TIMEOUT);
    }

    private HostConnection setConnection(HostConnection connection) {
        if (this.connection != null)
            rmi.releaseConnection(this.connection);

        this.connection = connection;

        rmi.registerConnection(this.connection);

        return this.connection;
    }

}
