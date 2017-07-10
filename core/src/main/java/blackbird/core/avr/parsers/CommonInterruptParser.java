package blackbird.core.avr.parsers;

import blackbird.core.connection.Packet;
import blackbird.core.avr.CommandBytes;
import blackbird.core.avr.packets.CommonInterruptPacket;

public class CommonInterruptParser extends PacketParser {

    public CommonInterruptParser() {
        super(CommandBytes.COMMON_INTERRUPT);
    }

    @Override
    public Packet parse(byte[] data) {
        return new CommonInterruptPacket();
    }

}
