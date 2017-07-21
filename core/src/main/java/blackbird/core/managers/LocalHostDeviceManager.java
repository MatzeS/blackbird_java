package blackbird.core.managers;

import blackbird.core.Blackbird;
import blackbird.core.Device;
import blackbird.core.HostDevice;
import blackbird.core.HostDeviceImplementation;
import blackbird.core.builders.GenericBuilder;

public class LocalHostDeviceManager extends ConstructingManager {

    public LocalHostDeviceManager(Blackbird blackbird, Device device) {

        super(blackbird, device);

        blackbird.getBuilders().add(new Builder());

    }


    @Override
    protected Object extendHandle(Class<?> type) {

        return build(type);
    }


    private class Builder extends GenericBuilder<HostDevice, HostDeviceImplementation> {

        @Override
        public HostDeviceImplementation buildGeneric(HostDevice device) {

            return new HostDeviceImplementation(blackbird);
        }


        @Override
        public boolean canBuild(Device device) {

            return device.equals(blackbird.getLocalDevice());
        }

    }

}
