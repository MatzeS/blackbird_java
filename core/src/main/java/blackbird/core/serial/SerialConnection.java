package blackbird.core.serial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import blackbird.core.Connection;

/**
 * A serial connection using a the {@link SerialPort} interface.
 */
public class SerialConnection extends Connection {

    private SerialPort serialPort;

    public SerialConnection(SerialPort serialPort) {
        this.serialPort = serialPort;
    }

    @Override
    public void close() throws IOException {
        serialPort.close();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return serialPort.getInputStream();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return serialPort.getOutputStream();
    }

    public SerialPort getSerialPort() {
        return serialPort;
    }

}

