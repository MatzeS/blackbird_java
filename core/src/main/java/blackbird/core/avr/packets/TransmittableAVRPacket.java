package blackbird.core.avr.packets;

import blackbird.core.avr.CommandBytes;

import java.io.ByteArrayOutputStream;

public abstract class TransmittableAVRPacket extends AVRPacket {

    private static final long serialVersionUID = -4559835212012670870L;

    private byte commandByte;

    public TransmittableAVRPacket(CommandBytes commandByte) {
        this(commandByte.getByte());
    }

    public TransmittableAVRPacket(byte commandByte) {
        this.commandByte = commandByte;
    }

    public TransmittableAVRPacket(int commandByte) {
        this((byte) commandByte);
    }

    public abstract void composePacket(ByteArrayOutputStream out);

    public Byte getCommandByte() {
        return commandByte;
    }

    public byte[] toByteArray() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(16);
        composePacket(outputStream);
        return outputStream.toByteArray();
    }

}
