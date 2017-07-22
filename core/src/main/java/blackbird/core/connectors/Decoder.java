package blackbird.core.connectors;

import blackbird.core.HostDevice;
import blackbird.core.connection.Connection;
import blackbird.core.util.Generics;

import java.util.List;

public abstract class Decoder<P> {

    public abstract P encode(Connection connection);

    public abstract List<P> decode(HostDevice device);


    public Class<P> getParameterType() {

        return (Class<P>) Generics.getGenericArgument(this, 0);
    }

}
