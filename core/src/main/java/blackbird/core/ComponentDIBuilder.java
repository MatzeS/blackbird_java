package blackbird.core;

import blackbird.core.util.Generics;

/**
 * Used to build {@link ComponentImplementation} extending device implementations.
 *
 * @param <D>  the device
 * @param <I>  the produced implementation
 * @param <P>  the consumed port
 * @param <CI> the inserted component implementation
 */
public abstract class ComponentDIBuilder<D extends Device, I, P, CI extends DInterface> extends GenericDIBuilder<D, I, P> {

    private Class<CI> componentDeviceInterface;

    public ComponentDIBuilder() {
        componentDeviceInterface = (Class<CI>) Generics.getGenericArgument(this, 3);
    }

    @Override
    public I build(D device, Class<I> interfaceType, P port) {
        CI componentInterface = device.getImplementation(componentDeviceInterface);
        //noinspection RedundantCast
        return (I) build(device, port, componentInterface);
    }

    public abstract I build(D device, P port, CI componentInterface);

}
