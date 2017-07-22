package blackbird.core.connectors;

import blackbird.core.HostDevice;
import blackbird.core.util.Generics;

import java.util.Collections;
import java.util.List;

public abstract class GenericPortDecoder<
        D extends HostDevice,
        P extends HostDevice.Port,
        CP> extends PortDecoder<CP> {

    public Class<D> getDeviceType() {

        return (Class<D>) Generics.getGenericArgument(this, 0);
    }


    public Class<P> getPortType() {

        return (Class<P>) Generics.getGenericArgument(this, 1);
    }


    @Override
    public Class<CP> getParameterType() {

        return (Class<CP>) Generics.getGenericArgument(this, 2);
    }


    @Override
    public List<CP> decode(HostDevice device, HostDevice.Port port) {

        if (device.getClass().isAssignableFrom(getDeviceType()))
            return Collections.emptyList();
        if (port.getClass().isAssignableFrom(getPortType()))
            return Collections.emptyList();

        return decodeGeneric((D) device, (P) port);
    }


    public abstract List<CP> decodeGeneric(D device, P port);

}
