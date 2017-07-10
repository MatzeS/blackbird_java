package blackbird.core;

public interface Blackbird {

    <T> T implementDevice(Device device, Class<T> type);

    <T> T interfaceDevice(Device device, Class<T> type);


    boolean isLocallyImplemented(Device endpoint);

}
