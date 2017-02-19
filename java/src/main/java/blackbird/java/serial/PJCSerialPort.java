package blackbird.java.serial;

import blackbird.core.CloseReason;
import blackbird.core.serial.AbstractSerialPort;
import purejavacomm.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.TooManyListenersException;

public class PJCSerialPort extends AbstractSerialPort implements SerialPortEventListener {

    private SerialPort serialPort;

    public PJCSerialPort() {
    }

    @Override
    public void close() {
        serialPort.removeEventListener();
        serialPort.close();
        listeners.fire(l -> l.closed(CloseReason.INTENTIONALLY));
    }

    public void connect(String port, int baudRate, int timeout) throws IOException {
        try {
            connect(CommPortIdentifier.getPortIdentifier(port), baudRate, timeout);
        } catch (NoSuchPortException e) {
            throw new IOException("No such port", e);
        }
    }

    public void connect(CommPortIdentifier portIdentifier, int baudRate, int timeout) throws IOException {
        if (portIdentifier.isCurrentlyOwned())
            throw new IOException(
                    portIdentifier.getName() + " is currently in use (" + portIdentifier.getCurrentOwner() + ")");

        if (portIdentifier.getPortType() != CommPortIdentifier.PORT_SERIAL)
            throw new IOException(portIdentifier.getName() + " is not a serial port");

        try {
            serialPort = (SerialPort) portIdentifier.open("blackbird", timeout);
            serialPort.setSerialPortParams(
                    baudRate,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            serialPort.notifyOnDataAvailable(true);
            serialPort.addEventListener(this);
        } catch (PortInUseException | TooManyListenersException | UnsupportedCommOperationException e) {
            throw new IOException(e);
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return serialPort.getInputStream();
    }

    @Override
    public String getName() {
        return serialPort.getName();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return serialPort.getOutputStream();
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE)
            fireDataAvailable();
        else
            System.err.println("other serial event");
    }

    public static void scan() {
        Enumeration<CommPortIdentifier> identifierEnumeration = CommPortIdentifier.getPortIdentifiers();
        while (identifierEnumeration.hasMoreElements()) {
            CommPortIdentifier identifier = identifierEnumeration.nextElement();
            System.out.println(identifier.getName() + "/" + identifier.getCurrentOwner());
        }
    }


}
