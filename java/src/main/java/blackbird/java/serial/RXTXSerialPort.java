package blackbird.java.serial;

import blackbird.core.CloseReason;
import blackbird.core.serial.AbstractSerialPort;
import gnu.io.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.TooManyListenersException;

public class RXTXSerialPort extends AbstractSerialPort implements gnu.io.SerialPortEventListener {

    private gnu.io.SerialPort serialPort;

    public RXTXSerialPort() {
    }

    @Override
    public void close() {
        serialPort.removeEventListener();
        serialPort.close();
        listeners.fire(l -> l.closed(CloseReason.INTENTIONALLY));
    }

    public void connect(String port, int baudRate, int timeout) throws IOException {
        scan();
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

        if (portIdentifier.getPortType() != purejavacomm.CommPortIdentifier.PORT_SERIAL)
            throw new IOException(portIdentifier.getName() + " is not a serial port");

        try {
            CommPort commPort = portIdentifier.open("blackbird", timeout);
            serialPort = (gnu.io.SerialPort) commPort;
            serialPort.setSerialPortParams(
                    baudRate,
                    gnu.io.SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
        } catch (UnsupportedCommOperationException | PortInUseException | TooManyListenersException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
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
            System.out.println("other event");
    }

    public static void scan() {

        Enumeration<CommPortIdentifier> identifierEnumeration = CommPortIdentifier.getPortIdentifiers();
        while (identifierEnumeration.hasMoreElements()) {
            CommPortIdentifier identifier = identifierEnumeration.nextElement();
            System.out.println("++++++++++++" + identifier.getName() + "/" + identifier.getCurrentOwner());
        }

    }

}
