package blackbird.core.device;

import java.io.IOException;

import blackbird.core.DIState;
import blackbird.core.DImplementation;
import blackbird.core.DInterface;
import blackbird.core.Device;
import blackbird.core.connection.serial.SerialConnection;

public class SerialDevice extends Device {

    private static final long serialVersionUID = 8876275800210193497L;

    public interface Interface extends DInterface {

    }

    public static class Implementation
            extends DImplementation<SerialDevice> implements Interface {

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

}
