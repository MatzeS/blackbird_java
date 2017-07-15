package blackbird.core;

public class LocalDeviceManager extends DeviceManager {


    public LocalDeviceManager(BlackbirdImpl blackbird, Device device) {
        super(blackbird, device);
    }

    @Override
    protected Object extendHandle(Class<?> type) {
        return buildHandle(type);
    }

}
