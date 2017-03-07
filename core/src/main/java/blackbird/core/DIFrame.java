package blackbird.core;

import blackbird.core.exception.ImplementationFailedException;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DIFrame {

    @Inject
    Blackbird blackbird;

    private Device device;

    private ImplementationGraph implementationGraph;

    public DIFrame(Device device) {
        this.device = device;


    }

    private List<HostDevice> getPossibleHosts() {
        if (device instanceof HostDevice)
            return Collections.singletonList((HostDevice) device);

        List<HostDevice> result = new ArrayList<>();

        List<Device> visited = new ArrayList<>();
        Queue<Device> queue = new LinkedList<>();

        queue.add(device);
        visited.add(device);

        while (!queue.isEmpty()) {
            Device node = queue.poll();
            if (node instanceof HostDevice)
                result.add((HostDevice) node);
            else
                blackbird.getCompounds().stream()
                        .filter(c -> c.getComponents().contains(node))
                        .map(Compound::getBonds)
                        .reduce(new ArrayList<>(), (a, b) -> {
                            a.addAll(b);
                            return a;
                        }).stream()
                        .filter(b -> b.getTo().equals(node))
                        .filter(b -> !visited.contains(b.getFrom()))
                        .forEach(b -> {
                            queue.add(b.getFrom());
                            visited.add(b.getFrom());
                        });
        }

        return result;
    }

    public <T> T remoteImplement(Class<T> implementationType) {
        HostDevice host = getCurrentHost();

        if(host.isHere())
            throw new RuntimeException("no remote request");

        return host.implement(HostDevice.Interface.class).implement(device, implementationType);
    }

    private HostDevice getCurrentHost() {
        Stream<HostDevice> hostStream = getPossibleHosts().stream()
                .filter(h -> h.implement(HostDevice.Interface.class).isDeviceImplemented(device));

        if(hostStream.count() > 1)
            throw new RuntimeException("inconsistent system");

        return hostStream.findAny().orElseGet(null);
    }

    public <T> T implement(Class<T> implementationType) {

        // is implementation local -> return / build

        // check remote -> take it

        // build it on proper host

    }


    public <T> T buildImplementation(Class<T> implementationType) {

        // build lock //TODO

        // try builder

        // add to graph


    }


}

