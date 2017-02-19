package blackbird.core.avr.parsers;

import blackbird.core.Packet;
import blackbird.core.avr.CommandBytes;
import blackbird.core.avr.packets.CapSensePinResponse;

public class CapSensePinParser extends PacketParser {

    public CapSensePinParser() {
        super(CommandBytes.CAP_SENSE);
    }

    @Override
    public Packet parse(byte[] data) {
        int pin = data[0];
        boolean pressed = data[1] == (byte) 0xFF;
        return new CapSensePinResponse(pin, pressed);
    }

}
