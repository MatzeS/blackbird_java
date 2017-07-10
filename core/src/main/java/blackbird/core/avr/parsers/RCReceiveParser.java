package blackbird.core.avr.parsers;

import blackbird.core.connection.Packet;
import blackbird.core.avr.CommandBytes;
import blackbird.core.avr.packets.RCReceiveResponse;
import blackbird.core.device.avr.RCReceiver;

import static blackbird.core.avr.ByteHelper.decode2Byte;
import static blackbird.core.avr.ByteHelper.decode4Byte;

public class RCReceiveParser extends PacketParser {

    public RCReceiveParser() {
        super(CommandBytes.RC_SWITCH);
    }

    @Override
    public Packet parse(byte[] data) {
        // FLAG UNUSED

        return new RCReceiveResponse(new RCReceiver.Code(
                decode4Byte(data, 1),
                decode2Byte(data, 5),
                decode2Byte(data, 7)
        ));
    }

}
