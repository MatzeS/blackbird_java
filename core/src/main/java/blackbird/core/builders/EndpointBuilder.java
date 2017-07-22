package blackbird.core.builders;

import blackbird.core.Device;
import blackbird.core.exception.BFException;
import blackbird.core.exception.OtherHostException;
import blackbird.core.util.Generics;

import java.util.Collections;
import java.util.List;

public abstract class EndpointBuilder<
        D extends Device,
        I,
        E extends Device,
        EI> extends GenericBuilder<D, I> {

    private Class<E> endpointType;
    private Class<EI> endpointInterfaceType;


    public EndpointBuilder() {

        endpointType = (Class<E>)
                Generics.getGenericArgument(this, 2);
        endpointInterfaceType = (Class<EI>)
                Generics.getGenericArgument(this, 3);
    }


    public abstract I buildFromEndpoint(D device, E endpoint, EI endpointImpl);


    @Override
    public I buildGeneric(D device) throws BFException {

        OtherHostException otherHostException = new OtherHostException();

        for (E endpoint : getEndpoints(device))
            try {
                System.out.println("endpoint: " + endpoint);

                EI endpointImpl = implement(endpoint, endpointInterfaceType);

                System.out.println("endpoint build" + endpoint);

                return buildFromEndpoint(device, endpoint, endpointImpl);
            } catch (OtherHostException e) {
                otherHostException.addHosts(e.getHosts());
            } catch (Exception ignored) {
            }

        if (!otherHostException.getHosts().isEmpty())
            throw otherHostException;
        else
            throw new BFException("no port, no endpoint or endpoint not implemented");
    }


    @Override
    public boolean canBuild(Device device) {

        if (!super.canBuild(device))
            return false;

        if (getEndpoints((D) device).size() == 0)
            return false; // no endpoints

        return true;
    }


    public List<E> getEndpoints(D device) {

        return Collections.singletonList(getSingleEndpoint(device));
    }


    public E getSingleEndpoint(D device) {

        throw new UnsupportedOperationException("Not implemented" + this.getClass());
    }

}