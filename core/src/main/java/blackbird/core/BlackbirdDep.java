package blackbird.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import blackbird.core.exception.ImplementationNotAvailableException;

public class BlackbirdDep {


    private HostDevice.Interface localDeviceImplementation;
    private HostDeviceImplementationBuilder hostDeviceImplementationBuilder;

    public HostDeviceImplementationBuilder getHostDeviceImplementationBuilder() {
        return hostDeviceImplementationBuilder;
    }

    public void setHostDeviceImplementationBuilder(HostDeviceImplementationBuilder hostDeviceImplementationBuilder) {
        if (hostDeviceImplementationBuilder != null)
            DIBuilderRegistry.removeBuilder(hostDeviceImplementationBuilder);

        this.hostDeviceImplementationBuilder = hostDeviceImplementationBuilder;

        DIBuilderRegistry.addBuilder(hostDeviceImplementationBuilder);
    }

    public HostDevice getLocalDevice() {
        return localDevice;
    }

    public void setLocalDevice(HostDevice localDevice) {
        this.localDevice = localDevice;
    }

    public HostDevice.Interface getLocalDeviceImplementation() {
        return localDeviceImplementation;
    }

    public void implementLocalDevice(Class<? extends HostDevice.Interface> implementationType) {
        localDeviceImplementation = implementDevice(localDevice, implementationType);
    }


    public void registerImplementation(DInterface implementation) {
        getOrCreateDeviceManager(implementation.getDevice()).registerImplementation(implementation);
    }

}
