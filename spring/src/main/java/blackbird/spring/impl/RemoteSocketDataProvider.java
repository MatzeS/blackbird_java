package blackbird.spring.impl;

import blackbird.core.impl.Socket;
import blackbird.spring.DataProvider;

import java.util.Map;

public class RemoteSocketDataProvider extends DataProvider<Socket, Socket.Interface> {

    @Override
    public void populateWithDevice(Map<String, Object> rootData, Map<String, Object> data, Socket device) {
    }

    @Override
    public void populateWithImplementation(Map<String, Object> rootData, Map<String, Object> data, Socket device, Socket.Interface implementation) {
        data.put("state", implementation.getToggleState() == Socket.ON);
    }

    @Override
    public void populateWithImplementationException(Map<String, Object> rootData, Map<String, Object> data, Socket device, Exception exception) {

    }

}