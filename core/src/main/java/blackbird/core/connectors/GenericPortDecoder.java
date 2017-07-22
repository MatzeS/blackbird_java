package blackbird.core.connectors;

import blackbird.core.HostDevice;
import blackbird.core.connection.Connection;
import blackbird.core.util.Generics;

import java.util.Collections;
import java.util.List;

public abstract class GenericPortDecoder<
        D extends HostDevice,
        P extends HostDevice.Port,
        C extends Connection,
        CP> extends PortDecoder<CP> {

    public Class<D> getDeviceType() {

        return (Class<D>) Generics.getGenericArgument(this, 0);
    }


    public Class<P> getPortType() {

        return (Class<P>) Generics.getGenericArgument(this, 1);
    }


    public Class<C> getConnectionType() {

        return (Class<C>) Generics.getGenericArgument(this, 2);
    }


    @Override
    public Class<CP> getParameterType() {

        return (Class<CP>) Generics.getGenericArgument(this, 3);
    }


    @Override
    public List<CP> decode(HostDevice device, HostDevice.Port port) {

        if (!getDeviceType().isAssignableFrom(device.getClass()))
            return Collections.emptyList();

        if (!getPortType().isAssignableFrom(port.getClass()))
            return Collections.emptyList();

        return decodeGeneric((D) device, (P) port);
    }


    @Override
    public CP encode(Connection connection) {

        if (!getConnectionType().isAssignableFrom(connection.getClass()))
            return null;

        return encodeGeneric((C) connection);
    }


    public abstract CP encodeGeneric(C connection);

    public abstract List<CP> decodeGeneric(D device, P port);

}
