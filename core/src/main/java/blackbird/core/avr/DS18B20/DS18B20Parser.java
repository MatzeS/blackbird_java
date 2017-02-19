package blackbird.core.avr.DS18B20;

import blackbird.core.Packet;
import blackbird.core.avr.ByteHelper;
import blackbird.core.avr.CommandBytes;
import blackbird.core.avr.parsers.PacketParser;

public class DS18B20Parser extends PacketParser {

    public DS18B20Parser() {
        super(CommandBytes.DS18B20);
    }

    @Override
    public Packet parse(byte[] data) {

        byte flag = data[0];
        long address = ByteHelper.decode8Byte(data, 1);

        if (flag == 10) {

            float temp = (float) (ByteHelper.decode4Byte(data, 9) / 10e3);

            return new DS18B20ReadResponse(address, temp);

        }

        return null;
    }

}
