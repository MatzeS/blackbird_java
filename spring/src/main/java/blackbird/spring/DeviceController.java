package blackbird.spring;

import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import blackbird.core.Blackbird;
import blackbird.core.Device;
import blackbird.core.ports.ParentDevicePort;

@Component
public class DeviceController {

    private static Reflections reflections = new Reflections("blackbird");

    protected Blackbird blackbird = Blackbird.getInstance();

    @Autowired
    protected List<DataProvider> dataProviders;
    protected Map<Integer, Device> devices;
    protected Map<Device, Integer> deviceIDs;
    @Autowired
    blackbird.spring.stomp.Controller stompController;

    private void addDevice(Device device) {
        int ID = device.hashCode();
        while (devices.containsKey(ID))
            ID += 31;
        devices.put(ID, device);
        deviceIDs.put(device, ID);

        stompController.emitDeviceData(ID);
    }


    public Map<String, Object> getDeviceData(int deviceID) {
        Device device = devices.get(deviceID);

        Map<String, Object> data = new HashMap<>();
        data.put("ID", deviceID);
        data.put("UI", device.getUIData());

        Object implementation = null;
        Exception implementationException = null;
//        try {
        //    implementation = blackbird.implementDevice(device.getImplementationInterface(), device);
//        } catch (Exception e) {
//            implementationException = e;
//        }

        for (DataProvider dataProvider : dataProviders) {
            if (dataProvider.getDeviceType().equals(Device.class))
                dataProvider.populate(data, data, device, implementation, implementationException);
            else if (dataProvider.applicable(device, implementation)) {
                Map<String, Object> subData = new HashMap<>();
                data.put(dataProvider.getDeviceType().getSimpleName(), subData);
                dataProvider.populate(data, subData, device, implementation, implementationException);
            }
        }

        if (device.getPort() instanceof ParentDevicePort) {
            Device parent = ((ParentDevicePort) device.getPort()).getParentDevice();
            int parentID = deviceIDs.get(parent);
            // data.put("parent", getDeviceData(parentID));
            data.put("parentID", parentID);
        }

        return data;
    }

    public Map<Device, Integer> getDeviceIDs() {
        return deviceIDs;
    }

    public Map<Integer, Device> getDevices() {
        return devices;
    }

    @PostConstruct
    public void init() {
        blackbird.init();

        //initial set of devics
        devices = new HashMap<>();
        deviceIDs = new HashMap<>();

        blackbird.getDevices().forEach(this::addDevice); //TODO continous

        dataProviders.sort((o1, o2) -> {
            Class<?> deviceType1 = o1.getDeviceType();
            Class<?> deviceType2 = o2.getDeviceType();

            Class<?> implType1 = o1.getImplementationType();
            Class<?> implType2 = o2.getImplementationType();

            if (deviceType1.isAssignableFrom(deviceType2))
                return -1;
            else if (deviceType2.isAssignableFrom(deviceType1))
                return 1;
            else if (implType1.isAssignableFrom(implType2))
                return -1;
            else if (implType2.isAssignableFrom(implType1))
                return 1;
            else
                return 0;
        });
    }

}
