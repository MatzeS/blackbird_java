package blackbird.spring.stomp;

import blackbird.spring.DeviceController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Map;

@org.springframework.stereotype.Controller
public class Controller {

    @Autowired
    protected SimpMessagingTemplate template;

    @Autowired
    DeviceController deviceController;

    public void emitDeviceData(Map<String, Object> data) {
        this.template.convertAndSend("/device/data", data);
    }


    public void emitDeviceData(int deviceID) {
        emitDeviceData(deviceController.getDeviceData(deviceID));
    }

    @MessageMapping("/requestAll")
    public void requestAll() {
        deviceController.getDeviceIDs().values().forEach(this::emitDeviceData);
    }

}