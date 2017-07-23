package blackbird.core.managers;

import blackbird.core.*;
import blackbird.core.connection.Connection;
import blackbird.core.connection.Packet;
import blackbird.core.connection.PacketConnection;
import blackbird.core.connectors.Connector;
import blackbird.core.packets.HostDIReply;
import blackbird.core.packets.HostDIRequest;
import blackbird.core.rmi.RemoteMethodInvocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

// TODO inject connection
public class HostManager extends DeviceManager {

    private static final RemoteMethodInvocation RMI =
            new RemoteMethodInvocation("blackbird");


    private Logger logger = LogManager.getLogger(this.getClass());

    private HostConnection selectedConnection;

    private Map<Object, HostConnection> connections;

    private LocalDIProvider localDIProvider;


    public HostManager(Blackbird blackbird, Device device) {

        super(blackbird, device);

        if (!(device instanceof HostDevice))
            throw new RuntimeException(
                    "HostManager is only appropriate for a HostDevice");

        localDIProvider = new LocalDIProvider();

        connections = new HashMap<>();
    }


    private List<Object> getParameters() {

        return blackbird.getDecoders().stream().map(decoder -> decoder.decode(getDevice()))
                .flatMap(List::stream).collect(Collectors.toList());
    }


    protected boolean connect() {

        if (isConnected())
            return true; // TODO ensure working connection


        for (Object parameter : getParameters())
            for (Connector connector :
                    blackbird.getConnectors().stream()
                            .filter(c -> c.accepts(parameter))
                            .collect(Collectors.toList()))
                try {
                    HostConnection connection = connector.connect(getDevice(), parameter);
                    blackbird.sendDeviceIdentificationTo(connection);
                    connection.addListener(blackbird.getIdentificationResponder());

                    addConnection(connection);

                    return true;
                } catch (Exception ignored) { //TODO multiexception
                    ignored.printStackTrace();
                }

        throw new RuntimeException("could not connect to host, no connection found" + getParameters().size()); //TODO text
    }


    @Override
    protected Object extendHandle(Class<?> type) {

        connect(); //TODO error

        try {
            HostDIReply reply = getSelectedConnection().sendAndReceiveAnswer(
                    new HostDIRequest(type), 2, HostDIReply.class); //TODO better give the other time for a handshake

            if (reply.getException() != null)
                throw new RuntimeException("exception during remote implementation", reply.getException()); //TODO

            return RMI.getRemoteObject(type, getSelectedConnection(), reply.getImplID());

        } catch (IOException e) {
            throw new RuntimeException("io exception during remote impl request", e); //TODO
        }

    }


    private HostConnection getSelectedConnection() {

        return selectedConnection;
    }


    @Override
    public HostDevice getDevice() {

        return (HostDevice) super.getDevice();
    }


    private boolean isConnected() {

        return getSelectedConnection() != null;
    }


    private void selectConnection(HostConnection connection) {

        if (!connections.containsValue(connection))
            throw new RuntimeException("connection does not belong to this device/manager");

        this.selectedConnection = connection;
    }


    public Object getParametersFromConnection(Connection connection) {

        return blackbird.getDecoders().stream()
                .map(decoder -> decoder.encode(connection))
                .filter(Objects::nonNull)
                .findAny().orElse(connection);
    }


    public void addConnection(HostConnection connection) {

        logger.trace("added connection {}", connection);

        connections.put(getParametersFromConnection(connection), connection);

        RMI.registerConnection(connection);
        connection.addListener(localDIProvider);

        if (!isConnected())
            selectConnection(connection);
    }


    private class LocalDIProvider extends PacketConnection.AnswerListener<HostDIRequest> {

        @Override
        public Packet answer(HostDIRequest packet) {

            try {

                DInterface impl = (DInterface) blackbird.interfaceLocalDevice(packet.getType());

                int ID = RMI.registerObject(impl); //TODO equal register?

                return new HostDIReply(ID);

            } catch (Exception e) {
                //TODO log
                return new HostDIReply(e);
            }

        }

    }

}
