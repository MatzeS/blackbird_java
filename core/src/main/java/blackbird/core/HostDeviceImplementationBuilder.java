package blackbird.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import blackbird.core.events.PacketReceivedEvent;
import blackbird.core.exception.ImplementationFailedException;
import blackbird.core.exception.NoReplyException;
import blackbird.core.network.NetworkConnector;
import blackbird.core.packets.HandshakePacket;
import blackbird.core.rmi.RemoteMethodInvocation;
import blackbird.core.util.MultiException;

/**
 * A builder providing interfaces of other host devices using {@link HostConnection}s.
 */
public class HostDeviceImplementationBuilder extends GenericDIBuilder<HostDevice, HostDevice.Interface, DPort> {

    public static final long DEFAULT_HANDSHAKE_TIMEOUT = 2000;

    private Logger logger = LogManager.getLogger(HostDeviceImplementationBuilder.class);

    private RemoteMethodInvocation rmi;
    private int localDeviceImplementationID;

    private List<Connector> connectors;

    public HostDeviceImplementationBuilder() {

        rmi = new RemoteMethodInvocation(Blackbird.class);

        connectors = new ArrayList<>();

        try {
            addConnector(new NetworkConnector());
        } catch (IOException e) {
            logger.error("IOException starting network connector", e);
        }

    }

    public void addConnector(Connector connector) {
        connectors.add(connector);
    }

    @Override
    public HostDevice.Interface build(HostDevice device, Class<HostDevice.Interface> interfaceType, DPort port) {

        List<Exception> exceptionList = new ArrayList<>();
        for (Connector connector : connectors)
            try {
                Connection connection = connector.connectTo(device, port);

                return setupConnection(connection, device);

            } catch (Exception e) {
                exceptionList.add(e);
            }

        throw new ImplementationFailedException("no connector succeeded!\n"
                + MultiException.generateMultipleExceptionText(exceptionList));
    }

    private int getLocalDeviceImplementationID() {
        if (localDeviceImplementationID == 0)
            localDeviceImplementationID = rmi.registerObject(blackbird.getLocalDeviceImplementation());
        return localDeviceImplementationID;
    }

    private HandshakePacket performHandshake(HostConnection hostConnection) throws IOException {
        return performHandshake(hostConnection, DEFAULT_HANDSHAKE_TIMEOUT);
    }

    private HandshakePacket performHandshake(HostConnection hostConnection, long timeout) throws IOException {

        HandshakePacket handshake = new HandshakePacket(blackbird.getLocalDevice(), localDeviceImplementationID);
        HandshakeResponder handshakeResponder = new HandshakeResponder(handshake);
        hostConnection.addListener(handshakeResponder);

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

        hostConnection.send(handshake);

        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (lock) {
            try {
                lock.wait(timeout);
            } catch (InterruptedException ignored) {
            }
        }

        hostConnection.removeListener(handshakeResponder);

        if (reply[0] == null)
            throw new NoReplyException("no handshake received");

        return reply[0];
    }

    public void removeConnector(Connector connector) {
        connectors.remove(connector);
    }

    /**
     * Performs the handshake and registers the host device interface achieved by RMI to the device service.
     * <p>
     * If the <code>expectedDevice</code> parameter is null,
     * its completely ignored. Else the remote connection side
     * has to identify its self as the given device.
     *
     * @param connection     the connection
     * @param expectedDevice the expected device on the remote connection side
     * @throws ImplementationFailedException if the expected device does not match
     * @throws IOException                   if an IO error occurs
     */
    public HostDevice.Interface setupConnection(Connection connection, Device expectedDevice)
            throws IOException, ImplementationFailedException {

        HostConnection hostConnection = new HostConnection(connection);
        HandshakePacket handshakeResult = performHandshake(hostConnection);

        HostDevice remoteDevice = handshakeResult.getDevice();

        if (expectedDevice != null)
            if (!expectedDevice.equals(remoteDevice))
                throw new ImplementationFailedException(
                        "device on other side is not the expected host (inconsistent model)" + remoteDevice);


        rmi.registerConnection(hostConnection);

        return rmi.getRemoteObject(
                HostDevice.Interface.class,
                hostConnection,
                handshakeResult.getRmiImplementationID());
    }

    public void setupConnection(Connection connection) throws IOException, ImplementationFailedException {
        HostDevice.Interface remoteInterface = setupConnection(connection, null);

        blackbird.registerImplementation(remoteInterface);
    }

    /**
     * In case a connection receives a handshake, the other side expects an
     * answer handshake.
     */
    public class HandshakeResponder extends PacketConnection.PacketTypeListener<HandshakePacket> {

        private HandshakePacket handshake;

        public HandshakeResponder(HandshakePacket handshake) {
            this.handshake = handshake;
        }

        @Override
        public void packetReceived(HandshakePacket packet, PacketReceivedEvent event) {
            try {
                event.getSource().send(handshake);
            } catch (IOException e) {
                logger.error("I/O Exception trying to answer a handshake", e);
            }
        }

    }

}
