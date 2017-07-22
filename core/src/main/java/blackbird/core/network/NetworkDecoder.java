package blackbird.core.network;

import blackbird.core.HostDevice;
import blackbird.core.connectors.GenericPortDecoder;

import java.net.InetSocketAddress;
import java.util.List;

public class NetworkDecoder extends GenericPortDecoder<HostDevice, NetworkPort, InetSocketAddress> {

    @Override
    public List<InetSocketAddress> decodeGeneric(HostDevice device, NetworkPort port) {

        return port.getSocketAddresses();
    }

}
