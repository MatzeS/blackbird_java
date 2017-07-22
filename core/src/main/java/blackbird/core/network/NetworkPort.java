package blackbird.core.network;

import blackbird.core.HostDevice;
import blackbird.core.util.Hex;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Storing MAC and possible according IP addresses with port.
 */
public class NetworkPort extends HostDevice.Port {

    private static final long serialVersionUID = 5388626648137321057L;

    private byte[] macAddress;
    private List<InetSocketAddress> socketAddresses;

    public NetworkPort(byte[] macAddress) {
        this.macAddress = macAddress;
        socketAddresses = new ArrayList<>();
    }

    public NetworkPort(String macAddress) {
        this(Hex.hexStringToByteArray(macAddress));
    }

    public NetworkPort(String mac, InetSocketAddress... socketAddresses) {
        this(mac);
        this.socketAddresses = Arrays.stream(socketAddresses).collect(Collectors.toList());
    }

    public byte[] getMacAddress() {
        return macAddress;
    }

    public List<InetSocketAddress> getSocketAddresses() {
        return socketAddresses;
    }

    public void addSocketAddress(InetSocketAddress address) {
        socketAddresses.add(address);
    }

}
