package blackbird.core.util;

import java.util.ArrayList;
import java.util.List;

import blackbird.core.Device;

public class BuildRequirement {

    private Device device;
    private Class<?> implementationType;

    public BuildRequirement(Device device, Class<?> implementationType) {
        this.device = device;
        this.implementationType = implementationType;
    }

    public Device getDevice() {
        return device;
    }

    public Class<?> getImplementationType() {
        return implementationType;
    }

    public static List<BuildRequirement> singleBuildRequirement(Device device, Class<?> type) {
        List<BuildRequirement> list = new ArrayList<>();
        list.add(new BuildRequirement(device, type));
        return list;
    }
}
