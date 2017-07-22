package blackbird.core;

import blackbird.core.builders.DIBuilder;
import blackbird.core.connection.Connection;
import blackbird.core.connection.PacketConnection;
import blackbird.core.connection.PacketReceivedEvent;
import blackbird.core.connection.exceptions.NoReplyException;
import blackbird.core.connectors.Connector;
import blackbird.core.managers.AgentManager;
import blackbird.core.managers.DeviceManager;
import blackbird.core.managers.HostManager;
import blackbird.core.managers.LocalHostDeviceManager;
import blackbird.core.packets.HandshakePacket;
import blackbird.core.util.ConstructionPlan;
import blackbird.core.util.ValueKeyedMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

//TODO threadsafety
//TODO hosthopping
public class Blackbird {


    public static final long DEFAULT_HANDSHAKE_TIMEOUT = 2000;
    private Logger logger = LogManager.getLogger(Blackbird.class);
    private Cluster cluster;
    private ValueKeyedMap<Device, DeviceManager> deviceManagers;
    private List<DIBuilder> builders;
    private List<Connector> connectors;
    private HostDevice localDevice;
    private HandshakePacket ownHandshake;

    PacketConnection.Listener handshakeResponder = new PacketConnection.Listener() {
        @Override
        public void packetReceived(PacketReceivedEvent event) {

            try {
                event.getSource().send(ownHandshake);
            } catch (IOException e) {
                e.printStackTrace(); //TODO logger
            }
        }
    };


    public Blackbird() {

        deviceManagers = new ValueKeyedMap<>(DeviceManager::getDevice);
        builders = new ArrayList<>(DIBuilderRegistry.getBuilders());
        connectors = new ArrayList<>();

    }


    public void addConnector(Connector connector) {

        connector.setAcceptConnectionHandle(c ->
                Blackbird.this.acceptConnection((Connection) c));
        connectors.add(connector);
    }


    public Map<HostDevice, HostDevice.Interface> getAvailableHostDeviceInterfaces() {

        return cluster.getHostDevices().stream()
                .filter(d -> {
                    try {
                        interfaceHost(d);
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                })
                .collect(Collectors.toMap(Function.identity(),
                        this::interfaceHost));
    }


    public List<DIBuilder> getBuilders() {

        return builders;
    }


    public List<Connector> getConnectors() {

        return connectors;
    }


    protected DeviceManager getDeviceManager(Device device) {

        DeviceManager manager = deviceManagers.get(device);

        if (manager == null) {
            if (device.equals(localDevice))
                manager = new LocalHostDeviceManager(this, device);
            else if (device instanceof HostDevice)
                manager = new HostManager(this, device);
            else
                manager = new AgentManager(this, device); //TODO use some builder here, also for thelocal device
            deviceManagers.put(manager);
        }

        return manager;
    }


    public Collection<Device> getDevices() {

        return cluster.getDevices();
    }


    public HostDevice getLocalDevice() {

        return localDevice;
    }


    public void setLocalDevice(HostDevice localDevice) {

        this.localDevice = localDevice;

        ownHandshake = new HandshakePacket(localDevice);
    }


    public <T> T interfaceDevice(Device device, Class<T> type) {

        return (T) getDeviceManager(device).getHandle(type);
    }


    public HostDevice.Interface interfaceHost(HostDevice host) {

        return interfaceDevice(host, HostDevice.Interface.class);
    }


    public boolean isLocalDevice(Device device) {

        return getLocalDevice().equals(device);
    }


    public boolean isLocallyImplemented(Device device) {

        if (device.equals(getLocalDevice()))
            return true;
        if (device.isHost())
            return false;

        return getAgentManager(device)
                .isLocallyImplemented();
    }


    public ConstructionPlan constructHandle(ConstructionPlan plan) {

        return getAgentManager(plan.getDevice()).constructHandle(plan);
    }


    private AgentManager getAgentManager(Device device) {

        return (AgentManager) getDeviceManager(device);
    }


    public Cluster getCluster() {

        return cluster;
    }


    public void setCluster(Cluster cluster) {

        this.cluster = cluster;
    }


    private HandshakePacket performHandshake(HostConnection hostConnection, long timeout) throws IOException {

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

        hostConnection.send(ownHandshake);

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


    private HandshakePacket performHandshake(HostConnection hostConnection) throws IOException {

        return performHandshake(hostConnection, DEFAULT_HANDSHAKE_TIMEOUT);
    }


    public HostConnection upgarde(Connection connection) throws IOException {

        HostConnection hostConnection = new HostConnection(connection);
        HandshakePacket handshakeResult = performHandshake(hostConnection);

        HostDevice remoteDevice = handshakeResult.getDevice();
        hostConnection.setHost(remoteDevice);
        return hostConnection;
    }


    public void acceptConnection(Connection connection) {

        HostConnection hostConnection = null;
        try {
            hostConnection = upgarde(connection);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ((HostManager) getDeviceManager(hostConnection.getHost())).setConnection(hostConnection);
    }


    public <T> T interfaceLocalDevice(Class<T> type) {

        return interfaceDevice(getLocalDevice(), type);
    }


}
