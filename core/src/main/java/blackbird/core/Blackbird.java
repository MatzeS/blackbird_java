package blackbird.core;

import blackbird.core.builders.DIBuilder;
import blackbird.core.connection.PacketConnection;
import blackbird.core.connection.PacketReceivedEvent;
import blackbird.core.connectors.Connector;
import blackbird.core.connectors.Decoder;
import blackbird.core.managers.AgentManager;
import blackbird.core.managers.DeviceManager;
import blackbird.core.managers.HostManager;
import blackbird.core.managers.LocalHostDeviceManager;
import blackbird.core.packets.HostIdentificationPacket;
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

    private Logger logger = LogManager.getLogger(Blackbird.class);
    private Cluster cluster;
    private ValueKeyedMap<Device, DeviceManager> deviceManagers;
    private List<DIBuilder> builders;
    private List<Decoder> decoders;
    private List<Connector> connectors;
    private HostDevice localDevice;
    private HostIdentificationPacket ownHandshake;

    private PacketConnection.Listener identificationResponder = new PacketConnection.PacketTypeListener<HostIdentificationPacket>() {
        @Override
        public void packetReceived(HostIdentificationPacket packet, PacketReceivedEvent event) {

            if (packet.doAnswer())
                try {
                    event.getSource().send(
                            new HostIdentificationPacket(localDevice)
                    );
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
                Blackbird.this.acceptConnection((HostConnection) c));
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

        ownHandshake = new HostIdentificationPacket(localDevice);
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


    private HostManager getHostManager(Device device) {

        return (HostManager) getDeviceManager(device);
    }


    public Cluster getCluster() {

        return cluster;
    }


    public void setCluster(Cluster cluster) {

        this.cluster = cluster;
    }


    public List<Decoder> getDecoders() {

        return this.decoders;
    }


    public void acceptConnection(HostConnection connection) {

        connection.addListener(identificationResponder);

        try {
            HostDevice host = HostIdentificationPacket.identify(connection);

            getHostManager(host).addConnection(connection);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public <T> T interfaceLocalDevice(Class<T> type) {

        return interfaceDevice(getLocalDevice(), type);
    }


}
