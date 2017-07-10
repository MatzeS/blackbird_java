package blackbird.core.avr;

import java.io.IOException;

import blackbird.core.connection.CloseReason;
import blackbird.core.connection.serial.SerialConnection;
import blackbird.core.connection.serial.SerialPort;

public class SerialAVRConnection extends AVRConnection implements SerialPort.Listener {

    public SerialAVRConnection(SerialConnection connection) throws IOException {
        super(connection);
        connection.getSerialPort().addListener(this);
    }

    @Override
    public void closed(CloseReason closeReason) {
        //TODO
    }

    @Override
    public void dataAvailable() {
        consumeInputStream();
    }

}
