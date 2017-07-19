package blackbird.core.device;

import java.io.IOException;

import blackbird.core.DIState;
import blackbird.core.DImplementation;
import blackbird.core.DInterface;
import blackbird.core.Device;
import blackbird.core.HostDevice;
import blackbird.core.connection.serial.SerialConnection;

public class SerialDevice extends Device {

    private static final long serialVersionUID = 8876275800210193497L;

    private Port port;

    public Port getPort() {
        return port;
    }

    public void setPort(Port port) {
        this.port = port;
    }

    public interface Interface extends DInterface {

    }

    public static class Implementation
            extends DImplementation implements Interface {

        private SerialConnection serialConnection;

        public Implementation(SerialConnection serialConnection) {
            this.serialConnection = serialConnection;
        }

        //TODO destroy
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

    }


    public static class Port {

        private static final long serialVersionUID = -9135816665280984514L;

        private HostDevice host;
        private String port;
        private int baudRate;

        public Port(HostDevice host, String port, int baudRate) {
            this.host = host;
            this.port = port;
            this.baudRate = baudRate;
        }

        public int getBaudRate() {
            return baudRate;
        }

        public HostDevice getHost() {
            return host;
        }

        public String getPort() {
            return port;
        }

    }

}
