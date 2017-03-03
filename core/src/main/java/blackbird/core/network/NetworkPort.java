package blackbird.core.network;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import blackbird.core.util.Hex;

/**
 * A port consumed by the NetworkConnector storing MAC and IP addresses.
 */
public class NetworkPort extends DPort {

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

}
