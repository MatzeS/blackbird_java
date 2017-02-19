package blackbird.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import blackbird.core.exception.ImplementationFailedException;
import blackbird.core.util.MultiException;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * TODO destruction
 */
public class DeviceManager {

    private Logger logger = LogManager.getLogger(DeviceManager.class);

    private Device device;

    private ImplementationGraph graph;

    public DeviceManager(Device device) {
        this.device = device;

        this.graph = new ImplementationGraph();
    }

    public <T> T buildDeviceImplementation(Class<T> implementationType) {
        return buildDeviceImplementation(implementationType, device.getPort());
    }

    public synchronized <T> T buildDeviceImplementation(Class<T> implementationType, DPort port) {
        checkArgument(device != null, "device must not be null");
        checkArgument(implementationType != null, "implementationType must not be null");

        logger.trace("building {} / {} / {}", device, implementationType, port);

        List<Exception> exceptionList = new ArrayList<>();
        for (DIBuilder builder : DIBuilderRegistry.getBuilders())
            try {
                logger.trace("attempting {} on {} / {} / {}", builder.getClass().getName(), device, implementationType, port);

                DInterface implementation = builder.buildImplementationRecursionSave(device, implementationType, port);
                logger.info("build with {} on {} / {} / {}", builder.getClass().getName(), device, implementationType, port);

                graph.add(implementation);

                return (T) implementation;

            } catch (ImplementationFailedException e) { //TODO failures
                logger.trace("build failed," + builder + "/ " + device + "/" + port);
                if (!(e.getCause() instanceof IllegalArgumentException || e.getMessage().contains("recursive")))
                    exceptionList.add(e);
            }

        throw new ImplementationFailedException("no builder succeeded for " + device + "/"
                + implementationType.getName() + "/" + (port != null ? port.getClass().getName() : "no port")
                + MultiException.generateMultipleExceptionText(exceptionList));
    }

    public Device getDevice() {
        return device;
    }

    public synchronized <T> T getImplementationObjectFor(Class<T> implementationType) {
        ImplementationGraph.Node node = graph.find(implementationType);

        if (node != null)
            return (T) node.getImplementation();

        return buildDeviceImplementation(implementationType);
    }

    public synchronized <T> T interfaceDevice(Class<T> interfaceType) {
        return getImplementationObjectFor(interfaceType);
    }

    public void registerImplementation(DInterface implementation) {
        graph.add(implementation);
    }

}
