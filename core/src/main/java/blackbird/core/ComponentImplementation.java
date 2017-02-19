package blackbird.core;

/**
 * The class is a flat delegator to a underlying implementation.
 * <p>
 * It simplifies the implementation construction together with the {@link ComponentDIBuilder}
 * reducing the constructor arguments and pre-constructing the underlying implementation.
 * <p>
 * Similar often extended interfaces are encouraged to provide a similar pattern.
 * <p>
 * Note: This pattern is crucial to complex implementation dependency graphs.
 * An implementation might extends and consumes an device interface with multiple implementation and therefore
 * can not extend the implementations but has to use the composition pattern allowing different implementations to
 * be used.
 *
 * @param <D> the device implemented by this implementation - {@link DInterface}
 * @param <I> the underlying DInterface used by this implementation
 */
public class ComponentImplementation<D extends Device, I extends DInterface> implements DInterface {

    protected I component;

    public ComponentImplementation(I component) {
        this.component = component;
    }

    @Override
    public DIState destroy() {
        return null;
    }

    @Override
    public D getDevice() {
        return (D) component.getDevice();
    }

    @Override
    public HostDevice getHost() {
        return component.getHost();
    }

    @Override
    public void loadState(DIState state) {
    }

}
