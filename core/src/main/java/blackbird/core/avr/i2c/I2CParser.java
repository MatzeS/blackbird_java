package blackbird.core.avr.i2c;

import blackbird.core.Packet;
import blackbird.core.avr.CommandBytes;
import blackbird.core.avr.parsers.PacketParser;

public class I2CParser extends PacketParser {

    public I2CParser() {
        super(CommandBytes.I2C);
    }

    @Override
    public Packet parse(byte[] data) {

        //byte flag = data[0];

        int slaveAddress = data[1];
        byte registerAddress = data[2];
        byte[] readData = new byte[data.length - 3];
        System.arraycopy(data, 3, readData, 0, readData.length);

        return new I2CReadResponse(slaveAddress, registerAddress, readData);

    }

}
