package blackbird.core.connectors;

import blackbird.core.HostDevice;

import java.util.List;
import java.util.stream.Collectors;

public abstract class PortDecoder<P> extends Decoder<P> {

    @Override
    public List<P> decode(HostDevice device) {

        return device.getPorts().stream()
                .map(p -> decode(device, p))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }


    public abstract List<P> decode(HostDevice device, HostDevice.Port port);

}
