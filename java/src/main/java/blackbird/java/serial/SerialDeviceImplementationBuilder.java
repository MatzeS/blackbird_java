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
        try {
            SerialConnection serialConnection = new SerialConnection(getSerialPort(port.getPort(), port.getBaudRate()));

            return new SerialDevice.Implementation(componentInterface, serialConnection);
        } catch (IOException e) {
            throw new ImplementationFailedException("error todo, ioException", e);
        }
    }

    private static Connection connect(SerialDevice.Port devicePort) throws NoConnectionException {
        try {
            SerialPort serialPort = getSerialPort(devicePort.getPort(), devicePort.getBaudRate());
            return new SerialConnection(serialPort);
        } catch (IOException e) {
            throw new NoConnectionException("could not establish serial port", e);
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

