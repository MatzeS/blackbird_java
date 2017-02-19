package blackbird.core.avr.i2c;

import blackbird.core.avr.CommandBytes;
import blackbird.core.avr.packets.TransmittableAVRPacket;

import java.io.ByteArrayOutputStream;

public class I2CReadQuery extends TransmittableAVRPacket {

    private int slaveAddress;
    private byte registerAddress;
    private int count;

    public I2CReadQuery(int slaveAddress, byte registerAddress, int count) {
        super(CommandBytes.I2C);

        this.slaveAddress = slaveAddress;
        this.registerAddress = registerAddress;
        this.count = count;
    }

    @Override
    public void composePacket(ByteArrayOutputStream outputStream) {

        byte flagByte = 0x00;
        flagByte |= 0x40; // read flag

        outputStream.write(flagByte);
        outputStream.write(slaveAddress);
        outputStream.write(registerAddress);
        outputStream.write(count);

    }

}
