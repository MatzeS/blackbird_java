package blackbird.core.avr.DS18B20;

import java.io.ByteArrayOutputStream;

import blackbird.core.avr.CommandBytes;
import blackbird.core.avr.packets.TransmittableAVRPacket;

import static blackbird.core.avr.ByteHelper.encode;

public class DS18B20ReadQuery extends TransmittableAVRPacket {

    private long address;

    public DS18B20ReadQuery(long address) {
        super(CommandBytes.DS18B20);
        this.address = address;
    }

    @Override
    public void composePacket(ByteArrayOutputStream out) {
        out.write(10);
        out.write(encode(address), 0, 8);
    }

}
