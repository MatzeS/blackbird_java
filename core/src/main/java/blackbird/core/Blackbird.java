package blackbird.core;

public interface Blackbird {

    <T> T interfaceDevice(Device device, Class<T> type);

    boolean isLocallyImplemented(Device endpoint);

}
