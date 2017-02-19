package blackbird.core.avr.packets;

import blackbird.core.avr.CommandBytes;

import java.io.ByteArrayOutputStream;

public class RCSwitchQuery extends TransmittableAVRPacket {

    int address;
    boolean on;

    public RCSwitchQuery(int address, boolean on) {
        super(CommandBytes.RC_SWITCH);
        this.address = address;
        this.on = on;
    }


    @Override
    public void composePacket(ByteArrayOutputStream outputStream) {

        byte groupByte = 0x00;
        byte socketByte = 0x00;

        for (int i = 0; i < 5; i++) {
            if ((address & (1L << 5 + i)) != 0)
                groupByte |= 1 << i;
            if ((address & (1L << i)) != 0)
                socketByte |= 1 << i;
        }

        if (on)
            groupByte |= 1 << 7;

        byte flag = 10;
        outputStream.write(flag); // switch flag
        outputStream.write(groupByte);
        outputStream.write(socketByte);

    }

}
