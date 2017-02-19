package blackbird.core.avr.parsers;

import blackbird.core.Packet;
import blackbird.core.avr.CommandBytes;

public abstract class PacketParser {

    private byte commandByte;

    public PacketParser(byte commandByte) {
        this.commandByte = commandByte;
    }

    public PacketParser(CommandBytes commandByte) {
        this(commandByte.getByte());
    }

    public Byte getCommandByte() {
        return commandByte;
    }

    public abstract Packet parse(byte[] data);


}
