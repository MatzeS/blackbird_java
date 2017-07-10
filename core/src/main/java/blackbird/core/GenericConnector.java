package blackbird.core;

import blackbird.core.connection.exceptions.NoConnectionException;
import blackbird.core.util.Generics;

/**
 * The class reduces the accepted connect parameters to generically defined types.
 * <p>
 * The <code>check...</code> methods can be overwritten for more complex analysis and exclusion.
 * <p>
 * Notice: Expect device and port as wide as possible.
 *
 * @param <D> the accepted device
 * @param <P> the accepted port
 */
public abstract class GenericConnector<D extends HostDevice, P extends DPort> implements Connector {

    private Class<D> deviceType;
    private Class<P> portType;

    public GenericConnector() {
        deviceType = (Class<D>) Generics.getGenericArgument(this, 0);
        portType = (Class<P>) Generics.getGenericArgument(this, 1);
    }

    public void checkDevice(D device) throws NoConnectionException {
    }

    public void checkPort(P port) throws NoConnectionException {
    }

    public abstract Connection connect(D device, P port) throws NoConnectionException;

    public Connection connectTo(HostDevice device, DPort port) throws NoConnectionException {
        if (!deviceType.isAssignableFrom(device.getClass()))
            throw new NoConnectionException("Connector expects a " + deviceType);

        if (!portType.isAssignableFrom(port.getClass()))
            throw new NoConnectionException("Connector expects a " + portType);

        checkDevice((D) device);
        checkPort((P) port);

        return connect((D) device, (P) port);
    }

}
