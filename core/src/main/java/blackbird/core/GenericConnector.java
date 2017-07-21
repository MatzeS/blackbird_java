package blackbird.core;

import blackbird.core.connection.Connection;
import blackbird.core.connection.exceptions.NoConnectionException;
import blackbird.core.util.Generics;
import blackbird.core.util.MultiException;

import java.util.ArrayList;
import java.util.List;

/**
 * Reduces the accepted device and port to generically defined types.
 * <p>
 * The <code>check...</code> methods can be overwritten pre-checks and exclusion.
 * <p>
 * Notice: Except device and port as wide as possible.
 *
 * @param <D> the accepted device
 * @param <P> the accepted port
 */
public abstract class GenericConnector<D extends HostDevice, P extends DPort> extends Connector {

    private Class<D> deviceType;
    private Class<P> portType;

    public GenericConnector() {
        deviceType = (Class<D>) Generics.getGenericArgument(this, 0);
        portType = (Class<P>) Generics.getGenericArgument(this, 1);
    }

    public void checkDevice(D device) {
    }

    public void checkPort(P port) {
    }

    public abstract Connection connectToPort(D device, P port);

    @Override
    public Connection connect(HostDevice device) throws NoConnectionException {
        if (!deviceType.isAssignableFrom(device.getClass()))
            throw new NoConnectionException("Connector expects a " + deviceType);

        checkDevice((D) device);


        List<Exception> exceptionList = new ArrayList<>();

        for (DPort port : device.getPorts())
            if (portType.isAssignableFrom(device.getPorts().getClass())) {
                checkPort((P) port);

                try {
                    return connectToPort((D) device, (P) port);
                } catch (Exception e) {
                    exceptionList.add(e);
                }
            }

        throw new NoConnectionException("no port succeeded: \n" +
                MultiException.generateMultipleExceptionText(exceptionList));
    }

}
