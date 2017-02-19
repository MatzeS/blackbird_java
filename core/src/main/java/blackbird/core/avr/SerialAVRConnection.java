package blackbird.core.avr;

import blackbird.core.CloseReason;
import blackbird.core.serial.SerialConnection;
import blackbird.core.serial.SerialPort;

import java.io.IOException;

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
