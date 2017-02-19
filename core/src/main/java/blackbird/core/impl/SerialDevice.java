package blackbird.core.impl;

import java.io.IOException;

import blackbird.core.ComponentImplementation;
import blackbird.core.DIState;
import blackbird.core.DInterface;
import blackbird.core.DPort;
import blackbird.core.Device;
import blackbird.core.HostDevice;
import blackbird.core.ports.LocalHostDevicePort;
import blackbird.core.serial.SerialConnection;

public class SerialDevice extends Device {

    private static final long serialVersionUID = 8876275800210193497L;

    public SerialDevice(String name) {
        super(name);
    }

    public interface Interface extends DInterface {

        @Override
        SerialDevice getDevice();

    }

    public static class Implementation
            extends ComponentImplementation<SerialDevice, DInterface> implements Interface {

        private SerialConnection serialConnection;

        public Implementation(DInterface component, SerialConnection serialConnection) {
            super(component);
            this.serialConnection = serialConnection;
        }

        @Override
        public DIState destroy() {
            try {
                serialConnection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        public SerialConnection getSerialConnection() {
            return serialConnection;
        }

        @Override
        public void loadState(DIState state) {
        }

    }

    public static class Port extends DPort {

        private static final long serialVersionUID = -9135816665280984514L;

        private String port;
        private int baudRate;

        public Port(String port, int baudRate) {
            this.port = port;
            this.baudRate = baudRate;
        }

        public int getBaudRate() {
            return baudRate;
        }

        public String getPort() {
            return port;
        }

        public static DPort create(HostDevice host, String port, int baudRate) {
            return new LocalHostDevicePort(host, new Port(port, baudRate));
        }

    }

}
