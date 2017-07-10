package blackbird.spring.impl;

import blackbird.core.Blackbird;
import blackbird.core.exception.ImplementationFailedException;
import blackbird.core.device.Socket;
import blackbird.spring.DeviceController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@RestController
@RequestMapping("/device/remoteSocket/")
public class RemoteSocketController {

    Blackbird blackbird = Blackbird.getInstance();

    @Autowired
    blackbird.spring.stomp.Controller stompController;

    @Autowired
    DeviceController deviceController;

    SocketStateListener socketStateListener;

    @PostConstruct
    public void init() {
        socketStateListener = new SocketStateListener();

        //TODO
        deviceController.getDevices().values().stream().filter(d -> d instanceof Socket).forEach(d -> {
            Socket.Interface socketInterface;
            try {
                socketInterface = blackbird.interfaceDevice(d, Socket.Interface.class);
                socketInterface.addListener(socketStateListener);
            } catch (ImplementationFailedException e) {
                //TODO
                System.out.println("could not implement " + d);
                //e.printStackTrace();
            }
        });
    }

    @RequestMapping("/switch/{deviceID}/{state}")
    public synchronized void rcswitch(@PathVariable int deviceID, @PathVariable boolean state) {
        try {
            Socket device = (Socket) deviceController.getDevices().get(deviceID);
            Socket.Interface socket =
                    blackbird.interfaceDevice(device, Socket.Interface.class);
            socket.toggle();
        } catch (ImplementationFailedException e) {
            //TODO
            e.printStackTrace();
        }
    }

    public class SocketStateListener implements Socket.Listener {

        @Override
        public void stateChanged(Socket socket, int toggleState) {
            int deviceID = deviceController.getDeviceIDs().get(socket);
            stompController.emitDeviceData(deviceID);
        }

    }

}
