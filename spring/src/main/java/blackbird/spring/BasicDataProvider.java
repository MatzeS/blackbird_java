package blackbird.spring;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import blackbird.core.Device;
import blackbird.core.ports.ParentDevicePort;
import blackbird.spring.util.ClassUtil;

public class BasicDataProvider extends DataProvider<Device, Object> {

    @Autowired
    DeviceController deviceController;

    @Override
    public void populateWithDevice(Map<String, Object> rootData, Map<String, Object> data, Device device) {

        data.put("name", device.getName());
        data.put("token", device.getToken());

        Class<?> outerClass = device.getClass();
        while (outerClass.getDeclaringClass() != null)
            outerClass = outerClass.getDeclaringClass();

        String type = outerClass.getSimpleName();
        if (type.trim().length() == 0)
            type = outerClass.getName();

        data.put("type", type);

        data.put("typeList",
                ClassUtil.getSuperclasses(device.getClass())
                        .stream().map(Class::getSimpleName)
                        .collect(Collectors.toList()));

        data.put("iconName", "ic_integrated_circuit");


        List<Integer> trail = new ArrayList<>();
        Device item = device;
        while (item.getPort() instanceof ParentDevicePort) {
            trail.add(deviceController.getDeviceIDs().get(item));
            item = ((ParentDevicePort) item.getPort()).getParentDevice();
        }
        trail.add(deviceController.getDeviceIDs().get(item));

        data.put("trail", trail);
        data.put("trailText", trail.stream().map(id -> deviceController.getDevices().get(id).getToken()).collect(Collectors.joining(" / ")));

        @SuppressWarnings("unchecked")
        Map<String, Object> ui = (Map<String, Object>) rootData.get("UI");

        if (ui.get("show") == null)
            if (!device.getClass().getName().contains("$"))
                //filter inner classes (only parent devices)
                ui.put("show", true);

    }

    @Override
    public void populateWithImplementation(Map<String, Object> rootData, Map<String, Object> data, Device device, Object implementation) {

        data.put("state", "AVAILABLE");
        data.put("stateIcon", "sign-check");
        //data.put("stateNotice", stateNotice);

    }

    @Override
    public void populateWithImplementationException(Map<String, Object> rootData, Map<String, Object> data, Device device, Exception exception) {

        data.put("state", "UNAVAILABLE");
        data.put("stateIcon", "sign-error");
        data.put("stateNotice", exception.toString());

    }

}
