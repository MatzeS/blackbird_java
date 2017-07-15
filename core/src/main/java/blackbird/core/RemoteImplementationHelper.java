package blackbird.core;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import blackbird.core.HostDevice.Interface;

public class RemoteImplementationHelper {

    public static <T> T getRemoteImpl(Blackbird blackbird, DeviceManager deviceManager, Class<T> type) {
        getLock().lock();
        try {


            T impl = impl = blackbird.getAvailableHostDeviceInterfaces().entrySet().stream()
                    .filter(e -> e.getValue().hasDeviceImplementation(device))
                    .findAny().map(e -> e.getValue().interfaceDevice(device, type))
                    .orElseThrow(null);

            // system wide check
            // gathering all possible bridges...
            ListMultimap<Integer, HostDevice> bridges = ArrayListMultimap.create();
            Map<HostDevice, Interface> bridgeInterfaces = new HashMap<>();


            blackbird.getAvailableHostDeviceInterfaces().entrySet().stream()
                    .forEach(e -> {
                        if (e.)
                            int distance = hostInterface.getImplementationDistanceTo(device);

                        if (distance != -1) {
                            bridges.put(distance, host);
                            bridgeInterfaces.put(host, hostInterface);
                        }
                    });

            for (HostDevice host : blackbird.getDevices().stream().distinct()
                    .filter(d -> d instanceof HostDevice)
                    .filter(d -> !d.equals(blackbird.getLocalDevice()))
                    .filter(d -> !d.equals(device))
                    .map(d -> (HostDevice) d)
                    .collect(Collectors.toList())) {

                try {
                    Interface hostInterface =
                            blackbird.interfaceDevice(host, Interface.class);
                    int distance = hostInterface.getImplementationDistanceTo(device);

                    if (distance != -1) {
                        bridges.put(distance, host);
                        bridgeInterfaces.put(host, hostInterface);
                    }
                } catch (Exception ignored) {
                }

            }

            if (bridges.isEmpty())
                return null;

            ListMultimap<HostDevice, Integer> distanceMap = Multimaps.invertFrom(bridges, ArrayListMultimap.create());
            //List<Integer> distances = StreamSupport.stream(bridges.keySet()).collect(Collectors.toList());
            //distances = StreamSupport.stream(distances).sorted((a, b) -> a - b).collect(Collectors.toList());

            //...and try to use them

            @SuppressWarnings("OptionalGetWithoutIsPresent")
            int minDistance = bridges.keySet().stream().mapToInt(i -> i).min().getAsInt();
            HostDevice bridge = bridges.get(minDistance).get(0); // any bridge with min distance

            Interface bridgeInterface = bridgeInterfaces.get(bridge);

            Object implementation = bridgeInterface.
                    interfaceDevice(device, type);

            blackbird.logger.info("{}, acquired remote implementation from {}", device, bridge);

            remoteImplgetLock().unlock();

            return (T) implementation;

        } finally {
            getLock().unlock();
        }

    }

}
