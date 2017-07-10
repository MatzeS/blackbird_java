package blackbird.core.avr.parsers;

import blackbird.core.connection.Packet;
import blackbird.core.avr.ByteHelper;
import blackbird.core.avr.CommandBytes;
import blackbird.core.avr.packets.IRReceiveResponse;

public class IRParser extends PacketParser {

    public IRParser() {
        super(CommandBytes.IR);
    }

    @Override
    public Packet parse(byte[] data) {
        if (data[0] == 10)
            return new IRReceiveResponse(ByteHelper.decode4Byte(data, 1));
        else
            throw new UnsupportedOperationException("invalid flag received");
    }

}
