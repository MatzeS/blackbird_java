package blackbird.java.serial;

import java.io.IOException;

import blackbird.core.builders.GenericBuilder;
import blackbird.core.connection.serial.SerialConnection;
import blackbird.core.connection.serial.SerialPort;
import blackbird.core.device.SerialDevice;
import blackbird.core.device.SerialDevice.Implementation;
import blackbird.core.device.SerialDevice.Port;
import blackbird.core.exception.BFException;
import blackbird.core.exception.OtherHostException;

public class SerialDeviceImplementationBuilder
        extends GenericBuilder<SerialDevice, Implementation> {

    @Override
    public Implementation buildGeneric(SerialDevice device) {
        Port port = device.getPort();

        if (!getLocalDevice().equals(port.getHost()))
            throw new OtherHostException(port.getHost());

        try {

            SerialConnection serialConnection =
                    new SerialConnection(getSerialPort(port.getPort(), port.getBaudRate()));
            Thread.sleep(2000);
            return new SerialDevice.Implementation(serialConnection);

        } catch (InterruptedException | IOException e) {
            throw new BFException(e);
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

