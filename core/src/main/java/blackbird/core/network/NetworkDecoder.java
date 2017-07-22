package blackbird.core.network;

import blackbird.core.HostDevice;
import blackbird.core.connectors.GenericPortDecoder;

import java.net.InetSocketAddress;
import java.util.List;

public class NetworkDecoder extends GenericPortDecoder<
        HostDevice,
        NetworkPort,
        NetworkConnection,
        InetSocketAddress> {

    @Override
    public InetSocketAddress encodeGeneric(NetworkConnection connection) {

        return (InetSocketAddress) connection.getSocket().getRemoteSocketAddress();
    }


    @Override
    public List<InetSocketAddress> decodeGeneric(HostDevice device, NetworkPort port) {

        return port.getSocketAddresses();
    }

}
