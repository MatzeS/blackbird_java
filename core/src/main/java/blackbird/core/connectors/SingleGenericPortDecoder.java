package blackbird.core.connectors;

import blackbird.core.HostDevice;
import blackbird.core.connection.Connection;

import java.util.Collections;
import java.util.List;

public abstract class SingleGenericPortDecoder<
        D extends HostDevice,
        P extends HostDevice.Port,
        C extends Connection,
        CP> extends GenericPortDecoder<D, P, C, CP> {


    @Override
    public List<CP> decodeGeneric(D device, P port) {

        return Collections.singletonList(decodeSingle(device, port));
    }


    public abstract CP decodeSingle(D device, P port);

}
