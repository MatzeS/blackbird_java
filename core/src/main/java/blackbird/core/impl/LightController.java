package blackbird.core.impl;

import blackbird.core.ComponentDIBuilder;
import blackbird.core.ComponentImplementation;
import blackbird.core.DInterface;
import blackbird.core.serial.SerialConnection;
import blackbird.core.util.Color;

import java.io.IOException;

public class LightController extends SerialDevice {

    private static final long serialVersionUID = 7937368501061607511L;

    public LightController(String name) {
        super(name);
    }


    public interface Interface extends DInterface {

        @Override
        LightController getDevice();

        void setPixels(byte stripIdentifier, int[] color) throws IOException;

    }

    public static class Implementation
            extends ComponentImplementation<LightController, SerialDevice.Implementation> implements Interface {

        private SerialConnection serialConnection;

        public Implementation(SerialDevice.Implementation component) {
            super(component);
            this.serialConnection = component.getSerialConnection();
        }

        @Override
        public synchronized void setPixels(byte stripIdentifier, int[] color) throws IOException {
            byte[] data = new byte[color.length * 3 + 1];
            data[0] = stripIdentifier;
            for (int i = 0; i < color.length; i++) {
                data[i * 3 + 1] = (byte) Color.red(color[i]);
                data[i * 3 + 2] = (byte) Color.green(color[i]);
                data[i * 3 + 3] = (byte) Color.blue(color[i]);
            }
            serialConnection.getOutputStream().write(data);
            serialConnection.getOutputStream().flush();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public static class Builder extends ComponentDIBuilder<LightController, Implementation, Port, SerialDevice.Implementation> {

            @Override
            public Implementation build(LightController device, Port port, SerialDevice.Implementation componentInterface) {
                return new Implementation(componentInterface);
            }
        }

    }

}
