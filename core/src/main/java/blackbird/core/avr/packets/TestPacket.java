package blackbird.core.avr.packets;

import java.io.ByteArrayOutputStream;

public class TestPacket extends TransmittableAVRPacket {

    public TestPacket() {
        super(0x01);
    }

    @Override
    public void composePacket(ByteArrayOutputStream outputStream) {

        for (int i = 0; i < 255; i++)
            if (i % 2 == 0)
                outputStream.write(0xFF);
            else
                outputStream.write(0x00);

    }
}
