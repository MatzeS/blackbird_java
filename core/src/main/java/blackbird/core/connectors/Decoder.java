package blackbird.core.connectors;

import blackbird.core.HostDevice;
import blackbird.core.util.Generics;

import java.util.List;

public abstract class Decoder<P> {


    public abstract List<P> decode(HostDevice device);


    public Class<P> getParameterType() {

        return (Class<P>) Generics.getGenericArgument(this, 0);
    }

}
