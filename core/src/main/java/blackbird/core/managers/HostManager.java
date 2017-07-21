package blackbird.core.managers;

import blackbird.core.*;
import blackbird.core.connection.Connection;
import blackbird.core.connection.Packet;
import blackbird.core.connection.PacketConnection;
import blackbird.core.packets.HostDIReply;
import blackbird.core.packets.HostDIRequest;
import blackbird.core.rmi.RemoteMethodInvocation;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

// TODO inject connection
public class HostManager extends DeviceManager {

    private static final RemoteMethodInvocation RMI =
            new RemoteMethodInvocation("blackbird");

    private HostConnection selectedConnection;
    private List<HostConnection> connections;


    public HostManager(Blackbird blackbird, Device device) {

        super(blackbird, device);

        if (!(device instanceof HostDevice))
            throw new RuntimeException(
                    "HostManager is only appropriate for a HostDevice");
    }


    protected HostConnection connect() {

        if (selectedConnection != null)
            return selectedConnection; // TODO ensure working connection

        for (Connector connector : blackbird.getConnectors())
            try {
                Connection connection = connector.connect(getDevice());

                HostConnection hostConnection = blackbird.upgarde(connection);

                if (!hostConnection.getHost().equals(device))
                    throw new RuntimeException( //TODO
                            "device on other side is not the expected host (inconsistent model)" + hostConnection.getHost());


                return setConnection(hostConnection);

            } catch (Exception ignored) {
            }

        throw new RuntimeException("could not connect to host, no connection found");
    }


    @Override
    protected Object extendHandle(Class<?> type) {

        HostConnection connection = connect();

        try {
            HostDIReply reply = connection.sendAndReceiveAnswer(
                    new HostDIRequest(type), HostDIReply.class);

            if (reply.getException() != null)
                throw new RuntimeException("exception during remote implementation", reply.getException()); //TODO

            return RMI.getRemoteObject(type, connection, reply.getImplID());

        } catch (IOException e) {
            throw new RuntimeException("io exception during remote impl request", e); //TODO
        }

    }


    protected Optional<HostConnection> getConnection() {

        return Optional.ofNullable(selectedConnection);
    }


    @Override
    public HostDevice getDevice() {

        return (HostDevice) super.getDevice();
    }


    protected boolean isConnected() {

        return getConnection().isPresent();
    }


    public HostConnection setConnection(HostConnection connection) {

        if (this.selectedConnection != null)
            RMI.releaseConnection(this.selectedConnection);

        this.selectedConnection = connection;

        //connection.addListener();

        RMI.registerConnection(this.selectedConnection);

        return this.selectedConnection;
    }


    private class LocalDIProvider extends PacketConnection.AnswerListener<HostDIRequest> {

        @Override
        public Packet answer(HostDIRequest packet) {

            try {

                blackbird.interfaceLocalDevice(packet.getType());

                //return new HostDIReply(implID);
                return null;//TODO

            } catch (Exception e) {
                //TODO log
                return new HostDIReply(e);
            }

        }

    }

}
