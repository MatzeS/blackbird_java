package blackbird.java.serial;

import java.io.IOException;

import blackbird.core.ComponentDIBuilder;
import blackbird.core.Connection;
import blackbird.core.DInterface;
import blackbird.core.exception.ImplementationFailedException;
import blackbird.core.exception.NoConnectionException;
import blackbird.core.impl.SerialDevice;
import blackbird.core.serial.SerialConnection;
import blackbird.core.serial.SerialPort;

public class SerialDeviceImplementationBuilder
        extends ComponentDIBuilder<SerialDevice, SerialDevice.Implementation, SerialDevice.Port, DInterface> {

    @Override
    public SerialDevice.Implementation build(SerialDevice device, SerialDevice.Port port, DInterface componentInterface) {
        if(!blackbird.getLocalDevice().equals(port.getHost()))
            throw new ImplementationFailedException("serail device is not attached to this device");

        try {
            SerialConnection serialConnection = new SerialConnection(getSerialPort(port.getPort(), port.getBaudRate()));

            Thread.sleep(2000);
            return new SerialDevice.Implementation(componentInterface, serialConnection);
        } catch (IOException e) {
            throw new ImplementationFailedException("error todo, ioException", e);
        } catch (InterruptedException e) {
            throw new ImplementationFailedException("interrupted");
        }
    }

        private static SerialPort getSerialPort(String portIdentifier, int baudRate) throws IOException {
        //RXTXSerialPort port = new RXTXSerialPort();
        PJCSerialPort port = new PJCSerialPort();
        port.connect(portIdentifier, baudRate, 2000);
        return port;
    }

    public static void scan() {
        PJCSerialPort.scan();
    }

}

